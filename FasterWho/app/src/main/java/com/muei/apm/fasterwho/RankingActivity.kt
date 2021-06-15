package com.muei.apm.fasterwho

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast

class RankingActivity : Toolbar(), AdapterView.OnItemClickListener{
    private var listView : ListView ? = null
    private var itemAdapters: ItemRankingAdapter ? = null
    private var arrayList : ArrayList<ItemRankingList> ? = null
    private var blocked : ArrayList<String> ? = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_ranking)
        layoutInflater.inflate(R.layout.activity_ranking,frameLayout)
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar2)
        toolbar.setTitle("Ranking")

        listView = findViewById(R.id.cardview_list_view)
        arrayList = ArrayList()
        arrayList = setDataItem()
        itemAdapters = ItemRankingAdapter(applicationContext, arrayList!!)
        listView?.adapter = itemAdapters
        listView?.onItemClickListener = this

        Toast.makeText(this, "Listado de Rankings", Toast.LENGTH_SHORT).show()
    }
    override fun onResume() {
        super.onResume()
        navView.menu.getItem(2).setChecked(true)
    }

    private fun isBlocked(rank: String, userPts: Int) : String {
        lateinit var message : String

        when (rank) {
            "Leyenda" -> if (userPts < 20000) {
                message = "BLOQUEADO - Faltan ${20000-userPts} puntos más"
                blocked?.add("Leyenda")
            } else {
                message = "DESBLOQUEADO"
            }
            "Diamante" -> if (userPts < 15000) {
                message = "BLOQUEADO - Faltan ${15000-userPts} puntos más"
                blocked?.add("Diamante")
            } else {
                message = "DESBLOQUEADO"
            }
            "Oro" -> if (userPts < 10000) {
                message = "BLOQUEADO - Faltan ${10000-userPts} puntos más"
                blocked?.add("Oro")
            } else {
                message = "DESBLOQUEADO"
            }
            "Plata" -> if (userPts < 5000) {
                message = "BLOQUEADO - Faltan ${5000-userPts} puntos más"
                blocked?.add("Plata")
            } else {
                message = "DESBLOQUEADO"
            }
            else -> message = "DESBLOQUEADO"
        }

        return message
    }

    private fun setDataItem() : ArrayList<ItemRankingList> {
        val listItem : ArrayList<ItemRankingList> = ArrayList()

        //Obtener los puntos del usuario para saber los puntos restantes para cada rango bloqueado
        var userPts: Int = 7500 //esto debe salir de la info del usuario en firebase

        listItem.add(ItemRankingList(R.drawable.leyenda, "Leyenda", "20.000+ pts.", isBlocked("Leyenda", userPts)))
        listItem.add(ItemRankingList(R.drawable.diamante, "Diamante", "15.000 - 19.999 pts.", isBlocked("Diamante", userPts)))
        listItem.add(ItemRankingList(R.drawable.oro, "Oro", "10.000 - 14.999 pts.", isBlocked("Oro", userPts)))
        listItem.add(ItemRankingList(R.drawable.plata, "Plata", "5.000 - 9.999 pts.", isBlocked("Plata", userPts)))
        listItem.add(ItemRankingList(R.drawable.bronce, "Bronce", "0 - 4.999 pts.", isBlocked("Bronce", userPts)))

        return listItem
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val items:ItemRankingList = arrayList?.get(position)!!

        if (!blocked.isNullOrEmpty() && blocked?.contains(items.title)!!) {
            Toast.makeText(applicationContext, "Desbloquee este rango para ver más", Toast.LENGTH_SHORT).show()
        } else {
            val intent = Intent(this, RankingListActivity::class.java)
            intent.putExtra("Rank",items.title)

            startActivity(intent)
        }
    }
}