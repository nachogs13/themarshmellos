package com.muei.apm.fasterwho

import android.content.Context
import android.content.Intent
import android.icu.number.NumberFormatter.with
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.muei.apm.fasterwho.dummy.DummyContent.DummyItem
import com.bumptech.glide.Glide.with as with


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

    private val db = FirebaseFirestore.getInstance()

    private var listItem : ArrayList<ItemRuta> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.d("VALUES",values.toString())
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
        var img = storage.getReference(item.img?.path.toString())
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
            val coords_fin_latitud = values[pos].coordenadasFinRuta?.latitude
            val coords_fin_longitud = values[pos].coordenadasFinRuta?.longitude
            val coords_ini_latitud = values[pos].coordenadasInicioRuta?.latitude
            val coords_ini_longitud = values[pos].coordenadasInicioRuta?.longitude
            val distancia = values[pos].distancia
            val desnivel = values[pos].desnivel
            val file = values[pos].file

            intent.putExtra("distancia", distancia)
            intent.putExtra("desnivel", desnivel)
            intent.putExtra("latitud_fin", coords_fin_latitud)
            intent.putExtra("longitud_fin", coords_fin_longitud)
            intent.putExtra("latitud_ini", coords_ini_latitud)
            intent.putExtra("longitud_ini", coords_ini_longitud)
            intent.putExtra("file", file?.id)
            intent.putExtra("nombre", values[pos].nombreRuta)
            v.context.startActivity(intent)
        }
    }
}