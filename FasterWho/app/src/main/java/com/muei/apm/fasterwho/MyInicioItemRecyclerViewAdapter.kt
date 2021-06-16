package com.muei.apm.fasterwho

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage


class MyInicioItemRecyclerViewAdapter(
    private val values: List<ItemRuta>)
    : RecyclerView.Adapter<MyInicioItemRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_inicio_item, parent, false)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val storage = FirebaseStorage.getInstance()
        val item = values[position]

        holder.mainNombreView.text = item.nombreRuta
        holder.mainDirView.text = item.direccionRuta
        holder.mainRatingView.rating = item.rating!!.toFloat()
        val img = storage.getReference(item.img?.path.toString())
        GlideApp.with(holder.itemView.context).load(img).into(holder.mainImgRuta)

    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val mainNombreView: TextView = view.findViewById(R.id.main_ruta_nombre)
        val mainDirView: TextView = view.findViewById(R.id.main_ruta_direccion)
        val mainRatingView: RatingBar = view.findViewById(R.id.rtbHighScore)
        val mainImgRuta: ImageView = view.findViewById(R.id.ruta_image)

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            val pos = adapterPosition
            val intent = Intent(v.context, RutaActivity::class.java)
            val coordsFinLatitud = values[pos].coordenadasFinRuta?.latitude
            val coordsFinLongitud = values[pos].coordenadasFinRuta?.longitude
            val coordsIniLatitud = values[pos].coordenadasInicioRuta?.latitude
            val coordsIniLongitud = values[pos].coordenadasInicioRuta?.longitude
            val distancia = values[pos].distancia
            val desnivel = values[pos].desnivel
            val file = values[pos].file
            val rating = values[pos].rating

            intent.putExtra("distancia", distancia)
            intent.putExtra("desnivel", desnivel)
            intent.putExtra("latitud_fin", coordsFinLatitud)
            intent.putExtra("longitud_fin", coordsFinLongitud)
            intent.putExtra("latitud_ini", coordsIniLatitud)
            intent.putExtra("longitud_ini", coordsIniLongitud)
            intent.putExtra("file", file)
            intent.putExtra("nombre", values[pos].nombreRuta)
            intent.putExtra("rating", rating)
            v.context.startActivity(intent)
        }
    }

}