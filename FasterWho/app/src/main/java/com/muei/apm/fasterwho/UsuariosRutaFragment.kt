package com.muei.apm.fasterwho

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.muei.apm.fasterwho.dummy.DummyContent

/**
 * A fragment representing a list of Users
 */
class UsuariosRutaFragment: Fragment() {
    private var columnCount = 1

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
                adapter = UsuariosRutaRecyclerViewAdapter(setDataItem())
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
                UsuariosRutaFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_COLUMN_COUNT, columnCount)
                    }
                }
    }

    /*
     * Función para crear un array con información de usuarios
     */
    private fun setDataItem() : ArrayList<ItemUsuarioRuta> {
        var listItem : ArrayList<ItemUsuarioRuta> = ArrayList()

        listItem.add(ItemUsuarioRuta(R.drawable.avatar,"María","00:40"))
        listItem.add(ItemUsuarioRuta(R.drawable.avatar,"Elena","00:53"))
        listItem.add(ItemUsuarioRuta(R.drawable.fotoperfil2,"Pablo","01:03"))
        listItem.add(ItemUsuarioRuta(R.drawable.fotoperfil,"Andrés","00:33"))
        listItem.add(ItemUsuarioRuta(R.drawable.fotoperfil2,"Jaime","01:48"))

        return listItem
    }
}