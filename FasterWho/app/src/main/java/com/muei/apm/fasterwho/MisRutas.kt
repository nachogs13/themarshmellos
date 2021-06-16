package com.muei.apm.fasterwho

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast

class MisRutas : Toolbar() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutInflater.inflate(R.layout.activity_mis_rutas,frameLayout)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar2)
        toolbar.title = "Mis Rutas"

        val btnAnadirRuta : com.google.android.material.floatingactionbutton.FloatingActionButton = findViewById(R.id.floatingActionButton)
        btnAnadirRuta.setOnClickListener {
            Toast.makeText(this, "Iniciar Ruta", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, SeguimientoActivity::class.java)
            startActivity(intent)
        }

        val btnFiltros : TextView = findViewById(R.id.filterText)
        btnFiltros.setOnClickListener{
            val intent = Intent(this, FiltersActivity::class.java)
            intent.putExtra("parent", "MisRutas")
            startActivity(intent)
        }
    }
    override fun onResume() {
        super.onResume()
        navView.menu.getItem(3).isChecked = true
    }

    fun sinResultados(isEmp: Boolean) {
        val sinRes : TextView = findViewById(R.id.textViewSinResultados2)
        val rel : RelativeLayout = findViewById(R.id.loadingPanel3)
        rel.visibility = View.GONE
        if (isEmp) {
            sinRes.visibility = View.VISIBLE
        } else {
            sinRes.visibility = View.GONE
        }
    }
}