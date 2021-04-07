package com.muei.apm.fasterwho

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class PopUpSaveRoute : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pop_up_save_route)

        val btnAceptar : TextView = findViewById(R.id.textView4)
        btnAceptar.setOnClickListener {
            Toast.makeText(this, "Se guarda la ruta hecha", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, EstadisticasActivity::class.java)
            startActivity(intent)
        }
    }
}