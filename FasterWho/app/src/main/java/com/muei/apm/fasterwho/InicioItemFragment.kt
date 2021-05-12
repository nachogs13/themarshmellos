package com.muei.apm.fasterwho

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.QuerySnapshot
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
    private lateinit var file : DocumentReference
    private var listItem : ArrayList<ItemRuta> = ArrayList()
    private lateinit var rutas : Task<QuerySnapshot>
    private lateinit var docs : QuerySnapshot


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_inicio_item_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                db.collection("rutas").get().addOnSuccessListener {

                    for (document in it) {
                        direccionRuta = document.data.get("direccion").toString()
                        nombreRuta = document.data.get("nombre") as String
                        rating = document.data.get("rating") as Number
                        coordenadasFin = document.data.get("coordenadas_fin") as GeoPoint
                        coordenadasInicio = document.data.get("coordenadas_inicio") as GeoPoint
                        file = document.data.get("kmlfile") as DocumentReference
                        Log.d("file", document.data.get("kmlfile").toString())

                        listItem.add(ItemRuta(nombreRuta,direccionRuta,coordenadasInicio,coordenadasFin,rating,file))

                    }
                    adapter = MyInicioItemRecyclerViewAdapter(listItem)
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
    /*private suspend fun setDataItem() : ArrayList<ItemRuta> {
        /*val job = launch {
            // aquí irían as peticións de datos a Firebase
            println("Datos listos!")
        }
        println("Agardando por datos de Firebase...")
        job.join() // agardamos aquí ata que a corrutina finalice

        //aquí xa continuaría a listItem.add...*/

        val job = GlobalScope.launch(Dispatchers.Main) {
            rutas = db.collection("rutas").get()
            db.collection("rutas").get().addOnSuccessListener {

                for (document in it) {
                    Log.d("FIREBASE", "${document.id} => ${document.data} ${it}")
                    direccionRuta = document.data.get("direccion").toString()
                    nombreRuta = document.data.get("nombre") as String
                    rating = document.data.get("rating") as Number
                    coordenadasFin = document.data.get("coordenadas_fin") as GeoPoint
                    coordenadasInicio = document.data.get("coordenadas_inicio") as GeoPoint

                    listItem.add(ItemRuta(nombreRuta,direccionRuta,coordenadasInicio,coordenadasFin,rating))
                    println("Lista"+listItem)
                }
            }
            Log.d("Waiting","DATOS LISTOS!!")

        }

        Log.d("Waiting","Agardando por datos de Firebase...")
        job.join()

        listItem.add(ItemRuta("nombreRuta","direccionRuta",5))
        listItem.add(ItemRuta("nombreRuta","direccionRuta",5))

        return listItem
    }*/
}