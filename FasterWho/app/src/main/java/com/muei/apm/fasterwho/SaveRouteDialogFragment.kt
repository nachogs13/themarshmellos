package com.muei.apm.fasterwho

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import kotlin.math.round


class SaveRouteDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        // obtenemos la distancia y la velocidad para pasarsela a EstadisticasActivity
        val distancia = arguments?.getDouble("distancia")
        val velocidadMaxima = arguments?.getDouble("velocidad")
        val horaInicio = arguments?.getString("horaInicio")
        val duracion = arguments?.getLong("duracion")
        val altitudGanada = arguments?.getDouble("altitudGanada", 0.0)
        val altitudPerdida = arguments?.getDouble("altitudPErdida", 0.0)
        val altitudMaxima = arguments?.getDouble("altitudMaxima", 0.0)
        val nombreArchivoRuta = arguments?.getString("nombreArchivoRuta")
        val intent = Intent(activity, EstadisticasActivity::class.java)
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setMessage("¿Desea guardar una imagen de la ruta?")
                    .setTitle(R.string.route_name)
                    .setPositiveButton(R.string.accept_button,
                            DialogInterface.OnClickListener { dialog, id ->
                                Toast.makeText( activity, "Se guarda la imagen", Toast.LENGTH_SHORT).show()

                                if (distancia != null) {
                                    intent.putExtra("distancia", distancia)
                                }
                                if (velocidadMaxima != null) {
                                    intent.putExtra("velocidad", velocidadMaxima)
                                }
                                if (horaInicio != null) {
                                    intent.putExtra("horaInicio",horaInicio)
                                }
                                if (duracion != null) {
                                    intent.putExtra("duracion", duracion)
                                }
                                if (altitudGanada != null) {
                                    intent.putExtra("altitudGanada", altitudGanada)
                                }
                                if (altitudPerdida !=null) {
                                    intent.putExtra("altitudPerdida", altitudPerdida)
                                }
                                if (altitudMaxima != null) {
                                    intent.putExtra("altitudMaxima", altitudMaxima)
                                }
                                intent.putExtra("nombreArchivoRuta", nombreArchivoRuta)
                                startActivity(intent)
                            })
                    .setNegativeButton(R.string.cancel_button,
                            DialogInterface.OnClickListener { dialog, id ->
                                Toast.makeText( activity, "No se guarda la imagen", Toast.LENGTH_SHORT).show()


                                if (distancia != null) {
                                    intent.putExtra("distancia", distancia)
                                }
                                if (velocidadMaxima != null) {
                                    intent.putExtra("velocidad", velocidadMaxima)
                                }

                                val horaInicio = arguments?.getString("horaInicio")
                                if (horaInicio != null) {
                                    intent.putExtra("horaInicio",horaInicio)
                                }
                                if (duracion != null) {
                                    intent.putExtra("duracion", duracion)
                                }
                                if (altitudGanada != null) {
                                    intent.putExtra("altitudGanada", altitudGanada)
                                }
                                if (altitudPerdida !=null) {
                                    intent.putExtra("altitudPerdida", altitudPerdida)
                                }

                                if (altitudMaxima != null) {
                                    intent.putExtra("altitudMaxima", altitudMaxima)
                                }
                                intent.putExtra("nombreArchivoRuta", nombreArchivoRuta)
                                startActivity(intent)
                            })
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

}
