package com.muei.apm.fasterwho

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage

class UsuariosRutaRecyclerViewAdapter(private val values: List<ItemUsuarioRuta>
) :RecyclerView.Adapter<UsuariosRutaRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuariosRutaRecyclerViewAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_usuarios_ruta_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: UsuariosRutaRecyclerViewAdapter.ViewHolder, position: Int) {
        val storage = FirebaseStorage.getInstance()

        val item = values[position]
        holder.userName.text = item.userName
        holder.userDuration.text = item.stadistic
        val img = storage.getReference(item.icon?.path.toString())
        GlideApp.with(holder.itemView.context).load(img).into(holder.icon)
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val userDuration: TextView = view.findViewById(R.id.user_duration)
        val userName: TextView = view.findViewById(R.id.user_name)
        val icon : ImageView = view.findViewById(R.id.user_image)

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            Toast.makeText(v.context, "Viendo el perfil del Amigo"+v.id.toString(), Toast.LENGTH_SHORT).show()
        }

    }
}