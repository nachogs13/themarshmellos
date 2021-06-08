package com.muei.apm.fasterwho

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.RatingBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.maps.android.data.kml.KmlLayer
import com.muei.apm.fasterwho.BuildConfig.APPLICATION_ID

class IniciarRutaActivity : AppCompatActivity(), OnMapReadyCallback {
    // Añado esto para la integración de geolocalizacion
    private val TAG = "IniciarRutaActivity"
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 34
    private var longitude: Double? = null
    private var latitude: Double? = null

    /**
     * Provides the entry point to the Fused Location Provider API.
     */
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var mMap: GoogleMap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_iniciar_ruta)

        // Añado esto para la integracion de la geolocalizacion
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapa_ruta) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val btnIniciarRuta : Button = findViewById(R.id.iniciar_ruta_button)
        btnIniciarRuta.setOnClickListener {
            //Toast.makeText(this, "Se inicia seguimiento de actividad", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, SeguimientoActivity::class.java)
            // Le pasamos a SeguimientoActivity el fichero KML para que muestre la ruta
            intent.putExtra("file", this.intent.getStringExtra("file"))
            intent.putExtra("latitud_fin", this.intent.getDoubleExtra("latitud_fin", 0.0))
            intent.putExtra("longitud_fin", this.intent.getDoubleExtra("longitud_fin", 0.0))
            intent.putExtra("longitud_ini", this.intent.getDoubleExtra("longitud_ini", 0.0))
            intent.putExtra("latitud_ini", this.intent.getDoubleExtra("latitud_ini", 0.0))
            startActivity(intent)
        }

        val btnSearch : Button = findViewById(R.id.ubicacion_search_button)
        btnSearch.setOnClickListener {
            Toast.makeText(this, "Buscando Ubicación", Toast.LENGTH_SHORT).show()
        }

    }

    // Añado esta funcion para la integracion con geolocaclizacion
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
                    showSnackbar("No se detectado la localización. Asegúrese de que el GPS está activado")
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
        ActivityCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    private fun startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
            REQUEST_PERMISSIONS_REQUEST_CODE)
    }

    private fun requestPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_COARSE_LOCATION
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
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
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
                                data = Uri.fromParts("package", APPLICATION_ID, null)
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            startActivity(intent)
                        })
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (!checkPermissions()) {
            requestPermissions()
        } else {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
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

                val cameraPosition: CameraPosition = CameraPosition.Builder().
                target(LatLng(latitud_ini, longitud_ini))
                    .zoom(13.5f)
                    .bearing(0f)
                    .tilt(25f)
                    .build()
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            }

            fusedLocationClient.lastLocation
                .addOnCompleteListener { taskLocation ->
                    if (taskLocation.isSuccessful && taskLocation.result != null) {

                        val location = taskLocation.result

                        var longitude2 = location?.longitude
                        var latitude2 = location?.latitude

                        // Add a marker in Sydney and move the camera
                        val posicion = latitude2?.let { longitude2?.let { it1 -> LatLng(it, it1) } }
                        mMap.addMarker(posicion?.let { MarkerOptions().position(it).title("Aquí estás tú!") })
                        if (file_kml == null) {
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(posicion,16f), 2500,null)
                        }
                    } else {
                        Log.w(TAG, "getLastLocation:exception", taskLocation.exception)
                        showSnackbar("No se detectado la localización. Asegúrese de que el GPS está activado")
                    }
                }
        }
    }

}