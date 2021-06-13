package com.muei.apm.fasterwho

import android.Manifest
import android.annotation.SuppressLint
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Chronometer
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.xml.sax.SAXException
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.lang.Math.abs
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.xml.parsers.ParserConfigurationException
import javax.xml.parsers.SAXParser
import javax.xml.parsers.SAXParserFactory


class SeguimientoActivity : AppCompatActivity(), OnMapReadyCallback,GoogleMap.OnMyLocationButtonClickListener {
    private lateinit var registro: RegistradorKML
    private var parser: SAXParser? = null
    private var handler: SaxHandler? = null
    private lateinit var ruta: Polyline
    private lateinit var mMap: GoogleMap
    private val TAG = "SeguimientoActivity"
    private val REQUEST_FINE_LOCATION_PERMISSIONS_REQUEST_CODE = 34
    private val REQUEST_BACKGROUND_LOCATION_PERMISSIONS_REQUEST_CODE = 56
    private lateinit var intentService: Intent

    // Servicio para poder recibir actualizacioens de localización en background
    private lateinit var foregroundLocationService: ForegroundLocationService
    private var mBound: Boolean = false
    private lateinit var connection: ServiceConnection
    private var distancia = 0.0
    private var velocidadMaxima = 0.0
    private var horaInicio: String? = null
    private var duracion = 0L
    private var altitudInicial = 0.0
    private var altitudMaxima: Double? = null
    private var altitudMinima: Double? = null
    private var nombreArchivoRuta: String? = null
    private var file: String? = null
    private val firebaseAuth = FirebaseAuth.getInstance()
    private var permisosConcedidos: Boolean = false
    private var textoBoton : TextView? = null
    private var velocidades: List<Float>? = null
    private var rutaRealizada: String? = null

    /**
     * Provides the entry point to the Fused Location Provider API.
     */
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRepository: LocationUpdateViewModel

    private var chronometer: Chronometer? = null

