package com.muei.apm.fasterwho

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.xml.sax.SAXException
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import javax.xml.parsers.ParserConfigurationException
import javax.xml.parsers.SAXParser
import javax.xml.parsers.SAXParserFactory

class RutaActivity : Toolbar(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var parser: SAXParser? = null
    private var handler: SaxHandler? = null
    private var file: String = ""
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutInflater.inflate(R.layout.activity_ruta,frameLayout)
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar2)
        toolbar.title = "Ruta concreta"

        val btnRating : RatingBar = findViewById(R.id.ratingBar2)
        btnRating.rating = this.intent.getLongExtra("rating", 0L).toFloat()

        val btnEmpezar : Button = findViewById(R.id.buttonEmpezar)
        btnEmpezar.setOnClickListener {
            val intent = Intent(this, SeguimientoActivity::class.java)
            // Le pasamos a SeguimientoActivity el fichero KML para que muestre la ruta
            intent.putExtra("file", this.intent.getStringExtra("file"))
            intent.putExtra("latitud_fin", this.intent.getDoubleExtra("latitud_fin", 0.0))
            intent.putExtra("longitud_fin", this.intent.getDoubleExtra("longitud_fin", 0.0))
            intent.putExtra("longitud_ini", this.intent.getDoubleExtra("longitud_ini", 0.0))
            intent.putExtra("latitud_ini", this.intent.getDoubleExtra("latitud_ini", 0.0))
            intent.putExtra("rutaRealizada", this.intent.getStringExtra("nombre"))

            startActivity(intent)
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapRutaConcreta) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val usuariosFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentUsuarios) as UsuariosRutaFragment
        val args = Bundle()
        args.putString("ruta", this.intent.getStringExtra("id").toString())
        usuariosFragment.arguments = args

        val textView: TextView = findViewById(R.id.distanciaTotal)
        textView.text = "Distancia total: ${intent.getDoubleExtra("distancia",0.0)} km"
        val textViewNombreRuta: TextView = findViewById(R.id.textNombreRuta)
        textViewNombreRuta.text = "${intent.getStringExtra("nombre")}"

        val compartir : ImageView = findViewById(R.id.compartir)
        compartir.setOnClickListener {
            try {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name")
                var shareMessage = "\n¿Conseguirás hacer la ruta ${intent.getStringExtra("nombre")} más rapido que yo?\n\n"
                shareMessage =
                    """
                    ${shareMessage}https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}
                    
                    
                    """.trimIndent()
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                startActivity(Intent.createChooser(shareIntent, "Seleccione aplicación"))
            } catch (e: Exception) {
                Log.e("Exception", e.toString())
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val latitudFin = intent.getDoubleExtra("latitud_fin",0.0)
        val longitudFin = intent.getDoubleExtra("longitud_fin", 0.0)
        val longitudIni = intent.getDoubleExtra("longitud_ini",0.0)
        val latitudIni = intent.getDoubleExtra("latitud_ini",0.0)
        file = intent.getStringExtra("file")!!

        mMap = googleMap
        val cameraPosition: CameraPosition = CameraPosition.Builder().
        target(LatLng(latitudIni, longitudIni))
                .zoom(13.5f)
                .bearing(0f)
                .tilt(25f)
                .build()
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))


        // Lo que hacemos aquí es leer el KML con SAX y llenar el mapa de puntos.
        // Lo hay que hacer con AsyncTask porque sino puede fallar cuando hay muchos puntos
        val factory = SAXParserFactory.newInstance()
        try {
            parser = factory.newSAXParser()
            // Manejador SAX programado por nosotros. Le pasamos nuestro mapa para que ponga los puntos.
            handler = SaxHandler(mMap)

            // AsyncTask. Le pasamos el directorio de ficheros como string.
            val procesador: RutaActivity.ProcesarKML = ProcesarKML()
            procesador.execute(this.filesDir.absolutePath)
        } catch (e: SAXException) {
            println(e.message)
            Log.d("ErrorSAX", e.toString())
        } catch (e: ParserConfigurationException) {
            println(e.message)
            Log.d("ErrorParser", e.toString())
        }

        mMap.addMarker(MarkerOptions().position(LatLng(latitudIni,longitudIni))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.
                HUE_GREEN)).title("Inicio"))
        mMap.addMarker(MarkerOptions().position(LatLng(latitudFin,longitudFin))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.
                HUE_ORANGE)).title("Fin"))
    }

    //==============================================================================================
    // ASYNCTASK - TAREA ASÍNCRONA
    //==============================================================================================
    private inner class ProcesarKML : AsyncTask<String?, Int?, Boolean>() {
        override fun doInBackground(vararg params: String?): Boolean {
            try {
                parser!!.parse(
                    FileInputStream(File(params[0],file)),
                    handler
                )
            } catch (e: FileNotFoundException) {
                Log.d("ErrorFichero", e.toString())
            } catch (e: SAXException) {
                Log.d("ErrorRuta", e.toString())
            } catch (e: IOException) {
                Log.d("Error", e.toString())
            }
            return true
        }

        override fun onPostExecute(aBoolean: Boolean) {
            mMap.addPolyline(handler?.getRuta()) // Se añade una ruta.

            // Se mueve la cámara a la última posición.
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(handler?.getLastCoordenadas(), 15f))
        }
    }
}