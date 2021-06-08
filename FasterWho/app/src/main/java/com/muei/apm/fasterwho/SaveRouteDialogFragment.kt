package com.muei.apm.fasterwho

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.EditText
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



        return activity?.let {
            // Use the Builder class for convenient dialog construction
            //var inputText: EditText? = EditText(context)
            //inputText!!.inputType = InputType.TYPE_CLASS_TEXT
           // inputText.hint = "Nombre Ruta"
            val builder = AlertDialog.Builder(it)
            //builder.setView(inputText)
            builder.setMessage("¿Desea guardar una imagen de la ruta?")
                    .setTitle("Guardar ruta")
                    .setPositiveButton(R.string.accept_button,
                            DialogInterface.OnClickListener { dialog, id ->
                                /*val intent = Intent(activity, DataRouteDialogFragment::class.java)
                                intent.putExtra("guardarRuta", true)
                                if (inputText.text != null) {
                                    intent.putExtra("nombreRuta", inputText.text.toString())
                                }

                                Log.i("PopUp", "Se guarda la ruta : ${inputText.text.toString()}")
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

                                startActivity(intent)*/
                                val popUpFragment = DataRouteDialogFragment()
                                var args = Bundle()
                                args.putDouble("distancia", distancia!!)
                                args.putDouble("velocidad", velocidadMaxima!!)
                                args.putString("horaInicio", horaInicio)
                                args.putLong("duracion", duracion!!)
                                args.putDouble("altitudGanda", altitudGanada!!)
                                args.putDouble("altitudPerdida", altitudPerdida!!)
                                args.putDouble("altitudMaxima", altitudMaxima!!)
                                args.putString("nombreArchivoRuta", nombreArchivoRuta)

                                popUpFragment.arguments = args
                                popUpFragment.show(requireActivity().supportFragmentManager, "Save Route")
                            })
                    .setNegativeButton(R.string.cancel_button,
                            DialogInterface.OnClickListener { dialog, id ->
                                Toast.makeText( activity, "No se guarda la ruta", Toast.LENGTH_SHORT).show()
                                val intent = Intent(activity, EstadisticasActivity::class.java)
                                intent.putExtra("guardarRuta", false)
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
                                //intent.putExtra("nombreArchivoRuta", nombreArchivoRuta)
                                startActivity(intent)
                            })
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

}
