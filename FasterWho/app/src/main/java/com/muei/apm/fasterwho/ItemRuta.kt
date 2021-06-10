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
    var file: String ? = null
    var img: DocumentReference ? = null
    var distancia: Number ? = null
    var desnivel: Number ? = null
    var publicRuta: Boolean ? = null
    var id: String ? = null

    constructor(
        nombreRuta: String?,
        direccionRuta: String?,
        coordenadasInicioRuta: GeoPoint?,
        coordenadasFinRuta: GeoPoint?,
        rating: Number?,
        file: String?,
        img: DocumentReference?,
        distancia: Number,
        desnivel: Number
    ) {
        this.nombreRuta = nombreRuta
        this.direccionRuta = direccionRuta
        this.coordenadasInicioRuta = coordenadasInicioRuta
        this.coordenadasFinRuta = coordenadasFinRuta
        this.rating = rating
        this.file = file
        this.img = img
        this.distancia = distancia
        this.desnivel = desnivel
    }

    constructor(
            nombreRuta: String?,
            direccionRuta: String?,
            coordenadasInicioRuta: GeoPoint?,
            coordenadasFinRuta: GeoPoint?,
            rating: Number?,
            file: String?,
            img: DocumentReference?,
            distancia: Number,
            desnivel: Number,
            publicRuta: Boolean
    ) {
        this.nombreRuta = nombreRuta
        this.direccionRuta = direccionRuta
        this.coordenadasInicioRuta = coordenadasInicioRuta
        this.coordenadasFinRuta = coordenadasFinRuta
        this.rating = rating
        this.file = file
        this.img = img
        this.distancia = distancia
        this.desnivel = desnivel
        this.publicRuta = publicRuta
    }
    constructor(
            nombreRuta: String?,
            direccionRuta: String?,
            coordenadasInicioRuta: GeoPoint?,
            coordenadasFinRuta: GeoPoint?,
            rating: Number?,
            file: String?,
            img: DocumentReference?,
            distancia: Number,
            desnivel: Number,
            publicRuta: Boolean,
            id: String
    ) {
        this.nombreRuta = nombreRuta
        this.direccionRuta = direccionRuta
        this.coordenadasInicioRuta = coordenadasInicioRuta
        this.coordenadasFinRuta = coordenadasFinRuta
        this.rating = rating
        this.file = file
        this.img = img
        this.distancia = distancia
        this.desnivel = desnivel
        this.publicRuta = publicRuta
        this.id = id
    }



}