    private val viewModel: SeguimientoViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_seguimiento)

        // Se obtiene el provider para poder acceder a las funcionalidades de localización
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Definimos el nombre del archivo KML de la ruta
        nombreArchivoRuta = "ruta-" + firebaseAuth.currentUser.email + getDate() + ".kml"
        registro = RegistradorKML(this, nombreArchivoRuta!!)

        locationRepository = run {
            ViewModelProviders.of(this).get(LocationUpdateViewModel::class.java)
        }
        // borramos en BD posibles localizaciones de una ruta anterior
        locationRepository.deleteLocations()

        // inicializamos el cronometro
        chronometer = findViewById(R.id.chronometer)

        // Comprobamos si esta activity se llama desde RutaActivity y obtenemos el kml
        // y el nombre de la ruta
        file = intent.getStringExtra("file")
        rutaRealizada = intent.getStringExtra("rutaRealizada")

        // función para lanzar el popup que muestra si se quiere guardar la ruta
        fun launchPopUp() {
            val popUpFragment = SaveRouteDialogFragment()

            // Le pasamos la distancia y la velocidad al pop up
            var args = Bundle()

            args.putDouble("velocidad", velocidadMaxima)
            args.putDouble("distancia", distancia)
            args.putString("horaInicio", horaInicio)
            args.putLong("duracion", duracion)
            args.putString("nombreArchivoRuta", nombreArchivoRuta)

            if (altitudMaxima != null) {
                args.putDouble("altitudGanada", altitudMaxima!! - altitudInicial)
                args.putDouble("altitudMaxima", altitudMaxima!!)
            }

            if (altitudMinima != null) {
                args.putDouble("altitudPerdida", altitudInicial - altitudMinima!!)
            }

            // primera localizacion
            args.putDouble("latitud_inicial", ruta.points.first().latitude)
            args.putDouble("longitud_inicial", ruta.points.first().longitude)
            // última localización
            args.putDouble("latitud_final", ruta.points.last().latitude)
            args.putDouble("longitud_final", ruta.points.last().longitude)

            // velocidad media
            if (velocidades != null && velocidades!!.isNotEmpty()) {
                val velocidadMedia = velocidades!!.average()
                args.putDouble("velocidadMedia", velocidadMedia)
            }

            popUpFragment.arguments = args
            popUpFragment.show(supportFragmentManager, "Save Route")
        }

        val btnFinalizarRuta: Button = findViewById(R.id.finalizar_ruta_button)
        textoBoton = findViewById(R.id.finalizar_ruta_button)
        btnFinalizarRuta.setOnClickListener {
            // si hay permisos se pasa a mostrar las estadisticas de la ruta hecha,
            // en caso contrario se vuelve al inicio
            if (permisosConcedidos) {
                // escribimos los puntos al fichero
                runBlocking {
                    launch {
                        // abrimos el fichero KML para comenzar a guardar los puntos en el
                        registro.abrirFichero()
                        for (i in ruta.points) {
                            registro.anhadirPunto(i.latitude, i.longitude, 0.0)
                        }
                        // se cierra el fichero KML
                        registro.cerrarFichero()
                    }
                }

                // se para el cronometro
                chronometer?.stop()

                // calculamos el tiempo de duración de la actividad
                duracion = (SystemClock.elapsedRealtime() - chronometer?.base!!)

                // Se para el broadcast que recibe las actualizaciones
                locationRepository.stopLocationUpdates()
                // se cierra el servicio que permite recibir actualizaciones en background
                if (mBound)
                    unbindService(connection)
                stopService(Intent(baseContext, ForegroundLocationService::class.java))

                // Si es una ruta nueva lanzamos el popup para saber si se quiere guardar la ruta
                if (file == null) {
                    launchPopUp()
                } else {
                    // En caso contrario llamamos directamente a EstadisticasActivity
                    val intent = Intent(this, EstadisticasActivity::class.java)
                    intent.putExtra("guardarRuta", false)
                    if (distancia != null) {
                        intent.putExtra("distancia", distancia)
                    }
                    if (velocidadMaxima != null) {
                        intent.putExtra("velocidad", velocidadMaxima)
                    }

                    if (horaInicio != null) {
                        intent.putExtra("horaInicio", horaInicio)
                    }
                    if (duracion != null) {
                        intent.putExtra("duracion", duracion)
                    }
                    if (altitudMaxima != null) {
                        intent.putExtra("altitudGanada", altitudMaxima!! - altitudInicial)
                        intent.putExtra("altitudMaxima", altitudMaxima!!)
                    }
                    if (altitudMinima != null) {
                        intent.putExtra("altitudPerdida", altitudInicial - altitudMinima!!)
                    }
                    if (rutaRealizada != null) {
                        intent.putExtra("rutaRealizada", rutaRealizada)
                    }

                    // velocidad media
                    if (velocidades != null && !velocidades!!.isEmpty()) {
                        val velocidadMedia = velocidades!!.average()
                        intent.putExtra("velocidadMedia", velocidadMedia)
                    }
                    // nombre del archivo kml
                    intent.putExtra("nombreArchivoRuta", nombreArchivoRuta)
                    // nombre de la ruta
                    intent.putExtra("rutaRealizada", rutaRealizada)

                    startActivity(intent)
                }
            } else {
                val intent = Intent(this, InicioActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Comprobamos los permisos
        if (!checkPermissions()) {
            requestPermissions()
        } else {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            // Tenemos permisos para la geolcalización, llamamos al método que obitene las actualizaciones
            permisosConcedidos = true
            obtenerLocalizaciones()

        }

    }

    /**
     * Método que implementa la lógica necesaria para obtener las actualizaciones de la geolocalización
     */
    fun obtenerLocalizaciones() {
        // Obtenemos la hora en la que se inicia la actividad
        val horaActual = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime.now()
        } else {
            TODO("VERSION.SDK_INT < O")
        }

        // Comprobamos si esta activity se llama desde RutaActivity, y en ese caso mostramos
        // la el recorrido de la ruta que se quiere realizar
        if (file != null) {
            Log.d(TAG, "En seguimientoActivity se va a mostrar la ruta a seguir")

            val latitud_fin = intent.getDoubleExtra("latitud_fin", 0.0)
            val longitud_fin = intent.getDoubleExtra("longitud_fin", 0.0)
            val longitud_ini = intent.getDoubleExtra("longitud_ini", 0.0)
            val latitud_ini = intent.getDoubleExtra("latitud_ini", 0.0)

            val factory = SAXParserFactory.newInstance()
            // intentamos cargar el kml
            try {
                parser = factory.newSAXParser()

                // Manejador SAX programado por nosotros. Le pasamos nuestro mapa para que ponga los puntos.
                handler = SaxHandler(mMap)

                // AsyncTask. Le pasamos el directorio de ficheros como string.
                val procesador: SeguimientoActivity.ProcesarKML = ProcesarKML()
                procesador.execute(this.filesDir.absolutePath)
            } catch (e: SAXException) {
                println(e.message)
                Log.d(TAG, "Error SAX al leer el KML")
            } catch (e: ParserConfigurationException) {
                println(e.message)
                Log.d(TAG, "Error de parser al leer el KML")
            }
            // se añade el punto de inicio
            mMap.addMarker(
                MarkerOptions().position(LatLng(latitud_ini, longitud_ini))
                    .icon(
                        BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.HUE_GREEN
                        )
                    ).title("Inicio")
            )
            // se añade el punto de fun
            mMap.addMarker(
                MarkerOptions().position(LatLng(latitud_fin, longitud_fin))
                    .icon(
                        BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.HUE_ORANGE
                        )
                    ).title("Fin")
            )
            // se mueve la cámara a la posición inicial
            val cameraPosition: CameraPosition =
                CameraPosition.Builder().target(LatLng(latitud_ini, longitud_ini))
                    .zoom(15.3f)
                    .bearing(0f)
                    .tilt(25f)
                    .build()
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }

        // Se añade esto para pintar el tramo de la ruta realizado
        var opcionesPolyLine = PolylineOptions().color(Color.BLUE).width(8F)
        ruta = mMap.addPolyline(opcionesPolyLine)

        // Vamos actualizando la posición actual en tiempo real
        mMap.setOnMyLocationButtonClickListener(this)
        enableMyLocation()
        var context: Context = this
        var pendingIntent = locationRepository.startLocationUpdates()

        /* se añade esto para poder crear el servicio de background*/
        connection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder = service as ForegroundLocationService.BinderLocationService
                foregroundLocationService = binder.getService()
                mBound = true
                foregroundLocationService.pendingIntent = pendingIntent
                foregroundLocationService.context = context
                startService(intentService)
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                mBound = false
            }
        }
        // nos suscribimos al servicio para que se inicie
        intentService = Intent(this, ForegroundLocationService::class.java)
        bindService(intentService, connection, Context.BIND_AUTO_CREATE)

        // variables para comenzar a calcular la distancia recorrida
        var puntoDescartado = false
        var primeraLocalizacion = true
        var previousLocation = LatLng(0.0, 0.0)
        var resultado = FloatArray(1)
        // se comprueba las actualizaciones recibidas
        locationRepository.locationListLiveData.observe(
            this, androidx.lifecycle.Observer { locations ->
                locations?.let {
                    Log.d(TAG, "Se obtienen ${locations.size} localizaciones")
                    if (locations.isEmpty()) {
                        Log.d(TAG, "Error localizaciones")
                    } else {
                        // se actualiza la línea del mapa con los nuevos puntos y se centra la cámara
                        ruta.points = locations

                        val posicion = locations.last()
                        mMap.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(posicion, 17f),
                            2500,
                            null
                        )

                        // Calculamos la distancia recorrida
                        if (!primeraLocalizacion) {
                            Location.distanceBetween(
                                previousLocation.latitude,
                                previousLocation.longitude,
                                posicion.latitude,
                                posicion.longitude,
                                resultado
                            )
                            distancia += abs(resultado[0])
                            previousLocation = posicion

                            // enviamos al fragment la nueva distancia
                            viewModel.setDistance(distancia)

                        } else {
                            Toast.makeText(this, "¡Comienza la ruta!", Toast.LENGTH_SHORT).show()
                            previousLocation = posicion
                            primeraLocalizacion = false
                            horaInicio = horaActual.format(DateTimeFormatter.ofPattern("HH:mm"))
                            // inicializamos el cronometro
                            chronometer?.base = SystemClock.elapsedRealtime()
                            chronometer?.start()

                        }

                        Log.i(TAG, "Distancia total hasta el momento: " + distancia)
                    }

                }
            }

        )

        // Obtenemos las velocidades hasta el momento
        locationRepository.speeds.observe(
            this,
            androidx.lifecycle.Observer { speeds ->
                speeds?.let {
                    if (speeds != null && speeds.isNotEmpty()) {
                        velocidades = speeds
                        velocidadMaxima = speeds.max()!!.toDouble()
                    }
                }
            }
        )

        var primeraAltitud = true
        // Obtenemos la primera altitud inicial, y la altitud máxima y mínima
        locationRepository.altitudes.observe(
            this, androidx.lifecycle.Observer { altitudes ->
                altitudes?.let {
                    Log.i(TAG, "Leyendo ${altitudes.size} altitudes")
                    if (altitudes.size > 0) {
                        if (primeraAltitud) {

                            altitudInicial = altitudes.last()
                            Log.i(TAG, "Altitud inicial= $altitudInicial")
                            primeraAltitud = false
                        } else {
                            if (altitudes.min()!! < altitudInicial) {
                                altitudMinima = altitudes.min()!!
                            }
                            if (altitudes.max()!! >= altitudInicial) {
                                Log.i(TAG, "Nueva altitud Máxima= $altitudMaxima")
                                altitudMaxima = altitudes.max()!!
                            }
                        }
                    }

                }

            }
        )
    }

    /**
     * Shows a [Snackbar].
     *
     * @param snackStrId The id for the string resource for the Snackbar text.
     * @param actionStrId The text of the action item.
     * @param listener The listener associated with the Snackbar action.
     */
    private fun showSnackbar(
        snackStrId: String,
        actionStrId: Int = 0,
        listener: View.OnClickListener? = null
    ) {
        val snackbar = Snackbar.make(
            findViewById(android.R.id.content), snackStrId,
            Snackbar.LENGTH_INDEFINITE
        )
        if (actionStrId != 0 && listener != null) {
            snackbar.setAction(getString(actionStrId), listener)
        }
        snackbar.show()
    }

    /**
     * Return the current state of the permissions needed.
     */
    private fun checkPermissions() =
        /*ActivityCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED*/
        ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    private fun startLocationPermissionRequest() {
        /*ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
            REQUEST_PERMISSIONS_REQUEST_CODE)*/
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            REQUEST_BACKGROUND_LOCATION_PERMISSIONS_REQUEST_CODE
        )
    }

    private fun requestPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        ) {
            // Provide an additional rationale to the user. This would happen if the user denied the
            // request previously, but didn't check the "Don't ask again" checkbox.
            Log.i(TAG, "Displaying permission rationale to provide additional context.")
            showSnackbar(
                "Se necesitan permisos GPS para la funcionalidad principal",
                android.R.string.ok,
                View.OnClickListener {
                    // Request permission
                    startLocationPermissionRequest()
                })

        } else {
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            Log.i(TAG, "Requesting permission")
            startLocationPermissionRequest()
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        Log.i(TAG, "onRequestPermissionResult")
        when (requestCode) {
            REQUEST_FINE_LOCATION_PERMISSIONS_REQUEST_CODE,
            REQUEST_BACKGROUND_LOCATION_PERMISSIONS_REQUEST_CODE ->
                when  {
                    // If user interaction was interrupted, the permission request is cancelled and you
                    // receive empty arrays.
                    grantResults.isEmpty() -> Log.i(TAG, "User interaction was cancelled.")

                    // Permission granted.
                    (grantResults[0] == PackageManager.PERMISSION_GRANTED) -> {

                        textoBoton?.setText("FINALIZAR RUTA")
                        permisosConcedidos = true
                        obtenerLocalizaciones()
                    }

                    // Permission denied.

                    // Notify the user via a SnackBar that they have rejected a core permission for the
                    // app, which makes the Activity useless. In a real app, core permissions would
                    // typically be best requested during a welcome-screen flow.

                    // Additionally, it is important to remember that a permission might have been
                    // rejected without asking the user for permission (device policy or "Never ask
                    // again" prompts). Therefore, a user interface affordance is typically implemented
                    // when permissions are denied. Otherwise, your app could appear unresponsive to
                    // touches or interactions which have required permissions.
                    else -> {
                        textoBoton?.setText("Volver a Inicio")
                        permisosConcedidos = false
                        showSnackbar("Permisos denegados", R.string.settings,
                            View.OnClickListener {
                                // Build intent that displays the App settings screen.
                                val intent = Intent().apply {
                                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                    data =
                                        Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                }
                                startActivity(intent)
                            })
                    }
                }
        }
    }


    private fun isPermissionsGranted() = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION
    ) == PackageManager.PERMISSION_GRANTED


    /**
     * Método para activar el seguimiento en tiempo real
     */
    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if (!::mMap.isInitialized) return
        if (isPermissionsGranted()) {
            mMap.isMyLocationEnabled = true
        } else {
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
            Toast.makeText(this, "Ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                REQUEST_BACKGROUND_LOCATION_PERMISSIONS_REQUEST_CODE)
        }
    }

    override fun onMyLocationButtonClick(): Boolean {
        //Toast.makeText(this, "Centrando el mapa", Toast.LENGTH_SHORT).show()

        return false
    }

    /**
     * Método que comprueba si se tienen los permisos cuando se sale de la app y se vuelve a abrir
     */
    @SuppressLint("MissingPermission")
    override fun onResumeFragments() {
        super.onResumeFragments()
        if (!::mMap.isInitialized) return
        if(!isPermissionsGranted()){
            mMap.isMyLocationEnabled = false
            Toast.makeText(this, "Para activar la localización ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Método para obtener la fecha actual en el formato yyyyMMddHHmmss
     */
    private fun getDate() :String {
        val formatter = SimpleDateFormat("yyyyMMddHHmmss")
        val calendar: Calendar = Calendar.getInstance()

        return formatter.format(calendar.time)
    }

    //==============================================================================================
    // ASYNCTASK - Tarea asíncrona para cargar el kml y que se produzcan fallos cuando contiene
    // muchos puntos
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