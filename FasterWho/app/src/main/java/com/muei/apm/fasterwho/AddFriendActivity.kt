package com.muei.apm.fasterwho

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.ArrayList

class AddFriendActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_friend)

        val btnCancelar : Button = findViewById(R.id.buttonCancelarAddFriend)
        btnCancelar.setOnClickListener {
            startActivity(Intent(this, MisAmigos::class.java))
        }

        val btnAceptar : Button = findViewById(R.id.buttonGuardarAddFriend)
        btnAceptar.setOnClickListener {
            val inputName : EditText = findViewById(R.id.editarNombreAmigo)
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
                        startActivity(Intent(this, MisAmigos::class.java))
                    }
            }
    }
}