package com.muei.apm.fasterwho

import android.os.Bundle
import androidx.appcompat.widget.Toolbar

class RankingListActivity : com.muei.apm.fasterwho.Toolbar() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_profile)
        layoutInflater.inflate(R.layout.activity_ranking_list,frameLayout)
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar2)
        var rank : String? = intent.getStringExtra("Rank")
        toolbar.setTitle("Ranking $rank")

    }
}