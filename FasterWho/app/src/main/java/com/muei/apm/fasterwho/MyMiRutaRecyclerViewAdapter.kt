package com.muei.apm.fasterwho

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

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
        private val values: List<ItemRuta>
) : RecyclerView.Adapter<MyMiRutaRecyclerViewAdapter.ViewHolder>() {
    private val db = FirebaseFirestore.getInstance()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_mis_rutas, parent, false)
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
        if(item.publicRuta !=true){
            holder.mainLockRuta.visibility = View.VISIBLE

        }else{
            holder.mainUnlockRuta.visibility = View.VISIBLE

        }
        holder.mainLockRuta.setOnClickListener {
            //cambiarEstadoRuta(holder)
            holder.mainLockRuta.visibility = View.GONE
            holder.mainUnlockRuta.visibility = View.VISIBLE
            db.collection("rutas").document(item.id.toString()).update("public",true)
            Toast.makeText(holder.mainLockRuta.context, "Ruta pública/privada", Toast.LENGTH_SHORT).show()
        }
        holder.mainUnlockRuta.setOnClickListener {
            db.collection("rutas").document(item.id.toString()).update("public",false)
            holder.mainUnlockRuta.visibility = View.GONE
            holder.mainLockRuta.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val mainNombreView: TextView = view.findViewById(R.id.mi_ruta_nombre)
        val mainDirView: TextView = view.findViewById(R.id.mi_ruta_direccion)
        val mainRatingView: RatingBar = view.findViewById(R.id.mi_ruta_score)
        val mainImgRuta: ImageView = view.findViewById(R.id.mi_ruta_image)
        val mainLockRuta: ImageView = view.findViewById(R.id.image_lock_ruta)
        val mainUnlockRuta: ImageButton =  view.findViewById(R.id.image_unlock_ruta)

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