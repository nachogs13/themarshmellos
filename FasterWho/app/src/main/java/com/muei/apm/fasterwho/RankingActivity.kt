package com.muei.apm.fasterwho

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast

class RankingActivity : Toolbar(), AdapterView.OnItemClickListener{
    private var listView : ListView ? = null
    private var itemAdapters: ItemRankingAdapter ? = null
    private var arrayList : ArrayList<ItemRankingList> ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_ranking)
        layoutInflater.inflate(R.layout.activity_ranking,frameLayout)

        listView = findViewById(R.id.cardview_list_view)
        arrayList = ArrayList()
        arrayList = setDataItem()
        itemAdapters = ItemRankingAdapter(applicationContext, arrayList!!)
        listView?.adapter = itemAdapters
        listView?.onItemClickListener = this

        Toast.makeText(this, "Listado de Rankings", Toast.LENGTH_SHORT).show()

       /*
        val btnRanking1 : TextView = findViewById(R.id.ranking1)
        btnRanking1.setOnClickListener {
            Toast.makeText(this, "Ranking1 BLOQUEADO", Toast.LENGTH_SHORT).show()
        }

        val btnRanking2 : TextView = findViewById(R.id.ranking2)
        btnRanking2.setOnClickListener {
            Toast.makeText(this, "Ranking2 BLOQUEADO", Toast.LENGTH_SHORT).show()
        }

        val btnRanking3 : TextView = findViewById(R.id.ranking3)
        btnRanking3.setOnClickListener {
            Toast.makeText(this, "Viendo el Ranking3", Toast.LENGTH_SHORT).show()
        }*/
    }
    override fun onResume() {
        super.onResume()
        navView.menu.getItem(2).setChecked(true)
    }

    private fun setDataItem() : ArrayList<ItemRankingList> {
        var listItem : ArrayList<ItemRankingList> = ArrayList()

        listItem.add(ItemRankingList(R.drawable.leyenda, "Leyenda", "Puntuaciones", "BLOQUEADO"))
        listItem.add(ItemRankingList(R.drawable.diamante, "Diamante", "Puntuaciones", "BLOQUEADO"))
        listItem.add(ItemRankingList(R.drawable.oro, "Oro", "Puntuaciones", "BLOQUEADO"))
        listItem.add(ItemRankingList(R.drawable.plata, "Plata", "Puntuaciones", "DESBLOQUEADO"))
        listItem.add(ItemRankingList(R.drawable.bronce, "Bronce", "Puntuaciones", "DESBLOQUEADO"))
        return listItem
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        var items:ItemRankingList = arrayList?.get(position)!!

        Toast.makeText(applicationContext, items.title, Toast.LENGTH_SHORT).show()
    }
}