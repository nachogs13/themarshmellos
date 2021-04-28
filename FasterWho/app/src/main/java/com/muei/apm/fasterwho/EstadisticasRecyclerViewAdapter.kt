package com.muei.apm.fasterwho

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class EstadisticasRecyclerViewAdapter(private val values: List<ItemEstadistica>
): RecyclerView.Adapter<EstadisticasRecyclerViewAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EstadisticasRecyclerViewAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_estadisticas_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: EstadisticasRecyclerViewAdapter.ViewHolder, position: Int) {
        val item = values[position]
        holder.stadisticName.text = item.stadisticName
        holder.stadistic.text = item.stadistic
        item.icon?.let { holder.icon.setImageResource(it) }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val stadistic: TextView = view.findViewById(R.id.stadistic)
        val stadisticName: TextView = view.findViewById(R.id.stadistic_name)
        val icon : ImageView = view.findViewById(R.id.stadistic_icon)

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            Toast.makeText(v.context, "Viendo estadística "+v.id.toString(), Toast.LENGTH_SHORT).show()
        }

    }
}