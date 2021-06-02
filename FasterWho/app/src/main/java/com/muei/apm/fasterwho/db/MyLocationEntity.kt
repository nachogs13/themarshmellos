package com.muei.apm.fasterwho.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import java.lang.NullPointerException
import java.text.DateFormat
import java.util.*

@Entity(tableName = "my_location_table")
data class MyLocationEntity (
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val foreground: Boolean = true,
    val position:LatLng = LatLng(0.0,0.0),
    val date: Date = Date(),
    val speed: Float = 0.0F

) {
    /**
     * En principio este método no va hacer falta, pero lo dejamos para debug
     */
    override fun toString(): String {
        val appState = if (foreground) {
            "in app"
        } else {
            "in BG"
        }

        return "$latitude, $longitude $appState on " +
                "${DateFormat.getDateTimeInstance().format(date)}.\n"
    }
}
