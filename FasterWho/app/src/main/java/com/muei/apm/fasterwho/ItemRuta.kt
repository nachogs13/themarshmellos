package com.muei.apm.fasterwho

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.GeoPoint
import java.lang.ref.Reference

class ItemRuta {

    var nombreRuta:String ? = null
    var direccionRuta:String ? = null
    var coordenadasInicioRuta: GeoPoint ? = null
    var coordenadasFinRuta: GeoPoint? = null
    var rating: Number ? = null
    var file: DocumentReference ? = null

    constructor(
        nombreRuta: String?,
        direccionRuta: String?,
        coordenadasInicioRuta: GeoPoint?,
        coordenadasFinRuta: GeoPoint?,
        rating: Number?,
        file: DocumentReference?
    ) {
        this.nombreRuta = nombreRuta
        this.direccionRuta = direccionRuta
        this.coordenadasInicioRuta = coordenadasInicioRuta
        this.coordenadasFinRuta = coordenadasFinRuta
        this.rating = rating
        this.file = file
    }

    constructor(nombreRuta: String?, direccionRuta: String?, rating: Number?) {
        this.nombreRuta = nombreRuta
        this.direccionRuta = direccionRuta
        this.rating = rating
    }


}