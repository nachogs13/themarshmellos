package com.muei.apm.fasterwho

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.RatingBar
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class IniciarRutaActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_iniciar_ruta)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapa_ruta) as SupportMapFragment
        mapFragment.getMapAsync(this)

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

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
}