package com.muei.apm.fasterwho.db

import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng
import java.util.*

/**
 * Converts non-standard objects in the {@link MyLocation} data class into and out of the database.
 */
class MyLocationTypeConverters {

    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun toDate(millisSinceEpoch: Long?): Date? {
        return millisSinceEpoch?.let {
            Date(it)
        }
    }

    @TypeConverter
    fun fromUUID(uuid: UUID?): String? {
        return uuid?.toString()
    }

    @TypeConverter
    fun toUUID(uuid: String?): UUID? {
        return UUID.fromString(uuid)
    }

    @TypeConverter
    fun fromLatLng(position: LatLng?): String? {
        if (position != null) {
            return position.latitude.toString() + "|" + position.longitude.toString()
        }
        return null
    }

    @TypeConverter
    fun toLatLng(position: String?): LatLng? {
        if (position != null) {
            val l =position.split("|")
            return LatLng(l[0].toDouble(),l[1].toDouble())
        }
        return null
    }
}