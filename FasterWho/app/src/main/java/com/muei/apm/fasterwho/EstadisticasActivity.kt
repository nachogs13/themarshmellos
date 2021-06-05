package com.muei.apm.fasterwho

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import org.xml.sax.SAXException
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.xml.parsers.ParserConfigurationException
import javax.xml.parsers.SAXParser
import javax.xml.parsers.SAXParserFactory

class EstadisticasActivity : AppCompatActivity(),OnMapReadyCallback {

    private lateinit var mapa: GoogleMap

    // Esto se añade para poder usarlo en el AsyncTask.
    private var parser: SAXParser? = null
    private var handler: SaxHandler? = null

    private var distancia : Double? = null
    private var velocidadMaxima : Double? = null
    private var horaInicio: String? = null
    private var duracion: Long? = null
    private var altitudGanada: Double? = null
    private var altitudPerdida: Double? = null
    private var altitudMaxima: Double? = null
    private val viewModel: EstadisticasViewModel by viewModels()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estadisticas)

        // Obtenemos los datos que se le pasan al terminar la ruta
        distancia = intent.getDoubleExtra("distancia", 0.0)
        velocidadMaxima = intent.getDoubleExtra("velocidad", 0.0)
        horaInicio = intent.getStringExtra("horaInicio")
        duracion = intent.getLongExtra("duracion", 0L)
        altitudGanada = intent.getDoubleExtra("altitudGanada", 0.0)
        altitudPerdida = intent.getDoubleExtra("altitudPerdida", 0.0)
        altitudMaxima = intent.getDoubleExtra("altitudMaxima", 0.0)

        // Se le pasan los datos al fragment que los muestra
        viewModel.setEstadisticas(listOf(ItemEstadistica(R.drawable.ic_directions_run_black_24dp,"Distancia", distancia.toString()),
            ItemEstadistica(R.drawable.ic_speed_black_24dp,"Vel. Máx.", velocidadMaxima.toString()),
            ItemEstadistica(R.drawable.ic_clock_24dp,"Hora Inicio", horaInicio),
            ItemEstadistica(R.drawable.ic_timer_black_24dp,"Duración ", getDate(duracion!!)),
            ItemEstadistica(R.drawable.ic_elevation_24dp, "Elev. Ganada", altitudGanada.toString()),
            ItemEstadistica(R.drawable.ic_elevation_24dp, "Elev. Perdida", altitudPerdida.toString()),
            ItemEstadistica(R.drawable.ic_elevation_24dp, "Elev. Máxima", altitudMaxima.toString()))
        )

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

    /**
     * Método para convertir un long con milisegundos en un string que representado la hora
     * con el formato "HH:MM:SS"
     */
    private fun getDate(milliSeconds : Long) :String {
        val formatter = SimpleDateFormat("HH:mm:ss")
        val calendar: Calendar = Calendar.getInstance()
        calendar.setTimeInMillis(milliSeconds)

        return formatter.format(calendar.time)
    }
}