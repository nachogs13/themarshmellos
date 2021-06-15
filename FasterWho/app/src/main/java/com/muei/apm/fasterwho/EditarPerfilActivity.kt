package com.muei.apm.fasterwho

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class EditarPerfilActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_perfil)

        getInfoUsuario()

        val btnCancelar : Button = findViewById(R.id.buttonCancelarEditPerfilar)
        btnCancelar.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        val btnAceptar : Button = findViewById(R.id.buttonGuardar)
        btnAceptar.setOnClickListener {
            guardarDatosPerfil()
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    private fun getInfoUsuario(){
        val textViewFechNac : TextView = findViewById(R.id.editarPerfilFechNacUsr)
        val textViewEstatura : TextView = findViewById(R.id.editarPerfilEstaturaUsr)
        val textViewPeso : TextView = findViewById(R.id.editarPerfilPesoUsr)
        val textView : TextView = findViewById(R.id.editarPerfilNombreUsr)

        db.collection("usuarios").document(firebaseAuth.currentUser!!.email!!.toString()).get()
                .addOnSuccessListener {
                    val username = it.get("username").toString()
                    if(it.get("estatura")!=null){
                        val estatura = it.get("estatura") as Number
                        textViewEstatura.text = estatura.toInt().toString()
                    }
                    if(it.get("sexo")!=null){
                        when(val sexo = it.get("sexo").toString()){
                            "M" -> {findViewById<RadioButton>(R.id.radioButtonM).isChecked = true
                            Log.d("SEXO M", (sexo == "M").toString())}
                            "H" -> findViewById<RadioButton>(R.id.radioButtonH).isChecked = true
                        }
                    }
                    if(it.get("peso")!=null){
                        val peso = it.get("peso") as Number
                        textViewPeso.text = peso.toFloat().toString()
                    }
                    if(it.get("fecha_nacimiento")!=null){
                        val fecha = it.get("fecha_nacimiento").toString()
                        textViewFechNac.text = fecha
                    }
                    textView.text = username

                }

    }

    private fun guardarDatosPerfil(){

        val textViewFechNac : EditText = findViewById(R.id.editarPerfilFechNacUsr)
        val textViewEstatura : EditText = findViewById(R.id.editarPerfilEstaturaUsr)
        val textViewPeso : EditText = findViewById(R.id.editarPerfilPesoUsr)
        val textView : EditText = findViewById(R.id.editarPerfilNombreUsr)

        onRadioButtonSelected(findViewById(R.id.radioButtonM))
        Log.d("fecha",textViewFechNac.text.toString())

        if(textViewEstatura.text.isNotEmpty()){
            db.collection("usuarios")
                    .document(firebaseAuth.currentUser!!.email!!.toString())
                    .update("estatura",textViewEstatura.text.toString().toInt())
        }
        if(textViewPeso.text.isNotEmpty()){
            db.collection("usuarios")
                    .document(firebaseAuth.currentUser!!.email!!.toString())
                    .update("peso", textViewPeso.text.toString().toDouble())
        }
        db.collection("usuarios")
                .document(firebaseAuth.currentUser!!.email!!.toString())
                .update("username",textView.text.toString())
        if(textViewFechNac.text.isNotEmpty()){
            try {
                val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                formatter.parse(textViewFechNac.text.toString())
                db.collection("usuarios")
                    .document(firebaseAuth.currentUser!!.email!!.toString())
                    .update("fecha_nacimiento",textViewFechNac.text.toString())
            } catch (e : Exception) {
                Toast.makeText(this, "Formato de fecha incorrecto", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onRadioButtonSelected(view: View){
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked

            // Check which radio button was clicked
            when (view.getId()) {
                R.id.radioButtonH ->
                    if (checked) {
                        db.collection("usuarios")
                                .document(firebaseAuth.currentUser!!.email!!.toString())
                                .update("sexo","H")
                    }
                R.id.radioButtonM ->
                    if (checked) {
                        db.collection("usuarios")
                                .document(firebaseAuth.currentUser!!.email!!.toString())
                                .update("sexo","M")
                    }
            }
        }
    }


}