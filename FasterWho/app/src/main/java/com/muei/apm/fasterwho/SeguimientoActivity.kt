package com.muei.apm.fasterwho

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast

class SeguimientoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seguimiento)

        val btnFinalizarRuta : Button = findViewById(R.id.finalizar_ruta_button)
        btnFinalizarRuta.setOnClickListener {
            Toast.makeText(this, "Se finaliza el seguimiento", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, PopUpSaveRoute::class.java)
            startActivity(intent)
        }
    }
}