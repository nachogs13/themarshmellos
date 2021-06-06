package com.muei.apm.fasterwho

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.SystemClock
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Chronometer
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.location.FusedLocationProviderApi
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.google.maps.android.SphericalUtil
import com.google.maps.android.data.kml.KmlLayer
import com.muei.apm.fasterwho.db.MyLocationAccessor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.Executors

class SeguimientoActivity : AppCompatActivity()/*,com.google.android.gms.location.LocationListener*/, OnMapReadyCallback,GoogleMap.OnMyLocationButtonClickListener{
    private lateinit var registro: RegistradorKML
    private lateinit var ruta: Polyline
    private lateinit var mMap: GoogleMap
    private val TAG = "SeguimientoActivity"
    private val REQUEST_FINE_LOCATION_PERMISSIONS_REQUEST_CODE = 34
    private val REQUEST_BACKGROUND_LOCATION_PERMISSIONS_REQUEST_CODE = 56
    private lateinit var intentService : Intent
    // Servicio para poder recibir actualizacioens de localización en background
    private lateinit var foregroundLocationService: ForegroundLocationService
    private var mBound: Boolean = false
    private lateinit var  connection : ServiceConnection
    private var longitude: Double? = null
    private var latitude: Double? = null
    private var distancia = 0.0
    private var velocidadMaxima = 0.0
    private var horaInicio: String? = null
    private var duracion = 0L
    private var altitudInicial = 0.0
    private var altitudMaxima: Double? = null
    private var altitudMinima: Double? = null
    private var nombreArchivoRuta: String? = null
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
        nombreArchivoRuta = "ruta-" +getDate() + ".kml"
        registro = RegistradorKML(this, nombreArchivoRuta!!)

        locationRepository = run {
            ViewModelProviders.of(this).get(LocationUpdateViewModel::class.java)
        }
        // borramos en BD posibles localizaciones de una ruta anterior
        locationRepository.deleteLocations()

        // inicializamos el cronometro
        chronometer = findViewById(R.id.chronometer)

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

            if (altitudMinima != null){
                args.putDouble("altitudPerdida", altitudInicial - altitudMinima!!)
            }

