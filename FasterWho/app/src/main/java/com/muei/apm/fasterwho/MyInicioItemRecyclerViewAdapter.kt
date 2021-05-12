package com.muei.apm.fasterwho

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.muei.apm.fasterwho.dummy.DummyContent.DummyItem


/**
 * [RecyclerView.Adapter] that can display a [DummyItem].
 * TODO: Replace the implementation with code for your data type.
 */
/**
 * [RecyclerView.Adapter] that can display a [DummyItem].
 * TODO: Replace the implementation with code for your data type.
 */
class MyInicioItemRecyclerViewAdapter(
    private val values: List<ItemRuta>)
    : RecyclerView.Adapter<MyInicioItemRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.d("VALUES",values.toString())
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_inicio_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = values[position]
        holder.mainNombreView.text = item.nombreRuta
        holder.mainDirView.text = item.direccionRuta
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val mainNombreView: TextView = view.findViewById(R.id.main_ruta_nombre)
        val mainDirView: TextView = view.findViewById(R.id.main_ruta_direccion)

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            val pos = adapterPosition
            val intent = Intent(v.context, RutaActivity::class.java)
            val coords_fin_latitud = values[pos].coordenadasFinRuta?.latitude
            val coords_fin_longitud = values[pos].coordenadasFinRuta?.longitude
            val coords_ini_latitud = values[pos].coordenadasInicioRuta?.latitude
            val coords_ini_longitud = values[pos].coordenadasInicioRuta?.longitude
            val file = values[pos].file

            intent.putExtra("latitud_fin", coords_fin_latitud)
            intent.putExtra("longitud_fin", coords_fin_longitud)
            intent.putExtra("latitud_ini", coords_ini_latitud)
            intent.putExtra("longitud_ini", coords_ini_longitud)
            intent.putExtra("file", file?.id)
            v.context.startActivity(intent)
        }
    }
}
