package com.muei.apm.fasterwho

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class InicioActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)
        /*val navigation = Intent(this, NavigationActivity::class.java)
        startActivity(navigation)*/
        val navigation = Intent(this, NavigationActivity::class.java)
        startActivity(navigation)

        val btnFiltros : TextView = findViewById(R.id.textViewFiltros)
        btnFiltros.setOnClickListener{
            val intent = Intent(this, FiltersActivity::class.java)
            startActivity(intent)
        }

    }
}