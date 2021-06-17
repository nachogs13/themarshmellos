package com.muei.apm.fasterwho

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.*
import java.util.*
import kotlin.collections.ArrayList

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
    private var latitudBuscada = 0F
    private var longitudBuscada = 0F
    private var tagIni = "InicioItemFragment"

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
        latitudBuscada = preferences.getFloat("LatitudBuscada", 0F)
        longitudBuscada = preferences.getFloat("LongitudBuscada", 0F)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_inicio_item_list, container, false)
        var filteredList: ArrayList<ItemRuta>

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                db.collection("rutas").whereEqualTo("public",true).get().addOnSuccessListener {

                    for (document in it) {
                        try {
                            direccionRuta = document.data["direccion"].toString()
                            nombreRuta = document.data["nombre"] as String
                            rating = document.data["rating"] as Number
                            coordenadasFin = document.data["coordenadas_fin"] as GeoPoint
                            coordenadasInicio = document.data["coordenadas_inicio"] as GeoPoint
                            file = document.data["kml"] as String
                            val dist = document.data["distancia"] as Number
                            val public = document.data["public"] as Boolean
                            val desnivel = document.data["desnivel"] as Number
                            val img = document.data["imgInicio"] as DocumentReference

                            listItem.add(ItemRuta(nombreRuta,direccionRuta,coordenadasInicio,
                                coordenadasFin,rating,file,img, dist, desnivel,public, document.id))
                        } catch (e: Exception) {
                            Log.d(tagIni, "Fallo al obtener la información de una ruta pública")
                        }
                    }
                    if (puntuacion!=0F || distancia!=0 || dificultad!=0F || longitudBuscada != 0F){
                        filteredList = filtrarLista()

                        if (filteredList.isNotEmpty()){
                            (activity as InicioActivity).sinResultados(isEmp = false)
                            adapter = MyInicioItemRecyclerViewAdapter(filteredList)
                        } else {
                            (activity as InicioActivity).sinResultados(isEmp = true)
                        }

                    } else {
                        if (listItem.isNotEmpty()){
                            (activity as InicioActivity).sinResultados(isEmp = false)
                            adapter = MyInicioItemRecyclerViewAdapter(listItem)
                        } else {
                            (activity as InicioActivity).sinResultados(isEmp = true)
                        }
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
            val (match, _) = listItem.partition {
                it.rating?.toFloat()  == puntuacion
            }
            filteredList = match as ArrayList<ItemRuta>
        }
        if(distancia!=0){
            if (filteredList.isNotEmpty()) listItem = filteredList
            val (match, _) = listItem.partition {
                it.distancia?.toFloat()!! >= distancia.toFloat() &&
                        it.distancia?.toFloat()!! < distancia.toFloat() + 1F
            }
            Log.d("match", match.toString())
            filteredList = match as ArrayList<ItemRuta>
        }
        if (dificultad!=0F){
            var nivelDificultad =""
            when(dificultad){
                1F -> nivelDificultad="Baja"
                2F -> nivelDificultad="Media"
                3F -> nivelDificultad="Alta"
            }
            if (filteredList.isNotEmpty()) listItem = filteredList
            val (match, _) = listItem.partition {
                val nivelDif = calcularDificultad(it.distancia!!.toFloat(),it.desnivel!!.toInt())
                nivelDif == nivelDificultad
            }
            filteredList = match as ArrayList<ItemRuta>
        }

        // filtramos por localidad
        if (latitudBuscada != 0F && longitudBuscada != 0F){
            val addresses: List<Address>?
            val geocoder = Geocoder(activity, Locale.getDefault())
            addresses = geocoder.getFromLocation(latitudBuscada.toDouble(), longitudBuscada.toDouble(),1)
            Log.i(tag, "Localidad a filtrar " + addresses[0].locality)
            val direccionRuta: String? = addresses[0].locality

            if (direccionRuta != null) {
                if (filteredList.isNotEmpty()) listItem = filteredList
                val (match, _) = listItem.partition {
                    it.coordenadasInicioRuta!!.latitude
                    direccionRuta == geocoder.getFromLocation(it.coordenadasInicioRuta!!.latitude, it.coordenadasInicioRuta!!.longitude,1)[0].locality
                }
                filteredList = match as ArrayList<ItemRuta>
            }
        }
        return filteredList
    }

    private fun calcularDificultad(dist: Float, desnivel: Int): String {
        val dificultad: String
        if (dist < 25F){
            dificultad = when(desnivel){
                in 1..599 -> "Baja"
                in 600..999 -> "Media"
                else -> "Alta"
            }
        }else if (dist >= 25F && dist < 40F){
            dificultad = when(desnivel){
                in 1..999 -> "Baja"
                in 1000..1499 -> "Media"
                else -> "Alta"
            }
        }else{
            dificultad = when(desnivel){
                in 1..1499 -> "Baja"
                in 1500..2499 -> "Media"
                else -> "Alta"
            }
        }
        return dificultad
    }

}