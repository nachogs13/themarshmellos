package com.muei.apm.fasterwho

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.RatingBar
import android.widget.Toast

class IniciarRutaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_iniciar_ruta)

        val btnIniciarRuta : Button = findViewById(R.id.iniciar_ruta_button)
        btnIniciarRuta.setOnClickListener {
            Toast.makeText(this, "Se inicia seguimiento de actividad", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, SeguimientoActivity::class.java)
            startActivity(intent)
        }

        val btnSearch : Button = findViewById(R.id.ubicacion_search_button)
        btnSearch.setOnClickListener {
            Toast.makeText(this, "Buscando Ubicación", Toast.LENGTH_SHORT).show()
        }

    }
}