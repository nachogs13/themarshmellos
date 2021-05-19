package com.muei.apm.fasterwho

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.storage.StorageReference
import java.lang.ref.Reference

class ItemRuta {

    var nombreRuta:String ? = null
    var direccionRuta:String ? = null
    var coordenadasInicioRuta: GeoPoint ? = null
    var coordenadasFinRuta: GeoPoint? = null
    var rating: Number ? = null
    var file: DocumentReference ? = null
    var img: DocumentReference ? = null

    constructor(
        nombreRuta: String?,
        direccionRuta: String?,
        coordenadasInicioRuta: GeoPoint?,
        coordenadasFinRuta: GeoPoint?,
        rating: Number?,
        file: DocumentReference?,
        img: DocumentReference?
    ) {
        this.nombreRuta = nombreRuta
        this.direccionRuta = direccionRuta
        this.coordenadasInicioRuta = coordenadasInicioRuta
        this.coordenadasFinRuta = coordenadasFinRuta
        this.rating = rating
        this.file = file
        this.img = img
    }

    constructor(nombreRuta: String?, direccionRuta: String?, rating: Number?) {
        this.nombreRuta = nombreRuta
        this.direccionRuta = direccionRuta
        this.rating = rating
    }


}