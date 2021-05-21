package com.muei.apm.fasterwho

import android.content.Intent
import android.graphics.Camera
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.maps.android.data.kml.KmlContainer
import com.google.maps.android.data.kml.KmlLayer
import com.google.maps.android.data.kml.KmlPlacemark
import com.google.maps.android.data.kml.KmlPolygon
import com.google.maps.android.geometry.Point
import com.google.maps.android.ktx.utils.kml.kmlLayer
import org.w3c.dom.Text
import java.io.File
import java.io.InputStream

class RutaActivity : Toolbar(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val db = FirebaseFirestore.getInstance()
    private lateinit var item : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_ruta)
        layoutInflater.inflate(R.layout.activity_ruta,frameLayout)
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar2)
        toolbar.setTitle("Ruta concreta")

        val btnEmpezar : Button = findViewById(R.id.buttonEmpezar)
        btnEmpezar.setOnClickListener({
            val intent = Intent(this, SeguimientoActivity::class.java)
            startActivity(intent)
        })

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapRutaConcreta) as SupportMapFragment
        mapFragment.getMapAsync(this)

        /*val btnMaria : TextView = findViewById(R.id.maria)
        btnMaria.setOnClickListener {
            Toast.makeText(this, "Viendo usuario María", Toast.LENGTH_SHORT).show()
        }
        val btnAvatarMaria : ImageView = findViewById(R.id.avatar1)
        btnAvatarMaria.setOnClickListener {
            Toast.makeText(this, "Viendo usuario María", Toast.LENGTH_SHORT).show()
        }

        val btnElena : TextView = findViewById(R.id.elena)
        btnElena.setOnClickListener {
            Toast.makeText(this, "Viendo usuario Elena", Toast.LENGTH_SHORT).show()
        }
        val btnAvatarElena : ImageView = findViewById(R.id.avatar2)
        btnAvatarElena.setOnClickListener {
            Toast.makeText(this, "Viendo usuario Elena", Toast.LENGTH_SHORT).show()
        }*/

        /*val btnMapa : ImageView = findViewById(R.id.imagenMapa)
        btnMapa.setOnClickListener {
            Toast.makeText(this, "Viendo Mapa Completo", Toast.LENGTH_SHORT).show()
        }*/
        val textView: TextView = findViewById(R.id.distanciaTotal)
        textView.text = "Distancia total ${intent.getDoubleExtra("distancia",0.0)} km"
        val textViewNombreRuta: TextView = findViewById(R.id.textNombreRuta)
        textViewNombreRuta.text = "${intent.getStringExtra("nombre")}"

        val btnRating : RatingBar = findViewById(R.id.ratingBar2)
        btnRating.setOnClickListener {
            Toast.makeText(this, "Poniendo estrellas dificultad", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val latitud_fin = intent.getDoubleExtra("latitud_fin",0.0)
        val longitud_fin = intent.getDoubleExtra("longitud_fin", 0.0)
        val longitud_ini = intent.getDoubleExtra("longitud_ini",0.0)
        val latitud_ini = intent.getDoubleExtra("latitud_ini",0.0)
        val file = intent.getStringExtra("file")

        val id = resources.getIdentifier(file,"raw",packageName)

        mMap = googleMap
        /*val double = 42.880444
        val double2 = -8.545669*/
        //mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE)
        val cameraPosition: CameraPosition = CameraPosition.Builder().
        target(LatLng(latitud_ini, longitud_ini))
                .zoom(13.5f)
                .bearing(0f)
                .tilt(25f)
                .build()
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

        val layer = KmlLayer(mMap,id,this)
        layer.addLayerToMap()
        mMap.addMarker(MarkerOptions().position(LatLng(latitud_ini,longitud_ini))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.
                HUE_GREEN)).title("Inicio"))
        mMap.addMarker(MarkerOptions().position(LatLng(latitud_fin,longitud_fin))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.
                HUE_ORANGE)).title("Fin"))
    }
}