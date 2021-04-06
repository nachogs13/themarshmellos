package com.muei.apm.fasterwho

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.muei.apm.fasterwho.dummy.DummyContent.DummyItem

/**
 * [RecyclerView.Adapter] that can display a [DummyItem].
 * TODO: Replace the implementation with code for your data type.
 */
/**
 * [RecyclerView.Adapter] that can display a [DummyItem].
 * TODO: Replace the implementation with code for your data type.
 */
class MyMiRutaRecyclerViewAdapter(
    private val values: List<DummyItem>
) : RecyclerView.Adapter<MyMiRutaRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_mis_rutas, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.nombreView.text = "Ruta " + item.id
        holder.direccionView.text = "Dirección de ruta " + item.id
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val nombreView: TextView = view.findViewById(R.id.mi_ruta_nombre)
        val direccionView: TextView = view.findViewById(R.id.mi_ruta_direccion)

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            v.context.startActivity(Intent(v.context, RutaActivity::class.java))
        }
    }
}