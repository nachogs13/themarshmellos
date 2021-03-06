package com.muei.apm.fasterwho

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

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
        val latitudInicial = arguments?.getDouble("latitud_inicial")
        val longitudInicial =arguments?.getDouble("longitud_inicial")
        val latitudFinal = arguments?.getDouble("latitud_final")
        val longitudFinal = arguments?.getDouble("longitud_final")
        val velocidadMedia = arguments?.getDouble("velocidadMedia")
        val aceleracionMaxima = arguments?.getDouble("aceleracionMaxima")

        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setMessage("¿Desea guardar la ruta?")
                .setTitle("Guardar ruta")
                .setPositiveButton(R.string.accept_button
                ) { _, _ ->

                    val popUpFragment = DataRouteDialogFragment()
                    val args = Bundle()
                    args.putDouble("distancia", distancia!!)
                    args.putDouble("velocidad", velocidadMaxima!!)
                    args.putString("horaInicio", horaInicio)
                    args.putLong("duracion", duracion!!)
                    args.putDouble("altitudGanda", altitudGanada!!)
                    args.putDouble("altitudPerdida", altitudPerdida!!)
                    args.putDouble("altitudMaxima", altitudMaxima!!)
                    args.putDouble("latitud_inicial", latitudInicial!!)
                    args.putDouble("longitud_inicial", longitudInicial!!)
                    args.putDouble("latitud_final", latitudFinal!!)
                    args.putDouble("longitud_final", longitudFinal!!)
                    args.putString("nombreArchivoRuta", nombreArchivoRuta)
                    args.putDouble("velocidadMedia", velocidadMedia!!)
                    args.putDouble("aceleracionMaxima", aceleracionMaxima!!)

                    popUpFragment.arguments = args
                    popUpFragment.show(requireActivity().supportFragmentManager, "Save Route")
                }
                .setNegativeButton(R.string.cancel_button
                ) { _, _ ->
                    Toast.makeText(activity, "No se guarda la ruta", Toast.LENGTH_SHORT).show()
                    val intent = Intent(activity, EstadisticasActivity::class.java)
                    intent.putExtra("guardarRuta", false)
                    intent.putExtra("cancelled", true)
                    if (distancia != null) {
                        intent.putExtra("distancia", distancia)
                    }
                    if (velocidadMaxima != null) {
                        intent.putExtra("velocidad", velocidadMaxima)
                    }

                    val horaInicio2 = arguments?.getString("horaInicio")
                    if (horaInicio2 != null) {
                        intent.putExtra("horaInicio", horaInicio2)
                    }
                    if (duracion != null) {
                        intent.putExtra("duracion", duracion)
                    }
                    if (altitudGanada != null) {
                        intent.putExtra("altitudGanada", altitudGanada)
                    }
                    if (altitudPerdida != null) {
                        intent.putExtra("altitudPerdida", altitudPerdida)
                    }

                    if (altitudMaxima != null) {
                        intent.putExtra("altitudMaxima", altitudMaxima)
                    }

                    if (velocidadMedia != null) {
                        intent.putExtra("velocidadMedia", velocidadMedia)
                    }

                    if (aceleracionMaxima != null) {
                        intent.putExtra("aceleracionMaxima", aceleracionMaxima)
                    }
                    intent.putExtra("nombreArchivoRuta", nombreArchivoRuta)
                    startActivity(intent)
                }
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

}