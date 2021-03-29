package com.muei.apm.fasterwho

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class IniciarRutaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_iniciar_ruta)

        val btnIniciarRuta : Button = findViewById(R.id.iniciar_ruta_button)
        btnIniciarRuta.setOnClickListener {
            val intent = Intent(this, SeguimientoActivity::class.java)
            startActivity(intent)
        }

    }
}