package com.muei.apm.fasterwho

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.muei.apm.fasterwho.dummy.DummyContent.DummyItem

/**
 * [RecyclerView.Adapter] that can display a [DummyItem].
 * TODO: Replace the implementation with code for your data type.
 */
class MyRankingListRecyclerViewAdapter(
    private val values: MutableList<ItemRanking>
) : RecyclerView.Adapter<MyRankingListRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_ranking_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]

        holder.userRank.text = (position+1).toString()
        holder.userName.text = item.userName
        holder.userPts.text = item.userPts.toString()
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userRank: TextView = view.findViewById(R.id.user_rank)
        val userName: TextView = view.findViewById(R.id.user_name)
        val userPts: TextView = view.findViewById(R.id.user_pts)
    }
}