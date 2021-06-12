package com.muei.apm.fasterwho

import android.graphics.Color
import android.util.Log
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import org.xml.sax.Attributes
import org.xml.sax.SAXException
import org.xml.sax.helpers.DefaultHandler
import java.util.*

/**
 * Clase de SAX que especifíca que se tiene que hacer mientras se lee el fichero KML
 */
class SaxHandler(mapa: GoogleMap): DefaultHandler() {

    // VARIABLES
    //==============================================================================================
    private var mapa // Mapa de Google.
            : GoogleMap? = null
    private var punto // Coordenadas de un punto.
            : LatLng? = null
    private var listaPuntos // Lista de puntos.
            : ArrayList<LatLng>? = null
    private var ruta // Ruta.
            : PolylineOptions? = null

    // Variables que necesitaremos en SAX.
    private var dentroEtiqueta = false
    private var textoLeido: String? = null

    //=============================
    // Constructor
    //============================
    init {
        this.mapa = mapa
        this.dentroEtiqueta = false
        this.listaPuntos = ArrayList<LatLng>()
        this.ruta = PolylineOptions()
    }
    //==============================================================================================
    // MÉTODOS SOBREESCRITOS
    //==============================================================================================
    /**
     * Contenido de la etiqueta.
     */
    @Throws(SAXException::class)
    override fun characters(ch: CharArray, start: Int, length: Int) {
        super.characters(ch, start, length)

        // Recoge el contenido de la etiqueta.
        if (dentroEtiqueta) {
            textoLeido = String(ch, start, length)
        }
    }

    /**
     * Comienzo del elemento.
     */
    @Throws(SAXException::class)
    override fun startElement(
        uri: String,
        localName: String,
        name: String,
        attributes: Attributes
    ) {
        dentroEtiqueta = true // Para comprobación de seguridad.
    }

    /**
     * Final del elemento.
     * Aquí programamos las acciones a realizar al finalizar una etiqueta.
     */
    @Throws(SAXException::class)
    override fun endElement(uri: String, localName: String, name: String) {
        super.endElement(uri, localName, name)

        // Comprobaciones de seguridad que nos convengan según ejercicio.
        if (dentroEtiqueta) {
            if (localName == "coordinates") {
                // Entraremos aquí cada vez que SAX lea un </coordinates>.
                try {
                    Log.d("SAX", textoLeido!!)
                    // Cogemos un punto mediante los indexOf de las comas.
                    // Para saber si hay que sumar o restar al índice haz pruebas con SYSO.
                    val latitud = textoLeido!!.substring(0, textoLeido!!.indexOf(',')).toDouble()
                    val longitud = textoLeido!!.substring(
                        textoLeido!!.indexOf(',') + 1,
                        textoLeido!!.lastIndexOf(',')
                    ).toDouble()
                    //double altura = Double.parseDouble(textoLeido.substring(textoLeido.lastIndexOf(',')+1, textoLeido.length()));
                    //double altura = Double.parseDouble(textoLeido.substring(textoLeido.lastIndexOf(',')+1, textoLeido.length()));
                    punto = LatLng(latitud, longitud)

                    listaPuntos?.add(punto!!) // Se añade un punto a la lista, para crear una ruta después.

                } catch (e: Exception) {
                    Log.d("SAX","Saltando punto errróneas: $textoLeido")
                    println("Saltando punto errróneas: $textoLeido")
                }
            }

            // Se resetean variables.
            textoLeido = ""
            dentroEtiqueta = false
        }
    }

    /**
     * Comienzo del documento.
     */
    @Throws(SAXException::class)
    override fun startDocument() {
        // Iniciamos variables que hagan falta (lo hicimos en constructor)
    }

    /**
     * Final del documento.
     * Aquí creamos la ruta en el mapa y movemos la cámara aplicando un zoom
     */
    @Throws(SAXException::class)
    override fun endDocument() {
        // Añadimos la lista de puntos (el array de puntos que hemos ido guardando).
        if (!listaPuntos?.isEmpty()!!) {
            ruta?.addAll(listaPuntos)?.color(Color.RED)
        } else println("Error, no hay puntos o no hay fichero.")
    }

    //==============================================================================================
    // GETTERS
    //==============================================================================================

    //==============================================================================================
    // GETTERS
    //==============================================================================================
    /**
     * Obtener ruta.
     */
    fun getRuta(): PolylineOptions? {
        return ruta
    }

    fun getPoints() : List<LatLng> {
        return ruta?.points!!
    }

    /**
     * Obtener última coordenada.
     */
    fun getLastCoordenadas(): LatLng? {
        return punto
    }

}

