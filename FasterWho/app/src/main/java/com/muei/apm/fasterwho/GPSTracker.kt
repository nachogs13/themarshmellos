package com.muei.apm.fasterwho

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*

class GPSTracker(contexto: Activity, registrador: RegistradorKML) : AppCompatActivity(),
    GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener {

    private val LOGTAG = "android-localizacion"
    private val PETICION_PERMISO_LOCALIZACION = 101
    private val PETICION_CONFIG_UBICACION = 201

    public lateinit var apiClient: GoogleApiClient
    private lateinit var contexto: Activity
    private lateinit var registrador: RegistradorKML
    private lateinit var locRequest: LocationRequest

    /**
     * Constructor
     */
    init {
        this.contexto = contexto
        this.registrador = registrador
        // Construcción cliente API Google
        apiClient = GoogleApiClient.Builder(this.contexto)
            .enableAutoManage(this,this)
            .addConnectionCallbacks(this)
            .addApi(LocationServices.API)
            .build()
        apiClient.connect()
    }

    /* Se implementan los métodos necesarios */
    fun toggleLocationUpdates(enable: Boolean) {
        if (enable) {
            enableLocationUpdates()
        } else {
            disableLocationUpdates()
        }
    }

    /**
     * Método importante en el cual se configuran los updates
     */
    fun enableLocationUpdates() {
        locRequest = LocationRequest()
        // estos parametros puede que los haya que configurar desde un lugar más adecuado
        locRequest.setInterval(1000)
        locRequest.setFastestInterval(600)
        locRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        val locSettingsRequest = LocationSettingsRequest.Builder()
            .addLocationRequest(locRequest)
            .build()

        val result = LocationServices.SettingsApi.checkLocationSettings(
            apiClient, locSettingsRequest
        )

        result.setResultCallback { locationSettingsResult ->
            val status = locationSettingsResult.status
            when (status.statusCode) {
                LocationSettingsStatusCodes.SUCCESS -> {
                    Log.i(LOGTAG, "Configuración correcta")
                    startLocationUpdates()
                }
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                    Log.i(LOGTAG, "Se requiere actuación del usuario")
                    status.startResolutionForResult(contexto, PETICION_CONFIG_UBICACION)
                } catch (e: SendIntentException) {
                    // Pasa algo raro al pedir configurar ubicacion
                    Log.i(LOGTAG, "Error al intentar solucionar configuración de ubicación")
                }
                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> Log.i(LOGTAG, "No se puede cumplir la configuración de ubicación necesaria")
            }
        }
    }

    private fun disableLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(apiClient, this)

    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                contexto,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) === PackageManager.PERMISSION_GRANTED
        ) {

            //Ojo: estamos suponiendo que ya tenemos concedido el permiso.
            //Sería recomendable implementar la posible petición en caso de no tenerlo.
            Log.i(LOGTAG, "Inicio de recepción de ubicaciones")
            LocationServices.FusedLocationApi.requestLocationUpdates(
                apiClient, locRequest, this
            )
        }
    }
    override fun onConnectionFailed(p0: ConnectionResult) {

        //Se ha producido un error que no se puede resolver automáticamente
        //y la conexión con los Google Play Services no se ha establecido.
        Log.e(LOGTAG, "Error grave al conectar con Google Play Services")
    }


    override fun onConnected(bundle: Bundle?) {
        //Conectado correctamente a Google Play Services
        if (ActivityCompat.checkSelfPermission(
                contexto,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) !== PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                contexto, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PETICION_PERMISO_LOCALIZACION
            )
        } else {
            val lastLocation = LocationServices.FusedLocationApi.getLastLocation(apiClient)
            //updateUI(lastLocation)
        }
    }

    override fun onConnectionSuspended(i: Int) {
        //Se ha interrumpido la conexión con Google Play Services
        Log.e(LOGTAG, "Se ha interrumpido la conexión con Google Play Services")
    }

    /**
     * TODO método importante
     * Se lanza periódicamente si hemos activado las actualizaciones
     * @param loc
     */
    private fun updateUI(loc: Location?) {
        if (loc != null) {
            registrador.anhadirPunto(loc.latitude, loc.longitude, loc.altitude)
        } else {
            // Si entramos aquí es porque las coordenadas son desconocidas
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // !! el super lo añado yo
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            PETICION_CONFIG_UBICACION -> when (resultCode) {
                RESULT_OK -> startLocationUpdates()
                RESULT_CANCELED -> Log.i(LOGTAG, "El usuario no ha realizado los cambios de configuración necesarios"
                )
            }
        }
    }

    /**
     * TODO Método que se lanza cuando recién actualiza
     * @param location
     */
    override fun onLocationChanged(location: Location?) {
        Log.i(LOGTAG, "Recibida nueva ubicación!")

        //Mostramos la nueva ubicación recibida
        updateUI(location)
        
    }

}