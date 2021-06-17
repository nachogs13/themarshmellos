package com.muei.apm.fasterwho

import android.app.Dialog
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddFriendDialogFragment : DialogFragment() {
    private val db = FirebaseFirestore.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {


        return activity?.let { it ->
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            val inputName = EditText(context)
            inputName.inputType = InputType.TYPE_CLASS_TEXT
            inputName.hint = "Usuario/email"
            builder.setView(inputName)
            builder.setMessage("Introduce el nombre de usuario o email de tu amig@.")
                .setTitle("Añadir amig@")
                .setPositiveButton(R.string.accept_button
                ) { _, _ ->
                    if (inputName.text.toString().isNotBlank()) {
                        db.collection("usuarios").document(inputName.text.toString()).get()
                            .addOnSuccessListener {
                                if (it.get("username") == null) {
                                    db.collection("usuarios")
                                        .whereEqualTo("username", inputName.text.toString())
                                        .get().addOnSuccessListener { it2 ->
                                            for (document in it2) {
                                                amigos(document.id)
                                            }
                                        }
                                } else {
                                    amigos(inputName.text.toString())
                                }
                            }
                    }
                }
                .setNegativeButton(R.string.cancel_button
                ) { _, _ ->
                    Toast.makeText(activity, "Operación cancelada", Toast.LENGTH_SHORT).show()
                    dismiss()
                }
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun amigos(
        inputName: String
    ) {
        var amigos: MutableList<String> = ArrayList()
        db.collection("usuarios").document(firebaseAuth.currentUser!!.email!!.toString()).get()
            .addOnSuccessListener { it2 ->
                if (it2.get("amigos") != null) {
                    amigos = it2.get("amigos") as MutableList<String>
                }
                amigos.add(inputName)
                db.collection("usuarios").document(firebaseAuth.currentUser!!.email!!.toString())
                    .update("amigos", amigos).addOnSuccessListener {
                        val amigos2 : MutableList<ItemAmigo> = ArrayList()
                        val adapter = MyMisAmigosRecyclerViewAdapter(amigos2)
                        adapter.setAmigosList()
                        dismiss()
                    }
            }
    }

}