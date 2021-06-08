package com.muei.apm.fasterwho

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

/**
 * DialogFragment para guardar la ruta con el nombre que indica el usuario
 */
class DataRouteDialogFragment : DialogFragment() {

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
            var inputName: EditText? = EditText(context)
            inputName!!.inputType = InputType.TYPE_CLASS_TEXT
            inputName.hint = "Nombre Ruta"

            val builder = AlertDialog.Builder(it)
            builder.setView(inputName)

            //builder.setView(listOf(inputDireccion,inputName))
            builder.setMessage("Introduzca los datos de la ruta")
                .setTitle("Datos ruta")
                .setPositiveButton(R.string.accept_button,
                    DialogInterface.OnClickListener { dialog, id ->

                        intent.putExtra("guardarRuta", true)
                        if (inputName.text != null) {
                            intent.putExtra("nombreRuta", inputName.text.toString())
                        }

                        Log.i("PopUp", "Nombre de la ruta : ${inputName.text.toString()}")

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
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}