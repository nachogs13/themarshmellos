package com.muei.apm.fasterwho.db

import android.Manifest
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.MainThread
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startForegroundService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.muei.apm.fasterwho.ForegroundLocationService
import com.muei.apm.fasterwho.LocationUpdatesBroadcastReceiver
import java.util.concurrent.TimeUnit

private const val TAG = "MyLocationManager"

/**
 * Manages all location related tasks for the app.
 */
class MyLocationManager private constructor(private val context: Context) {

    private val _receivingLocationUpdates: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)

    /**
     * Status of location updates, i.e., whether the app is actively subscribed to location changes.
     */
    val receivingLocationUpdates: LiveData<Boolean>
        get() = _receivingLocationUpdates

    // The Fused Location Provider provides access to location APIs.
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    // Stores parameters for requests to the FusedLocationProviderApi.
    private val locationRequest: LocationRequest = LocationRequest().apply {
        // Sets the desired interval for active location updates. This interval is inexact. You
        // may not receive updates at all if no location sources are available, or you may
        // receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        //
        // IMPORTANT NOTE: Apps running on "O" devices (regardless of targetSdkVersion) may
        // receive updates less frequently than this interval when the app is no longer in the
        // foreground.
        interval = TimeUnit.SECONDS.toMillis(5)

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        fastestInterval = TimeUnit.SECONDS.toMillis(5)

        // Sets the maximum time when batched location updates are delivered. Updates may be
        // delivered sooner than this interval.
        maxWaitTime = TimeUnit.SECONDS.toMillis(5)

        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    /**
     * Creates default PendingIntent for location changes.
     *
     * Note: We use a BroadcastReceiver because on API level 26 and above (Oreo+), Android places
     * limits on Services.
     */
    private val locationUpdatePendingIntent: PendingIntent by lazy {
        val intent = Intent(context, LocationUpdatesBroadcastReceiver::class.java)
        intent.action = LocationUpdatesBroadcastReceiver.ACTION_PROCESS_UPDATES
        PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    /**
     * Usamos un servicio para poder seguir recibiendo actualizaciones en background, ya que desde
     * el API 26 estas se reducen a sol un par de actualizaciones por hora para poder ahorras batería
     */
    /*private val notification: Notification = Notification.Builder(context)
        .setContentIntent(locationUpdatePendingIntent)
        .setContentTitle("FasterWho?")
        .setContentText("Se está usando la ubicación")
        .build()*/


    /**
     * Uses the FusedLocationProvider to start location updates if the correct fine locations are
     * approved.
     *
     * @throws SecurityException if ACCESS_FINE_LOCATION permission is removed before the
     * FusedLocationClient's requestLocationUpdates() has been completed.
     */
    @Throws(SecurityException::class)
    @MainThread
    fun startLocationUpdates() : PendingIntent? {
        Log.i(TAG, "startLocationUpdates()--------------------------------------------------------------")

        if (!context.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) return null

        try {
            _receivingLocationUpdates.value = true
            // If the PendingIntent is the same as the last request (which it always is), this
            // request will replace any requestLocationUpdates() called before.
            fusedLocationClient.requestLocationUpdates(locationRequest, locationUpdatePendingIntent)

            //servicio.startService(context,locationUpdatePendingIntent)
            Log.i(TAG,"Recibiendo localizaciones--------------------------")
            return locationUpdatePendingIntent
        } catch (permissionRevoked: SecurityException) {
            _receivingLocationUpdates.value = false

            // Exception only occurs if the user revokes the FINE location permission before
            // requestLocationUpdates() is finished executing (very rare).
            Log.d(TAG, "Location permission revoked; details: $permissionRevoked")
            throw permissionRevoked
        }
    }

    @MainThread
    fun stopLocationUpdates() {
        Log.d(TAG, "stopLocationUpdates()")
        _receivingLocationUpdates.value = false
        //ForegroundLocationService(locationUpdatePendingIntent).stopService(context)
        fusedLocationClient.removeLocationUpdates(locationUpdatePendingIntent)
    }

    companion object {
        @Volatile private var INSTANCE: MyLocationManager? = null

        fun getInstance(context: Context): MyLocationManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: MyLocationManager(context).also { INSTANCE = it }
            }
        }
    }

    /**
     * Helper functions to simplify permission checks/requests.
     */
    fun Context.hasPermission(permission: String): Boolean {

        // Background permissions didn't exit prior to Q, so it's approved by default.
        if (permission == Manifest.permission.ACCESS_BACKGROUND_LOCATION &&
            android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {
            return true
        }

        return ActivityCompat.checkSelfPermission(this, permission) ==
                PackageManager.PERMISSION_GRANTED
    }


}