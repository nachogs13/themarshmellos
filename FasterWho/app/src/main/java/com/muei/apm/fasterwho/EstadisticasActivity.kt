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
import com.google.firebase.Timestamp
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
import kotlin.math.floor
import kotlin.math.roundToInt

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
    private var rutaRealizada: String? = null
    private var velocidadMedia: Double? = null
    private var aceleracionMaxima: Double? = null
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
        Log.i(TAG,"La ruta ha durado: $duracion ms")
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
        // Obtenemos el nombre de la ruta que se ha realizado (si es el caso)
        rutaRealizada = intent.getStringExtra("rutaRealizada")
        Log.i(TAG,"El nombre de la ruta es: $rutaRealizada")
        velocidadMedia = intent.getDoubleExtra("velocidadMedia", 0.0)
        aceleracionMaxima = intent.getDoubleExtra("aceleracionMaxima", 0.0)
        // Se muestra el nombre de la ruta pública realizada
        if (rutaRealizada != null) {
            Toast.makeText(this, "Ruta hecha: $rutaRealizada",Toast.LENGTH_SHORT).show()
        }

        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING
        df.decimalFormatSymbols = DecimalFormatSymbols(Locale.ENGLISH)

        // comprobamos si se quiere guardar la ruta
        val guardarRuta = intent.getBooleanExtra("guardarRuta", false)
        if (guardarRuta) {

            Log.i(TAG, "Usuario actual: " + firebaseAuth.currentUser.email)
            // inicializamos el almacenamiento en Firebase
            storage = FirebaseStorage.getInstance()

            // Creamos una referencia al storage desde nuestra app
            val storageRef = storage.reference

            // Obtenemos la ruta del fichero a subir
            val file = Uri.fromFile(File(this.filesDir.absolutePath, nombreArchivoRuta))

            val rutaRef = storageRef.child("kmlsRutas/${file.lastPathSegment}")
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
                /*if (addresses.get(0).featureName != null) {
                    direccionRuta = addresses.get(0).featureName
                    Log.i(TAG, "Dirección corta detectada: ${direccionRuta}")
                } else {*/
                direccionRuta = addresses.get(0).getAddressLine(0)
                Log.i(TAG, "Dirección larga detectada: ${direccionRuta}")
                //}
            } else {
                direccionRuta = ""
            }

            // procedemos a almacenar en la coleccion "rutas" de BD la información sobre la ruta
            Log.i(TAG, "Ruta: ${filesDir.absolutePath}/${file.lastPathSegment}")
            val ruta = hashMapOf(
                "imgInicio" to db.document("ImgRutas/carreira-pedestre.PNG"),
                "imagen" to "carreira-pedestre.PNG",
                "kmlfile" to db.document("${filesDir.absolutePath}/${file.lastPathSegment}"),
                "kml" to "$nombreArchivoRuta",
                //"kmlFile" to storageRef.child("/kmlRutas/${firebaseAuth.currentUser.email}/${file.lastPathSegment}"),
                "coordenadas_inicio" to GeoPoint(latitud_inicial!!,longitud_inicial!!),
                "coordenadas_fin" to GeoPoint(latitud_final!!,longitud_final!!),
                "direccion" to direccionRuta,
                "distancia" to df.format(distancia!!/1000).toDouble(),
                "nombre" to nombreRuta,
                "usuario" to firebaseAuth.currentUser.email,
                "propietario" to firebaseAuth.currentUser.email,
                "desnivel" to altitudMaxima,
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
        val distanciaString: String?
        distanciaString = if (distancia!! < 1000.0) {
            df.format(distancia!!) + " metros"
        } else {
            df.format(distancia!!/1000) + " km"
        }
        // Se le pasan los datos al fragment que los muestra
        viewModel.setEstadisticas(listOf(ItemEstadistica(R.drawable.ic_directions_run_black_24dp,"Distancia", distanciaString),
            ItemEstadistica(R.drawable.ic_speed_black_24dp,"Vel. Máx.", df.format(velocidadMaxima!!)),
            ItemEstadistica(R.drawable.ic_speed_black_24dp,"Vel. Media.", df.format(velocidadMedia!!)),
            ItemEstadistica(R.drawable.ic_speed_black_24dp,"Acel. Máxima.", df.format(aceleracionMaxima!!)),
            ItemEstadistica(R.drawable.ic_clock_24dp,"Hora Inicio", horaInicio),
            ItemEstadistica(R.drawable.ic_timer_black_24dp,"Duración ", getDate(duracion!!)),
            ItemEstadistica(R.drawable.ic_elevation_24dp, "Elev. Ganada", df.format(altitudGanada!!)),
            ItemEstadistica(R.drawable.ic_elevation_24dp, "Elev. Perdida", df.format(altitudPerdida!!)),
            ItemEstadistica(R.drawable.ic_elevation_24dp, "Elev. Máxima", df.format(altitudMaxima!!))
        ))

        //don't calculate any points if the current route is not saved or not a previously existing one
        val cancelled = intent.getBooleanExtra("cancelled", false)
        if (!cancelled) {
            //update ptosRanking attribute of the user
            var ptosRuta = 0
            val base = 500 //base value
            var ptosRanking: Long
            var ptos = 0
            var repeated = 1.0F
            var top = 1 //we start on 1 because we would have to add 1 anyway to get users' position in the ranking
            val difficulty: Float
            var totalDistance = 0.0
            lateinit var ruta: String

            //calculate the difficulty of the route
            if (distancia!! < 25F) {
                difficulty = when (altitudMaxima) {
                    in 1F..599F -> 1.0F
                    in 600F..999F -> 1.5F
                    else -> 2.0F
                }
            } else if (distancia!! >= 25F && distancia!! < 40F) {
                difficulty = when (altitudMaxima) {
                    in 1F..999F -> 1.0F
                    in 1000F..1499F -> 1.5F
                    else -> 2.0F
                }
            } else {
                difficulty = when (altitudMaxima) {
                    in 1F..1499F -> 1.0F
                    in 1500F..2499F -> 1.5F
                    else -> 2.0F
                }
            }
            //obtain current route id
            val name: String = if (guardarRuta) {
                nombreRuta.toString()
            } else {
                rutaRealizada.toString()
            }
            db.collection("rutas")
                    .whereEqualTo("nombre", name)
                    .get().addOnSuccessListener { it ->
                        for (document in it) {
                            ruta = document.id
                            totalDistance = document.data["distancia"] as Double
                            Log.i(TAG, "Identificador de la ruta: $ruta")
                        }
                        //register route completion to Firebase
                        val horas : Double = floor(((duracion!!/3600000L).toDouble()))
                        val minutos : Double = floor(((duracion!!-horas*3600000L)/60000L))
                        val segundos : Double = floor((duracion!!-(horas*3600000L)-(minutos*60000L))/1000L)
                        val milis : Double = duracion!!-(horas*3600000L)-(minutos*60000L)-(segundos*1000L)
                        val registroRuta = hashMapOf(
                                "idUsuario" to firebaseAuth.currentUser!!.email!!,
                                "idRuta" to ruta,
                                "fecha" to Timestamp.now(), //mostrar con un formato rapido
                                "horas" to horas,
                                "minutos" to minutos,
                                "segundos" to segundos,
                                "milis" to milis,
                                "kms" to distancia
                        )
                        // añadimos el documento a la base de datos
                        db.collection("rutasUsuarios").document()
                                .set(registroRuta)
                                .addOnSuccessListener { Log.i(TAG, "Registro de ruta guardado en Firebase") }
                                .addOnFailureListener { Log.i(TAG, "Error al guardar el registro de ruta en Firebase")}
                        //check if route is repeated by the user
                        db.collection("rutasUsuarios")
                                .whereEqualTo("idUsuario", firebaseAuth.currentUser!!.email!!)
                                .get().addOnSuccessListener {
                                    for (document in it) {
                                        val rutaItem: String = document.data["idRuta"] as String
                                        if (rutaItem == ruta) {
                                            repeated = 0.5F //this value halves the points if the route is repeated
                                        }
                                    }
                                }
                        //check the users' position in ranking within the users who completed the route
                        db.collection("rutasUsuarios")
                                .whereEqualTo("idRuta", ruta)
                                .get().addOnSuccessListener { it ->
                                    for (document in it) if (document.data["idUsuario"] != firebaseAuth.currentUser!!.email!!) {
                                        val hours = document.data["horas"] as Double
                                        val mins = document.data["minutos"] as Double
                                        val secs = document.data["segundos"] as Double
                                        val mils = document.data["milis"] as Double
                                        val time = hours * 3600000 + mins * 60000 + secs * 1000 + mils
                                        if (time < duracion!!) {
                                            top++
                                        }
                                    }
                                    //if the user is not in the top 100, doesnt receive top points, otherwise gets points from 5-500 (top 100-1) with step 5
                                    top = if (top > 100) {
                                        0
                                    } else {
                                        val i = 500 - ((top - 1) * 5)
                                        i
                                    }
                                    //perform route points calculation
                                    if (distancia!! >= totalDistance) {
                                        ptosRuta = ((top + (base * difficulty)) * repeated).roundToInt()
                                    }
                                    if (distancia!! < 2000.0) {
                                        ptosRuta = (distancia!! / 100).roundToInt()
                                    }
                                    //get current ranking points for the user
                                    db.collection("usuarios").document(firebaseAuth.currentUser!!.email!!.toString()).get()
                                            .addOnSuccessListener {
                                                ptosRanking = it.get("ptosRanking") as Long
                                                ptos = ptosRanking.toInt()
                                                Log.i(TAG,"The user currently has $ptos points.")

                                                //update ranking points value
                                                db.collection("usuarios")
                                                        .document(firebaseAuth.currentUser!!.email!!.toString())
                                                        .update("ptosRanking", ptos + ptosRuta)
                                            }
                                }
                    }
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map2) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        val btnFinalizar : Button = findViewById(R.id.buttonEstFinalizar)
        btnFinalizar.setOnClickListener {
            //Toast.makeText(this, "Se finaliza de ver el resumen ruta", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, RankingActivity::class.java)
            startActivity(intent)
        }
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
    }

    //==============================================================================================
    // ASYNCTASK - Tarea asíncrona para cargar el kml y que se produzcan fallos cuando contiene
    // muchos puntos
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
                Log.d(TAG, "Archivo KML no encontrado")
            } catch (e: SAXException) {
                Log.d(TAG, "Error SAX - ${e.toString()}")
            } catch (e: IOException) {
                Log.d(TAG, "Error IO - ${e.toString()}")
            }
            return true
        }

        override fun onPostExecute(aBoolean: Boolean) {
            mapa.addPolyline(handler?.getRuta()) // Se añade una ruta.

            // Se mueve la cámara a la última posición.
            mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(handler?.getLastCoordenadas(), 17f))

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
        //calendar.add(Calendar.HOUR_OF_DAY, -1)

        return formatter.format(calendar.time)
    }
}