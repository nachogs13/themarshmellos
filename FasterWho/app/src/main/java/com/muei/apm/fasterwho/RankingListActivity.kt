package com.muei.apm.fasterwho

import android.os.Bundle

class RankingListActivity : com.muei.apm.fasterwho.Toolbar() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutInflater.inflate(R.layout.activity_ranking_list,frameLayout)
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar2)

        val rank : String? = intent.getStringExtra("Rank")
        toolbar.title = "Ranking $rank"

        val rankingFragment = supportFragmentManager
                .findFragmentById(R.id.fragmentRankingList) as RankingListFragment
        val args = Bundle()
        args.putString("rank", rank)
        rankingFragment.arguments = args
    }
}