package com.muei.apm.fasterwho.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import java.util.*

/**
 * Definimos las operaciones permitidas en la base de datos
 */
@Dao
interface MyLocationDAO {

    @Query("SELECT * FROM my_location_table ORDER BY date DESC")
    fun getLocations(): LiveData<List<MyLocationEntity>>

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