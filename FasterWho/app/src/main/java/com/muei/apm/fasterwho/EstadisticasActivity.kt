package com.muei.apm.fasterwho

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
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

class EstadisticasActivity : AppCompatActivity(),OnMapReadyCallback {

    private lateinit var mapa: GoogleMap

    // Esto se añade para poder usarlo en el AsyncTask.
    private var parser: SAXParser? = null
    private var handler: SaxHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estadisticas)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map2) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        val btnFinalizar : Button = findViewById(R.id.buttonEstFinalizar)
        btnFinalizar.setOnClickListener({
            Toast.makeText(this, "Se finaliza de ver el resumen ruta", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, RankingActivity::class.java)
            startActivity(intent)
        })

        /*val btnMapa : ImageView = findViewById(R.id.ImagenLeyenda2)
        btnMapa.setOnClickListener({
            Toast.makeText(this, "Viendo la ruta en el mapa", Toast.LENGTH_SHORT).show()
        })*/


    }

    /**
     * Este método ejecuta todas las acciones que programemos antes de abrir el mapa
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mapa = googleMap

        // Lo que hacemos aquí es leer el KML con SAX y llenar el mapa de puntos.
        // Lo hay que hacer con AsyncTask porque sino puede fallar cuando hay muchos puntos
        val factory = SAXParserFactory.newInstance()

        try {
            parser = factory.newSAXParser()

            // Manejador SAX programado por nosotros. Le pasamos nuestro mapa para que ponga los puntos.
            handler = SaxHandler(mapa)

            // AsyncTask. Le pasamos el directorio de ficheros como string.
            val procesador: ProcesarKML = ProcesarKML()
            procesador.execute(this.filesDir.absolutePath)
        } catch (e: SAXException) {
            println(e.message)
        } catch (e: ParserConfigurationException) {
            println(e.message)
        }
        // Add a marker in Sydney and move the camera
        //val sydney = LatLng(-34.0, 151.0)
        //mapa.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        //mapa.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    //==============================================================================================
    // ASYNCTASK - TAREA ASÍNCRONA
    //==============================================================================================
    private inner class ProcesarKML : AsyncTask<String?, Int?, Boolean>() {
        override fun doInBackground(vararg params: String?): Boolean? {
            try {
                parser!!.parse(
                    FileInputStream(File(params[0],"ruta.kml")),
                    handler
                )
            } catch (e: FileNotFoundException) {
                // Pongo null en los contexto para evitar el error. Revisarlo!!
                Toast.makeText(null, "Error: " + e.message, Toast.LENGTH_LONG).show()
            } catch (e: SAXException) {
                Toast.makeText(null, "Error: " + e.message, Toast.LENGTH_LONG).show()
            } catch (e: IOException) {
                Toast.makeText(null, "Error: " + e.message, Toast.LENGTH_LONG).show()
            }
            return true
        }

        override fun onPostExecute(aBoolean: Boolean) {
            mapa.addPolyline(handler?.getRuta()) // Se añade una ruta.

            // Se añade un punto en el mapa.
            //mapa.addMarker(new MarkerOptions().position(handler.coordenadas).title("hola"));

            // Se mueve la cámara a la última posición.
            mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(handler?.getLastCoordenadas(), 15f))
        }
    }
}