            popUpFragment.arguments = args
            popUpFragment.show(supportFragmentManager, "Save Route")
        }

        val btnFinalizarRuta : Button = findViewById(R.id.finalizar_ruta_button)
        btnFinalizarRuta.setOnClickListener {
            chronometer?.stop()

            // calculamos el tiempo de duración de la actividad
            duracion = (SystemClock.elapsedRealtime() - chronometer?.base!!)

            Toast.makeText(this, "Se finaliza el seguimiento", Toast.LENGTH_SHORT).show()

            // Se para el broadcast que recibe las actualizaciones
            locationRepository.stopLocationUpdates()
            // se cierra el servicio que permite recibir actualizaciones en background
            if (mBound)
                unbindService(connection)
            stopService(Intent(baseContext, ForegroundLocationService::class.java))

            // se cierra el fichero KML
            registro.cerrarFichero()

            // lanzamos el popup para saber si se quiere guardar la ruta
            launchPopUp()



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
            // Obtenemos la hora en la que se inicia la actividad
            val horaActual = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LocalDateTime.now()
            } else {
                TODO("VERSION.SDK_INT < O")
            }
            horaInicio = horaActual.format(DateTimeFormatter.ofPattern("HH:mm"))
            chronometer?.start()

            // Comprobamos si esta activity se llama desde RutaActivity
            val file_kml = intent.getStringExtra("file")
            if (file_kml != null) {
                Log.d(TAG, "En seguimientoActivity se va a mostrar la ruta a seguir")

                val latitud_fin = intent.getDoubleExtra("latitud_fin",0.0)
                val longitud_fin = intent.getDoubleExtra("longitud_fin", 0.0)
                val longitud_ini = intent.getDoubleExtra("longitud_ini",0.0)
                val latitud_ini = intent.getDoubleExtra("latitud_ini",0.0)

                val id = resources.getIdentifier(file_kml,"raw",packageName)

                val layer = KmlLayer(mMap,id,this)
                layer.addLayerToMap()
                mMap.addMarker(MarkerOptions().position(LatLng(latitud_ini,longitud_ini))
                    .icon(
                        BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.
                    HUE_GREEN)).title("Inicio"))
                mMap.addMarker(MarkerOptions().position(LatLng(latitud_fin,longitud_fin))
                    .icon(
                        BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.
                    HUE_ORANGE)).title("Fin"))
            }

            // Se añade esto para pintar el tramo de la ruta realizado
            var opcionesPolyLine = PolylineOptions().color(Color.CYAN).width(4F)
            ruta = mMap.addPolyline(opcionesPolyLine)

            // Obtenemos la posición actual y ponemos una marca en el mapa
            fusedLocationClient.lastLocation
                .addOnCompleteListener { taskLocation ->
                    if (taskLocation.isSuccessful && taskLocation.result != null) {
                        val location = taskLocation.result
                        // Obtenemos la latitud y longitud
                        var longitude2 = location?.longitude
                        var latitude2 = location?.latitude

                        // Añadimos la marca
                        val posicion = latitude2?.let { longitude2?.let { it1 -> LatLng(it, it1) } }
                        mMap.addMarker(posicion?.let { MarkerOptions().position(it).title("Empiezaste aquí") })
                        if (file_kml == null) {
                            //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(posicion,16f), 2500,null)
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posicion,16f))
                        }

                    } else {
                        Log.w(TAG, "getLastLocation:exception", taskLocation.exception)
                        showSnackbar("No se detectado la localización. Asegúrese de que el GPS está activado")
                    }
                }

            // Vamos actualizando la posición actual en tiempo real
            mMap.setOnMyLocationButtonClickListener(this)
            enableMyLocation()
            var context: Context = this
            var pendingIntent = locationRepository.startLocationUpdates()

            /* se añade esto para poder crear el servicio */
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
            intentService =Intent(this,ForegroundLocationService::class.java)
            bindService(intentService, connection, Context.BIND_AUTO_CREATE)

            // abrimos el fichero KML para comenzar a guardar los puntos en el
            registro.abrirFichero()

            // variables para comenzar a calcular la distancia recorrida
            var primeraLocalizacion = true
            var previousLocation = LatLng(0.0, 0.0)
            var resultado = FloatArray(1)
            // se comprueba las actualizaciones recibidas
            locationRepository.locationListLiveData.observe(
                this,androidx.lifecycle.Observer { locations ->
                    locations?.let {
                        Log.d(TAG, "Se obtienen ${locations.size} localizaciones")
                        if (locations.isEmpty()) {
                            Log.d(TAG, "Error localizaciones")
                        } else {
                            // se actualiza la línea del mapa con los nuevos puntos y se centra la cámara
                            ruta.points = locations
                            val posicion = locations.last()
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(posicion,16f), 2500,null)
                            Toast.makeText(this,"Puntos " + ruta.points.size, Toast.LENGTH_SHORT).show()

                            // Calculamos la distancia
                            if (!primeraLocalizacion) {
                                Location.distanceBetween(
                                    previousLocation.latitude,
                                    previousLocation.longitude,
                                    posicion.latitude,
                                    posicion.longitude,
                                    resultado
                                )
                                distancia += resultado[0]
                                previousLocation = posicion

                                // enviamos al fragment la nueva distancia
                                viewModel.setDistance(distancia)

                            } else {
                                previousLocation = posicion
                                primeraLocalizacion = false
                            }

                            // Se escribe el punto de geolocalización en el archivo KML
                            registro.anhadirPunto(posicion.latitude, posicion.longitude, 0.0)

                            Log.i(TAG, "Distancia total hasta el momento: " + distancia)
                        }
                    }

                }

            )

            // Obtenemos la velocidad máxima hasta el momento
            locationRepository.speed.observe(
                this,
                androidx.lifecycle.Observer { speed ->
                    speed?.let {
                        velocidadMaxima = speed.toDouble()
                        Log.i(TAG, "Velocidad máxima hasta el momento: $velocidadMaxima")
                    }
                }
            )

            var primeraAltitud = true
            // Obtenemos la primera altitud inicial, y la altitud máxima y mínima
            locationRepository.altitudes.observe(
                this, androidx.lifecycle.Observer { altitudes ->
                    altitudes?.let {
                        Log.i(TAG,"Leyendo ${altitudes.size} altitudes")
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

    }

    // Se añade esta funcion para la integracion con geolocaclizacion
    override fun onStart() {
        super.onStart()

        if (!checkPermissions()) {
            requestPermissions()
        } else {
            getLastLocation()
        }
    }

    /**
     * Provides a simple way of getting a device's location and is well suited for
     * applications that do not require a fine-grained location and that do not need location
     * updates. Gets the best and most recent location currently available, which may be null
     * in rare cases when a location is not available.
     *
     * Note: this method should be called after location permission has been granted.
     */
    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        fusedLocationClient.lastLocation
            .addOnCompleteListener { taskLocation ->
                if (taskLocation.isSuccessful && taskLocation.result != null) {
                    val location = taskLocation.result

                    longitude = location?.longitude
                    latitude = location?.latitude
                } else {
                    Log.w(TAG, "getLastLocation:exception", taskLocation.exception)
                    showSnackbar("No se ha detectado la localización. Asegúrese de que el GPS está activado")
                }
            }
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
        val snackbar = Snackbar.make(findViewById(android.R.id.content), snackStrId,
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
        ActivityCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    private fun startLocationPermissionRequest() {
        /*ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
            REQUEST_PERMISSIONS_REQUEST_CODE)*/
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_BACKGROUND_LOCATION_PERMISSIONS_REQUEST_CODE)
    }

    private fun requestPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )) {
            // Provide an additional rationale to the user. This would happen if the user denied the
            // request previously, but didn't check the "Don't ask again" checkbox.
            Log.i(TAG, "Displaying permission rationale to provide additional context.")
            showSnackbar("Se necesitan permisos GPS para la funcionalidad principal", android.R.string.ok, View.OnClickListener {
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
        if (requestCode == REQUEST_BACKGROUND_LOCATION_PERMISSIONS_REQUEST_CODE) {
            when {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                grantResults.isEmpty() -> Log.i(TAG, "User interaction was cancelled.")

                // Permission granted.
                (grantResults[0] == PackageManager.PERMISSION_GRANTED) -> getLastLocation()

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
                    showSnackbar("Permisos denegados", R.string.settings,
                        View.OnClickListener {
                            // Build intent that displays the App settings screen.
                            val intent = Intent().apply {
                                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
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
        Toast.makeText(this, "Centrando el mapa", Toast.LENGTH_SHORT).show()
        //Log.i(TAG, "Localizacion modificada")
        //Toast.makeText(this, "Localizacion Modificada", Toast.LENGTH_SHORT).show()
        //val locationListLiveData = locationRepository.getLocations()
        /*locationRepository.locationListLiveData.observe(
            this,androidx.lifecycle.Observer { locations ->
                locations?.let {

                    if (locations.isEmpty()) {
                        Log.d(TAG, "Error localizaciones")
                    } else {
                        val points = ruta.points
                        points.add(LatLng(
                            locations.last().latitude, locations.last().longitude
                        ))

                        ruta.points = points
                        Log.d(TAG, "Se obtienen ${locations.size} localizaciones")
                        Toast.makeText(this, "Se obtienen ${locations.size} localizaciones", Toast.LENGTH_SHORT).show()
                    }
                }

            }
        )*/
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
}