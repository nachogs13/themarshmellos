package com.muei.apm.fasterwho

import android.content.Intent
import android.graphics.Camera
import android.os.AsyncTask
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
import org.xml.sax.SAXException
import java.io.*
import java.lang.Exception
import javax.xml.parsers.ParserConfigurationException
import javax.xml.parsers.SAXParser
import javax.xml.parsers.SAXParserFactory

class RutaActivity : Toolbar(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val db = FirebaseFirestore.getInstance()
    private lateinit var item : String
    private var parser: SAXParser? = null
    private var handler: SaxHandler? = null
    private var file: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_ruta)
        layoutInflater.inflate(R.layout.activity_ruta,frameLayout)
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar2)
        toolbar.setTitle("Ruta concreta")

        val btnEmpezar : Button = findViewById(R.id.buttonEmpezar)
        btnEmpezar.setOnClickListener {
            val intent = Intent(this, SeguimientoActivity::class.java)
            //val intent = Intent(this, IniciarRutaActivity::class.java)
            // Le pasamos a SeguimientoActivity el fichero KML para que muestre la ruta
            intent.putExtra("file", this.intent.getStringExtra("file"))
            intent.putExtra("latitud_fin", this.intent.getDoubleExtra("latitud_fin", 0.0))
            intent.putExtra("longitud_fin", this.intent.getDoubleExtra("longitud_fin", 0.0))
            intent.putExtra("longitud_ini", this.intent.getDoubleExtra("longitud_ini", 0.0))
            intent.putExtra("latitud_ini", this.intent.getDoubleExtra("latitud_ini", 0.0))

            startActivity(intent)
        }

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
        file = intent.getStringExtra("file")

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

        // intentamos poner el kml con KmlLayer, si salta excepción quiere decir que estamos ante
        // una ruta propia del usuario, por lo tanto hay que buscarla en el almacenamiento interno
        // y añadir el KML al mapa mediante ProcesarKML
        try {
            val layer = KmlLayer(mMap,id,this)
            layer.addLayerToMap()
        } catch(e: Exception) {
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
            } catch (e: ParserConfigurationException) {
                println(e.message)
            }
        }

        mMap.addMarker(MarkerOptions().position(LatLng(latitud_ini,longitud_ini))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.
                HUE_GREEN)).title("Inicio"))
        mMap.addMarker(MarkerOptions().position(LatLng(latitud_fin,longitud_fin))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.
                HUE_ORANGE)).title("Fin"))
    }

    //==============================================================================================
    // ASYNCTASK - TAREA ASÍNCRONA
    //==============================================================================================
    private inner class ProcesarKML : AsyncTask<String?, Int?, Boolean>() {
        override fun doInBackground(vararg params: String?): Boolean? {
            try {
                parser!!.parse(
                    FileInputStream(File(params[0],file)),
                    handler
                )
            } catch (e: FileNotFoundException) {
                // Pongo null en los contexto para evitar el error. Revisarlo!!
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