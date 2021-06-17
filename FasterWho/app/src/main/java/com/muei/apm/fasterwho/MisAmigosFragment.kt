package com.muei.apm.fasterwho

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore

/**
 * A fragment representing a list of Items.
 */
class MisAmigosFragment : Fragment() {

    private var columnCount = 1
    private val db = FirebaseFirestore.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()

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
        val view = inflater.inflate(R.layout.fragment_mis_amigos_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                db.collection("usuarios").document(firebaseAuth.currentUser!!.email!!.toString()).get().addOnSuccessListener {

                    if (it.get("amigos") != null) {
                        val amigos = it.get("amigos") as List<String>
                        val items: MutableList<ItemAmigo> = ArrayList()
                        db.collection("usuarios").whereIn(FieldPath.documentId(), amigos).get()
                            .addOnSuccessListener { it2->
                                for (document in it2) {
                                    var img: DocumentReference? = null
                                    if (document.get("imgPerfil") != null) {
                                        img = document.get("imgPerfil") as DocumentReference
                                    }
                                    items.add(
                                        ItemAmigo(
                                            document.id,
                                            document.get("username").toString(),
                                            img
                                        )
                                    )
                                }

                                (activity as MisAmigos).sinResultados(isEmp = false)
                                adapter = MyMisAmigosRecyclerViewAdapter(items)
                            }
                    } else {
                        (activity as MisAmigos).sinResultados(isEmp = true)
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
            MisAmigosFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}