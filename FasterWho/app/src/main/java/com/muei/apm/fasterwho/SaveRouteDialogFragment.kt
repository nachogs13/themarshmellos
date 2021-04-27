package com.muei.apm.fasterwho

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment


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
                                val intent = Intent(activity, EstadisticasActivity::class.java)
                                startActivity(intent)
                            })
                    .setNegativeButton(R.string.cancel_button,
                            DialogInterface.OnClickListener { dialog, id ->
                                Toast.makeText( activity, "No se guarda la imagen", Toast.LENGTH_SHORT).show()
                                val intent = Intent(activity, EstadisticasActivity::class.java)
                                startActivity(intent)
                            })
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

}
