package com.muei.apm.fasterwho

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.muei.apm.fasterwho.dummy.DummyContent.DummyItem

/**
 * [RecyclerView.Adapter] that can display a [DummyItem].
 * TODO: Replace the implementation with code for your data type.
 */
class MyMisAmigosRecyclerViewAdapter(
    private val values: List<DummyItem>
) : RecyclerView.Adapter<MyMisAmigosRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_mis_amigos, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        if (item.id == 3.toString()) {
            holder.friendName.text = "Yo"
            holder.imageView.setImageResource(R.drawable.fotoperfil2)
        } else {
            holder.friendName.text = "Amigo " + item.id.toString()
        }
        holder.friendView.text = item.id.toString()
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val friendView: TextView = view.findViewById(R.id.friend_rank)
        val friendName: TextView = view.findViewById(R.id.friend_name)
        val imageView: ImageView = view.findViewById(R.id.friend_image)

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            Toast.makeText(v.context, "Viendo el perfil del usuario"+v.id.toString(), Toast.LENGTH_SHORT).show()
        }
    }
}