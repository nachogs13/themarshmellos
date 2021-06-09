package com.muei.apm.fasterwho

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

class MisRutas : Toolbar() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutInflater.inflate(R.layout.activity_mis_rutas,frameLayout)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar2)
        toolbar.setTitle("Mis Rutas")
        //setContentView(R.layout.activity_mis_rutas)
        /*     setSupportActionBar(findViewById(R.id.toolbar))

             findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
                 Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                         .setAction("Action", null).show()
             }*/
        val btnAnadirRuta : com.google.android.material.floatingactionbutton.FloatingActionButton = findViewById(R.id.floatingActionButton)
        btnAnadirRuta.setOnClickListener({
            Toast.makeText(this, "Iniciar Ruta", Toast.LENGTH_SHORT).show()
            //val intent = Intent(this, IniciarRutaActivity::class.java)
            val intent = Intent(this, SeguimientoActivity::class.java)
            startActivity(intent)
        })
    }
    override fun onResume() {
        super.onResume()
        navView.menu.getItem(3).setChecked(true)
    }
}