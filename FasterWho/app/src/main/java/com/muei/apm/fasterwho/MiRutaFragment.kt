package com.muei.apm.fasterwho

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.muei.apm.fasterwho.dummy.DummyContent

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A fragment representing a list of Items.
 */
class MiRutaFragment : Fragment() {

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
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_mis_rutas_list, container, false)
        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                db.collection("rutas")
                        .whereEqualTo("usuario",FirebaseAuth.getInstance().currentUser.email.toString())
                        .get().addOnSuccessListener {

                    for (document in it) {
                        direccionRuta = document.data.get("direccion").toString()
                        nombreRuta = document.data.get("nombre").toString()
                        rating = document.data.get("rating") as Number
                        coordenadasFin = document.data.get("coordenadas_fin") as GeoPoint
                        coordenadasInicio = document.data.get("coordenadas_inicio") as GeoPoint
                        val public = document.data.get("public") as Boolean
                        file = document.data.get("kml") as String
                        val dist = document.data.get("distancia") as Number
                        val desnivel = document.data.get("desnivel") as Number
                        var img = document.data.get("imgInicio") as DocumentReference
                        val id = document.id as String

                        listItem.add(ItemRuta(nombreRuta,direccionRuta,coordenadasInicio,
                                coordenadasFin,rating,file,img, dist, desnivel, public,id))
                    }
                        adapter = MyMiRutaRecyclerViewAdapter(listItem)

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
            MiRutaFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }

}