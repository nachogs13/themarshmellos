package com.muei.apm.fasterwho.db

import android.content.Context
import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import java.util.concurrent.ExecutorService

/**
 * Clase para acceder a la base de datos y al API de geolocalización
 */
class MyLocationAccessor private constructor(
    private val baseDatos: MyLocationDatabase,
    private val locationManager: MyLocationManager,
    private val executor: ExecutorService
) {
    // Instanciamos el DAO
    private val locationDAO = baseDatos.locationDAO()

    /**
     * Obtiene los puntos almacenados de geolocalización
     */
    fun getLocations(): LiveData<List<MyLocationEntity>> = locationDAO.getLocations()

    /**
     * Permite añadir un punto de geolocalización a la BD
     */
    fun addLocation(myLocationEntity: MyLocationEntity) {
        executor.execute {
            locationDAO.addLocation(myLocationEntity)
        }
    }

    /**
     * Permite añadir una lista de puntos de geolocalización a la BD
     */
    fun addLocations(myLocationEntities: List<MyLocationEntity>) {
        executor.execute {
            locationDAO.addLocations(myLocationEntities)
        }
    }

    /**
     * Permite eliminar todos los puntos de geolocalización de la BD
     */
    fun deleteLocations() {
        executor.execute {
            locationDAO.deleteLocations()
        }
    }
    
    /**
     * Obtiene el estado de si se están recibiendo actualizacioens o no
     */
    val receivingLocationUpdates: LiveData<Boolean> = locationManager.receivingLocationUpdates

    /**
     * Inicia la recepción de actualizaciones de geolocalización
     */
    @MainThread
    fun startLocationUpdates() = locationManager.startLocationUpdates()

    /**
     * Para la recepción de actualizaciones de la geolocalización
     */
    @MainThread
    fun stopLocationUpdates() = locationManager.stopLocationUpdates()

    companion object {
        @Volatile private var INSTANCE: MyLocationAccessor? = null

        fun getInstance(context: Context, executor: ExecutorService): MyLocationAccessor {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: MyLocationAccessor(
                    MyLocationDatabase.getInstance(context),
                    MyLocationManager.getInstance(context),
                    executor)
                    .also { INSTANCE = it }
            }
        }
    }
}