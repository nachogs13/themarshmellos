package com.muei.apm.fasterwho

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

/**
 * A fragment representing a list of Users
 */
class UsuariosRutaFragment: Fragment() {
    private var columnCount = 1
    private val db = FirebaseFirestore.getInstance()

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
        val view = inflater.inflate(R.layout.fragment_usuarios_ruta_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                val items : MutableList<ItemUsuarioRuta> = ArrayList()
                db.collection("rutasUsuarios").whereEqualTo("idRuta", arguments?.get("ruta").toString())
                    .get().addOnSuccessListener {
                        if (it.documents.isNotEmpty()) {
                            for (document in it) {
                                db.collection("usuarios").document(document.get("idUsuario").toString())
                                    .get().addOnSuccessListener { it2 ->
                                        var img: DocumentReference? = null
                                        if (it2.get("imgPerfil") != null) {
                                            img = it2.get("imgPerfil") as DocumentReference
                                        }
                                        items.add(
                                            ItemUsuarioRuta(
                                                img,
                                                it2.get("username").toString(),
                                                document.get("horas").toString().split(".")[0] + ":" +
                                                        document.get("minutos").toString().split(".")[0] + ":"
                                                        + document.get("segundos").toString().split(".")[0] + ":" +
                                                        document.get("milis").toString().split(".")[0]
                                            )
                                        )
                                        adapter = UsuariosRutaRecyclerViewAdapter(items)
                                }
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
        fun newInstance(rutaConcreta: String) =
                UsuariosRutaFragment().apply {
                    arguments = Bundle().apply {
                        putString("ruta", rutaConcreta)
                    }
                }
    }
}