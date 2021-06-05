package com.muei.apm.fasterwho

import android.content.Context
import android.widget.Toast
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.PrintWriter

/**
 * Clase que contiene el código necesario para crear un archivo KML con la ruta realizada
 */
class RegistradorKML(contexto: Context) {
    //==============================================================================================
    // CONSTANTES
    //==============================================================================================
    // KML.
    val KML_NOMBRE_FICHERO = "ruta.kml"
    private val KML_CABECERA = """<?xml version="1.0" encoding="UTF-8"?>
<kml xmlns="http://www.opengis.net/kml/2.2">
  <Placemark>"""
    private val KML_PIE = "\n  </Placemark>\n</kml>"

    // GPX.
    val GPX_NOMBRE_FICHERO = "ruta.gpx"
    private val GPX_CABECERA = """<?xml version="1.0" encoding="UTF-8" standalone="no" ?>

<gpx xmlns="http://www.topografix.com/GPX/1/1" creator="byHand" version="1.1"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd">"""
    private val GPX_PIE = "\n</gpx>"

    //==============================================================================================
    // ATRIBUTOS
    //==============================================================================================
    private var fichero: File? = null
    private var flujoSalida: FileWriter? = null
    private var filtroSalida: PrintWriter? = null
    private var contexto: Context? = null

    //==============================================================================================
    // CONSTRUCTOR
    //==============================================================================================
    init {
        this.contexto = contexto
        fichero = File(contexto.filesDir, KML_NOMBRE_FICHERO)
    }

    //==============================================================================================
    // MÉTODOS
    //==============================================================================================

    //==============================================================================================
    // MÉTODOS
    //==============================================================================================
    /**
     * Método que abre o crea el fichero, y escribe la cabecera.
     */
    fun abrirFichero() {
        try {
            flujoSalida = FileWriter(fichero)
            filtroSalida = PrintWriter(flujoSalida)
            filtroSalida!!.write(KML_CABECERA)
            filtroSalida!!.close()
            flujoSalida!!.close()
        } catch (e: IOException) {
            Toast.makeText(contexto, "Error: " + e.message, Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Método que añade un punto al fichero.
     */
    fun anhadirPunto(latitud: Double, longitud: Double, altitud: Double) {
        try {
            flujoSalida = FileWriter(fichero, true)
            filtroSalida = PrintWriter(flujoSalida)
            filtroSalida!!.append("\n    <point>\n      <coordinates> ")
            filtroSalida!!.append("$latitud,$longitud,$altitud")
            filtroSalida!!.append(" </coordinates> \n    </point>")
            filtroSalida!!.close()
            flujoSalida!!.close()
        } catch (e: IOException) {
            Toast.makeText(contexto, "Error: " + e.message, Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Método que cierra el fichero, y es escribe el pie.
     */
    fun cerrarFichero() {
        try {
            flujoSalida = FileWriter(fichero, true)
            filtroSalida = PrintWriter(flujoSalida)
            filtroSalida!!.append(KML_PIE)
            filtroSalida!!.close()
            flujoSalida!!.close()
        } catch (e: IOException) {
            Toast.makeText(contexto, "Error: " + e.message, Toast.LENGTH_LONG).show()
        }
    }
}