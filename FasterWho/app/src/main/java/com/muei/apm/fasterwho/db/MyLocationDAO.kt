package com.muei.apm.fasterwho.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.google.android.gms.maps.model.LatLng
import java.util.*

/**
 * Definimos las operaciones permitidas en la base de datos
 */
@Dao
interface MyLocationDAO {

    @Query("SELECT speed FROM my_location_table ORDER BY speed DESC")
    fun getSpeeds(): LiveData<List<Float>>

    @Query("SELECT position FROM my_location_table ORDER BY date ASC")
    fun getLocations(): LiveData<List<LatLng>>

    @Query("SELECT * FROM my_location_table WHERE id=(:id)")
    fun getLocation(id: UUID): LiveData<MyLocationEntity>

    @Update
    fun updateLocation(myLocationEntity: MyLocationEntity)

    @Insert
    fun addLocation(myLocationEntity: MyLocationEntity)

    @Insert
    fun addLocations(myLocationEntities: List<MyLocationEntity>)

    /* Esto lo añadi yo*/
    @Query("DELETE FROM my_location_table")
    fun deleteLocations()
}