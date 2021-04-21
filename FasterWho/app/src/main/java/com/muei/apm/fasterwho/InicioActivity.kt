package com.muei.apm.fasterwho

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView

class InicioActivity : com.muei.apm.fasterwho.Toolbar(), NavigationView.OnNavigationItemSelectedListener {
    //private lateinit var drawerLayout: DrawerLayout
    //private lateinit var navView : NavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_profile)
        layoutInflater.inflate(R.layout.activity_inicio,frameLayout)
        val toolbar: Toolbar = findViewById(R.id.toolbar2)
        toolbar.setTitle("Inicio")

        val btnFiltros : TextView = findViewById(R.id.textViewFiltros)
        btnFiltros.setOnClickListener{
            val intent = Intent(this, FiltersActivity::class.java)
            startActivity(intent)
        }

        val btnScan : Button = findViewById(R.id.buttonScanQr)
        btnScan.setOnClickListener{
            Toast.makeText(this, "Se busca ruta mediante código QR", Toast.LENGTH_SHORT).show()
        }

        val btnAnadirRuta : com.google.android.material.floatingactionbutton.FloatingActionButton = findViewById(R.id.floatingActionButton)
        btnAnadirRuta.setOnClickListener({
            Toast.makeText(this, "Iniciar Ruta", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, IniciarRutaActivity::class.java)
            startActivity(intent)
        })
    }

    override fun onResume() {
        super.onResume()
        navView.menu.getItem(0).setChecked(true)
    }

}