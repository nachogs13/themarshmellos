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
class MyRankingListRecyclerViewAdapter(
    private val values: List<DummyItem>
) : RecyclerView.Adapter<MyRankingListRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_ranking_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        if (item.id == 3.toString()) {
            holder.userName.text = "Yo"
        } else {
            holder.userName.text = "Usuario " + item.id.toString()
        }
        holder.userRank.text = item.id.toString()
        holder.userPts.text = 7500.toString() + " pts." //replace with firebase result of actual points
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val userRank: TextView = view.findViewById(R.id.user_rank)
        val userName: TextView = view.findViewById(R.id.user_name)
        val userPts: TextView = view.findViewById(R.id.user_pts)

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            Toast.makeText(v.context, "Viendo el perfil del usuario"+v.id.toString(), Toast.LENGTH_SHORT).show()
        }
    }
}