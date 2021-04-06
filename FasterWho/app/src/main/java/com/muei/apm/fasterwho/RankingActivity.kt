package com.muei.apm.fasterwho

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast

class RankingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ranking)

        val btnRanking1 : TextView = findViewById(R.id.ranking1)
        btnRanking1.setOnClickListener {
            Toast.makeText(this, "Viendo el Ranking1", Toast.LENGTH_SHORT).show()
        }

        val btnRanking2 : TextView = findViewById(R.id.ranking2)
        btnRanking2.setOnClickListener {
            Toast.makeText(this, "Viendo el Ranking2", Toast.LENGTH_SHORT).show()
        }

        val btnRanking3 : TextView = findViewById(R.id.ranking3)
        btnRanking3.setOnClickListener {
            Toast.makeText(this, "Viendo el Ranking3", Toast.LENGTH_SHORT).show()
        }
    }
}