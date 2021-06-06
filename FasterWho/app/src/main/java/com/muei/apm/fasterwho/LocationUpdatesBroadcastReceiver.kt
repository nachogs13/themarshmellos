package com.muei.apm.fasterwho

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import com.muei.apm.fasterwho.db.MyLocationAccessor
import com.muei.apm.fasterwho.db.MyLocationEntity
import java.util.*
import java.util.concurrent.Executors

private val TAG = "BroadcastReceiverLocation"

/**
 * Clase que hereda de BoadcasReceiver y se encarga de recibir las nuevas actualizaciones de
 * geolocalización
 */
class LocationUpdatesBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.i(TAG, "onReceive() context:$context, intent:$intent")

        if (intent.action == ACTION_PROCESS_UPDATES) {

            // Checks for location availability changes.
            LocationAvailability.extractLocationAvailability(intent)?.let { locationAvailability ->
                if (!locationAvailability.isLocationAvailable) {
                    Log.i(TAG, "Location services are no longer available!")
                }
            }

            LocationResult.extractResult(intent)?.let { locationResult ->
                val locations = locationResult.locations.map { location ->
                    Log.i(TAG, "Altitud actual ${location.altitude}")
                    MyLocationEntity(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        foreground = isAppInForeground(context),
                        position = LatLng(location.latitude, location.longitude),
                        date = Date(location.time),
                        speed = location.speed * 3.6F, // Lo multiplicamos por 3.6 para pasar
                                                        // de m/s a km/h
                        altitude = location.altitude
                    )
                }
                if (locations.isNotEmpty()) {
                    MyLocationAccessor.getInstance(context, Executors.newSingleThreadExecutor())
                        .addLocations(locations)
                }
            }
        }
    }

    /**
     * Este método solo se utiliza para saber si las actualizaciones de geolocalización se obtienen
     * en bacground o foreground. Lo dejamos para debug
     */
    private fun isAppInForeground(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager.runningAppProcesses ?: return false

        appProcesses.forEach { appProcess ->
            if (appProcess.importance ==
                ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
                appProcess.processName == context.packageName) {
                return true
            }
        }
        return false
    }

    companion object {
        const val ACTION_PROCESS_UPDATES =
            "com.muei.apm.fasterwho.action.PROCESS_UPDATES"
    }
}