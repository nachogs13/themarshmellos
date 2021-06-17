package com.muei.apm.fasterwho

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class MyMisAmigosRecyclerViewAdapter(
    private var values: MutableList<ItemAmigo>
) : RecyclerView.Adapter<MyMisAmigosRecyclerViewAdapter.ViewHolder>() {
    private val db = FirebaseFirestore.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_mis_amigos, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val storage = FirebaseStorage.getInstance()
        val item = values[position]

        holder.friendView.text = item.email
        holder.friendName.text = item.nombre
        if (item.imagen != null) {
            val img = storage.getReference(item.imagen?.path.toString())
            GlideApp.with(holder.itemView.context).load(img).into(holder.imageView)
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val friendView: TextView = view.findViewById(R.id.friend_email)
        val friendName: TextView = view.findViewById(R.id.friend_name)
        val imageView: ImageView = view.findViewById(R.id.friend_image)

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            Toast.makeText(v.context, "Viendo el perfil del usuario"+v.id.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    fun setAmigosList() {
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
                        values.clear()
                        values.addAll(items)
                        notifyDataSetChanged()
                        Log.d("amiguiiiis", this.values.toString())
                    }
            }
        }
    }
}