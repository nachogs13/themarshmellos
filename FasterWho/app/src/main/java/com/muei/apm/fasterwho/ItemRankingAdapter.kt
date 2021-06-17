package com.muei.apm.fasterwho

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class ItemRankingAdapter(var context: Context, var arrayList: ArrayList<ItemRankingList>) : BaseAdapter() {

    override fun getCount(): Int {
        return arrayList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view:View = View.inflate(context, R.layout.cardview_ranking_item_fragment, null)

        val icons: ImageView = view.findViewById(R.id.icon_list)
        val title: TextView = view.findViewById(R.id.title_text_view)
        val detail : TextView = view.findViewById(R.id.detail_text_view)
        val detail2 : TextView = view.findViewById(R.id.detail1_text_view)

        val items : ItemRankingList = arrayList[position]

        icons.setImageResource(items.icons!!)
        title.text = items.title
        detail.text = items.detail
        detail2.text = items.detail2
        if (items.detail2!!.contains("DESBLOQUEADO")) {
            detail2.setTextColor(Color.parseColor("#00a135"))
        } else if (items.detail2!!.contains("BLOQUEADO")) {
            detail2.setTextColor(Color.parseColor("#ff0000"))
        }

        return view
    }

    override fun getItem(position: Int): Any {
        return arrayList[position]
    }
}