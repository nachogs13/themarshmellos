package com.muei.apm.fasterwho

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import kotlin.math.round


class SaveRouteDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setMessage("¿Desea guardar una imagen de la ruta?")
                    .setTitle(R.string.route_name)
                    .setPositiveButton(R.string.accept_button,
                            DialogInterface.OnClickListener { dialog, id ->
                                Toast.makeText( activity, "Se guarda la imagen", Toast.LENGTH_SHORT).show()
                                // obtenemos la distancia y la velocidad para pasarsela a EstadisticasActivity
                                val distancia = arguments?.getDouble("distancia")
                                val velocidadMaxima = arguments?.getDouble("velocidad")
                                val horaInicio = arguments?.getString("horaInicio")
                                val duracion = arguments?.getLong("duracion")
                                val intent = Intent(activity, EstadisticasActivity::class.java)
                                if (distancia != null) {
                                    intent.putExtra("distancia", String.format("%.2f",distancia/1000))
                                }
                                if (velocidadMaxima != null) {
                                    intent.putExtra("velocidad", String.format("%.2f",velocidadMaxima))
                                }
                                if (horaInicio != null) {
                                    intent.putExtra("horaInicio",horaInicio)
                                }
                                if (duracion != null) {
                                    intent.putExtra("duracion", duracion)
                                }
                                startActivity(intent)
                            })
                    .setNegativeButton(R.string.cancel_button,
                            DialogInterface.OnClickListener { dialog, id ->
                                Toast.makeText( activity, "No se guarda la imagen", Toast.LENGTH_SHORT).show()
                                // obtenemos la distancia y la velocidad para pasarsela a EstadisticasActivity
                                val distancia = arguments?.getDouble("distancia")
                                val velocidadMaxima = arguments?.getDouble("velocidad")
                                val duracion = arguments?.getLong("duracion")
                                val intent = Intent(activity, EstadisticasActivity::class.java)
                                if (distancia != null) {
                                    intent.putExtra("distancia", distancia/*String.format("%.2f",distancia/1000)*/)
                                }
                                if (velocidadMaxima != null) {
                                    intent.putExtra("velocidad", velocidadMaxima/*String.format("%.2f",velocidadMaxima)*/)
                                }
                                val horaInicio = arguments?.getString("horaInicio")
                                if (horaInicio != null) {
                                    intent.putExtra("horaInicio",horaInicio)
                                }
                                if (duracion != null) {
                                    intent.putExtra("duracion", duracion)
                                }
                                startActivity(intent)
                            })
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

}
