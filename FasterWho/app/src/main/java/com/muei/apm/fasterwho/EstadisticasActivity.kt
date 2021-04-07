package com.muei.apm.fasterwho

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast

class EstadisticasActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estadisticas)

        val btnFinalizar : Button = findViewById(R.id.buttonEstFinalizar)
        btnFinalizar.setOnClickListener({
            Toast.makeText(this, "Se finaliza de ver el resumen ruta", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, RankingActivity::class.java)
            startActivity(intent)
        })

        val btnMapa : ImageView = findViewById(R.id.ImagenLeyenda2)
        btnMapa.setOnClickListener({
            Toast.makeText(this, "Viendo la ruta en el mapa", Toast.LENGTH_SHORT).show()
        })


    }
}