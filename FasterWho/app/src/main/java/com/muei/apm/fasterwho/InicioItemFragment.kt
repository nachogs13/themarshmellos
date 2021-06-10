package com.muei.apm.fasterwho

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.core.content.edit
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.*
import java.lang.ref.Reference

/**
 * A fragment representing a list of Items.
 */
class InicioItemFragment : Fragment() {

    private var columnCount = 1
    private val db = FirebaseFirestore.getInstance()
    private lateinit var direccionRuta : String
    private lateinit var nombreRuta : String
    private lateinit var coordenadasInicio : GeoPoint
    private lateinit var coordenadasFin : GeoPoint
    private lateinit var rating : Number
    private lateinit var file : String
    private var listItem : ArrayList<ItemRuta> = ArrayList()
    private var filteredList : ArrayList<ItemRuta> = ArrayList()
    private var puntuacion : Float = 0F
    private var dificultad : Float = 0F
    private var distancia : Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
                arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
        val preferences = this.activity?.getSharedPreferences(getString(
                R.string.preference_filtersActivity_key), Context.MODE_PRIVATE) ?: return
        puntuacion = preferences.getFloat(getString(R.string.puntuacion),0F)
        dificultad = preferences.getFloat(getString(R.string.nivel_de_dificultad),0F)
        distancia = preferences.getInt(getString(R.string.distancia),0)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_inicio_item_list, container, false)
        var filteredList = ArrayList<ItemRuta>()

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                db.collection("rutasPrivadas").whereEqualTo("public",true).get().addOnSuccessListener {

                    for (document in it) {
                        direccionRuta = document.data.get("direccion").toString()
                        nombreRuta = document.data.get("nombre") as String
                        rating = document.data.get("rating") as Number
                        coordenadasFin = document.data.get("coordenadas_fin") as GeoPoint
                        coordenadasInicio = document.data.get("coordenadas_inicio") as GeoPoint
                        file = document.data.get("kmlfile") as String
                        val dist = document.data.get("distancia") as Number
                        val public = document.data.get("public") as Boolean
                        val desnivel = document.data.get("desnivel") as Number
                        var img = document.data.get("imgInicio") as DocumentReference

                        listItem.add(ItemRuta(nombreRuta,direccionRuta,coordenadasInicio,
                                coordenadasFin,rating,file,img, dist, desnivel,public))
                    }
                    if (puntuacion!=0F || distancia!=0 || dificultad!=0F){
                        filteredList = filtrarLista()

                        if (!filteredList.isEmpty()){

                            adapter = MyInicioItemRecyclerViewAdapter(filteredList)
                        }

                    }else {
                        adapter = MyInicioItemRecyclerViewAdapter(listItem)
                    }

                }


            }
        }
        return view

    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            InicioItemFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }

    private fun filtrarLista(): ArrayList<ItemRuta> {
        if (puntuacion!=0F){
            val (match, noMatch) = listItem.partition {
                it.rating?.toFloat()  == puntuacion.toFloat()
            }
            filteredList = match as ArrayList<ItemRuta>
        }
        if(distancia!=0){
            if (!filteredList.isEmpty()) listItem = filteredList
            val (match, noMatch) = listItem.partition {
                it.distancia?.toFloat()!! >= distancia.toFloat() &&
                        it.distancia?.toFloat()!! < distancia.toFloat() + 1F
            }
            Log.d("match", match.toString())
            filteredList = match as ArrayList<ItemRuta>
        }
        if (dificultad!=0F){
            var nivelDificultad: String =""
            when(dificultad){
                1F -> nivelDificultad="Baja"
                2F -> nivelDificultad="Media"
                3F -> nivelDificultad="Alta"
            }
            if (!filteredList.isEmpty()) listItem = filteredList
            val (match, noMatch) = listItem.partition {
                val nivelDif = calcularDificultad(it.distancia!!.toFloat(),it.desnivel!!.toInt())
                nivelDif == nivelDificultad
            }
            filteredList = match as ArrayList<ItemRuta>
        }
        return filteredList
    }

    fun calcularDificultad(dist: Float, desnivel: Int): String {
        var dificultad: String =""
        if (dist < 25F){
            when(desnivel){
                in 1..599 -> dificultad="Baja"
                in 600..999 -> dificultad="Media"
                else ->dificultad="Alta"
            }
        }else if (dist >= 25F && dist < 40F){
            when(desnivel){
                in 1..999 -> dificultad="Baja"
                in 1000..1499 -> dificultad="Media"
                else ->dificultad="Alta"
            }
        }else{
            when(desnivel){
                in 1..1499 -> dificultad="Baja"
                in 1500..2499 -> dificultad="Media"
                else ->dificultad="Alta"
            }
        }
        return dificultad
    }

}