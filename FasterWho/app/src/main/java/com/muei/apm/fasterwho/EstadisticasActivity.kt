package com.muei.apm.fasterwho

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.storage.FirebaseStorage
import org.xml.sax.SAXException
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.*
import javax.xml.parsers.ParserConfigurationException
import javax.xml.parsers.SAXParser
import javax.xml.parsers.SAXParserFactory

class EstadisticasActivity : AppCompatActivity(),OnMapReadyCallback {

    private lateinit var mapa: GoogleMap
    private val TAG = "EstadisticasActivity"
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
    private var nombreArchivoRuta: String? = null
    private var nombreRuta: String? = null
    private var latitud_inicial: Double? = null
    private var latitud_final: Double? = null
    private var longitud_inicial: Double? = null
    private var longitud_final: Double? = null
    private val viewModel: EstadisticasViewModel by viewModels()

    lateinit var storage: FirebaseStorage
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()


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
        nombreRuta = intent.getStringExtra("nombreRuta")
        // obtenemos las latitudes y longitudes iniciales y finales
        latitud_inicial = intent.getDoubleExtra("latitud_inicial", 0.0)
        latitud_final = intent.getDoubleExtra("latitud_final", 0.0)
        longitud_inicial = intent.getDoubleExtra("longitud_inicial", 0.0)
        longitud_final = intent.getDoubleExtra("longitud_final", 0.0)
        // Obtenemos el nombre del archivo KML de la ruta
        nombreArchivoRuta = intent.getStringExtra("nombreArchivoRuta")

        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING
        df.decimalFormatSymbols = DecimalFormatSymbols(Locale.ENGLISH)

        // comprobamos si se quiere guardar la ruta
        val guardarRuta = intent.getBooleanExtra("guardarRuta", false)
        if (guardarRuta) {


            Log.i(TAG, "Usuario actual: " + firebaseAuth.currentUser.email)
            // inicializamos el almacenamiento en Firebase
            storage= FirebaseStorage.getInstance()

            // Creamos una referencia al storage desde nuestra app
            var storageRef = storage.reference

            // Obtenemos la ruta del fichero a subir
            var file = Uri.fromFile(File(this.filesDir.absolutePath, nombreArchivoRuta))

            val rutaRef = storageRef.child("kmlsRutas/${firebaseAuth.currentUser.email}/${file.lastPathSegment}")
            val uploadTask = rutaRef.putFile(file)

            // registramos observadores para escuchar cuando la carga termina o falla
            uploadTask.addOnFailureListener {
                Log.i(TAG, "Falló la carga del kml")
                Toast.makeText(this, "Error al cargar en Firebase el KML", Toast.LENGTH_SHORT).show()

            }.addOnSuccessListener { taskSnapshot ->
                Log.i(TAG, "Archivo cargado correctamente")
                Toast.makeText(this, "Archivo KML cargado correctamente en Firebase", Toast.LENGTH_SHORT).show()

            }

            // Probamos a obtener la direccion a partir de LatLng
            var addresses: List<Address>? = null;
            val geocoder = Geocoder(this, Locale.getDefault())
            var direccionRuta: String? = null
            if (latitud_inicial != 0.0 && longitud_inicial != 0.0) {
                addresses = geocoder.getFromLocation(latitud_inicial!!,longitud_final!!,1)
                if (addresses.get(0).featureName != null) {
                    direccionRuta = addresses.get(0).featureName
                    Log.i(TAG, "Dirección corta detectada: ${direccionRuta}")
                } else {
                    direccionRuta = addresses.get(0).getAddressLine(0)
                    Log.i(TAG, "Dirección larga detectada: ${direccionRuta}")
                }
            } else {
                direccionRuta = ""
            }


            // procedemos a almacenar en la coleccion "rutasPrivadas" de BD la información sobre la ruta
            Log.i(TAG, "Ruta: ${filesDir.absolutePath}/${file.lastPathSegment}")
            val ruta = hashMapOf(
                "imgInicio" to db.document("ImgRutas/carreira-pedestre.PNG"),
                "kmlfile" to db.document("${filesDir.absolutePath}/${file.lastPathSegment}"),
                //"kmlFile" to storageRef.child("/kmlRutas/${firebaseAuth.currentUser.email}/${file.lastPathSegment}"),
                "coordenadas_inicio" to GeoPoint(latitud_inicial!!,longitud_inicial!!),
                "coordenadas_fin" to GeoPoint(latitud_final!!,longitud_final!!),
                "direccion" to direccionRuta,
                "distancia" to df.format(distancia!!/1000).toDouble(),
                "nombre" to nombreRuta,
                "usuario" to firebaseAuth.currentUser.email,
                "propietario" to firebaseAuth.currentUser.email,
                "desnivel" to 0,
                "public" to false,
                "rating" to 0
            )
            // añadimos el documento a la base de datos
            db.collection("rutas").document()
                .set(ruta)
                .addOnSuccessListener { Log.i(TAG, "Ruta guardada en Firebase") }
                .addOnFailureListener { e -> Log.i(TAG, "Error al guardar la ruta en Firebase")}
        }




        // convertimos la distancia a metros o kilometros
        var distanciaString : String? = null
        if (distancia!! < 1000.0) {
            distanciaString = df.format(distancia!!) + " metros"
        } else {
            distanciaString = df.format(distancia!!/1000) + " km"
        }
        // Se le pasan los datos al fragment que los muestra
        viewModel.setEstadisticas(listOf(ItemEstadistica(R.drawable.ic_directions_run_black_24dp,"Distancia", distanciaString),
            ItemEstadistica(R.drawable.ic_speed_black_24dp,"Vel. Máx.", df.format(velocidadMaxima!!)),
            ItemEstadistica(R.drawable.ic_clock_24dp,"Hora Inicio", horaInicio),
            ItemEstadistica(R.drawable.ic_timer_black_24dp,"Duración ", getDate(duracion!!)),
            ItemEstadistica(R.drawable.ic_elevation_24dp, "Elev. Ganada", df.format(altitudGanada!!)),
            ItemEstadistica(R.drawable.ic_elevation_24dp, "Elev. Perdida", df.format(altitudPerdida!!)),
            ItemEstadistica(R.drawable.ic_elevation_24dp, "Elev. Máxima", df.format(altitudMaxima!!))
        ))

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
                    FileInputStream(File(params[0],nombreArchivoRuta)),
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

        calendar.timeInMillis = milliSeconds
        // restamos una por a causa del cambio horario
        calendar.add(Calendar.HOUR_OF_DAY, -1)

        return formatter.format(calendar.time)
    }
}