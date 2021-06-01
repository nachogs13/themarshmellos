package com.muei.apm.fasterwho

import android.app.Application
import android.app.PendingIntent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.muei.apm.fasterwho.db.MyLocationAccessor
import java.util.concurrent.Executors

class LocationUpdateViewModel(application: Application) : AndroidViewModel(application) {

    private val locationRepository = MyLocationAccessor.getInstance(
        application.applicationContext,
        Executors.newSingleThreadExecutor()
    )

    val receivingLocationUpdates: LiveData<Boolean> = locationRepository.receivingLocationUpdates

    val locationListLiveData = locationRepository.getLocations()

    fun startLocationUpdates() : PendingIntent? = locationRepository.startLocationUpdates()

    fun stopLocationUpdates() = locationRepository.stopLocationUpdates()

    fun deleteLocations() = locationRepository.deleteLocations()
}