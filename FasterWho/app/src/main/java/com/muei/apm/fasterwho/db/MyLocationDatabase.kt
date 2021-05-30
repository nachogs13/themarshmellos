package com.muei.apm.fasterwho.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

// Nombre de la base de datos
private const val DATABASE_NAME = "my_location_table"

/**
 * Base de datos para almacenar los puntos de geolocalizacion
 */
@Database(entities = [MyLocationEntity::class], version = 1)
@TypeConverters(MyLocationTypeConverters::class)
abstract class MyLocationDatabase : RoomDatabase() {
    abstract fun locationDAO(): MyLocationDAO

    companion object {
        // Instanciación de Singleton
        @Volatile private var INSTANCE: MyLocationDatabase? = null

        // Obtiene una instancia de la BD
        fun getInstance(context: Context): MyLocationDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        // Crea la BD
        private fun buildDatabase(context: Context): MyLocationDatabase {
            return Room.databaseBuilder(
                context,
                MyLocationDatabase::class.java,
                DATABASE_NAME
            ).build()
        }
    }
}
