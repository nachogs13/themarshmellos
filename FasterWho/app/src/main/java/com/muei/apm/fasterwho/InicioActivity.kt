package com.muei.apm.fasterwho

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton

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

        val btnAnadirRuta : com.google.android.material.floatingactionbutton.FloatingActionButton = findViewById(R.id.floatingActionButton)
        btnAnadirRuta.setOnClickListener({
            Toast.makeText(this, "Iniciar Ruta", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, IniciarRutaActivity::class.java)
            startActivity(intent)
        })

    }
}