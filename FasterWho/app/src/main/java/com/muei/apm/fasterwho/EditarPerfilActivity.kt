package com.muei.apm.fasterwho

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class EditarPerfilActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_perfil)

        getInfoUsuario()

        val btnCancelar : Button = findViewById(R.id.buttonCancelarEditPerfilar)
        btnCancelar.setOnClickListener({
            startActivity(Intent(this, ProfileActivity::class.java))
        })

        val btnAceptar : Button = findViewById(R.id.buttonGuardar)
        btnAceptar.setOnClickListener({
            guardarDatosPerfil()
            startActivity(Intent(this,ProfileActivity::class.java))
        })
    }

    private fun getInfoUsuario(){
        val storage = FirebaseStorage.getInstance()
       // var textViewSexo : TextView = findViewById(R.id.editarPerfilNombreUsr)
        var textViewFechNac : TextView = findViewById(R.id.editarPerfilFechNacUsr)
        var textViewEstatura : TextView = findViewById(R.id.editarPerfilEstaturaUsr)
        var textViewPeso : TextView = findViewById(R.id.editarPerfilPesoUsr)
        var textView : TextView = findViewById(R.id.editarPerfilNombreUsr)

        db.collection("usuarios").document(firebaseAuth.currentUser.email).get()
                .addOnSuccessListener {
                    val username = it.get("username").toString()
                    if(it.get("estatura")!=null){
                        val estatura = it.get("estatura") as Number
                        textViewEstatura.text = estatura.toInt().toString()
                    }
                    /*if(it.get("imgPerfil")!=null){

                        val img = it.get("imgPerfil") as DocumentReference
                        GlideApp.with(this).load(storage.getReference(img.path.toString()))
                                .into(this.findViewById(R.id.imageButton3))
                    }*/
                    if(it.get("sexo")!=null){
                        val sexo = it.get("sexo").toString()
                        when(sexo){
                            "M" -> {findViewById<RadioButton>(R.id.radioButtonM).isChecked = true
                            Log.d("SEXO M",sexo.equals("M").toString())}
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

        var textViewFechNac : EditText = findViewById(R.id.editarPerfilFechNacUsr)
        var textViewEstatura : EditText = findViewById(R.id.editarPerfilEstaturaUsr)
        var textViewPeso : EditText = findViewById(R.id.editarPerfilPesoUsr)
        var textView : EditText = findViewById(R.id.editarPerfilNombreUsr)

        onRadioButtonSelected(findViewById(R.id.radioButtonM))
        Log.d("fecha",textViewFechNac.text.toString())

        if(!textViewEstatura.text.isEmpty()){
            db.collection("usuarios")
                    .document(firebaseAuth.currentUser.email)
                    .update("estatura",textViewEstatura.text.toString().toInt())
        }
        if(!textViewPeso.text.isEmpty()){
            db.collection("usuarios")
                    .document(firebaseAuth.currentUser.email)
                    .update("peso", textViewPeso.text.toString().toDouble())
        }
        db.collection("usuarios")
                .document(firebaseAuth.currentUser.email)
                .update("username",textView.text.toString())
        if(!textViewFechNac.text.isEmpty()){
            db.collection("usuarios")
                    .document(firebaseAuth.currentUser.email)
                    .update("fecha_nacimiento",textViewFechNac.text.toString())
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
                                .document(firebaseAuth.currentUser.email)
                                .update("sexo","H")
                    }
                R.id.radioButtonM ->
                    if (checked) {
                        db.collection("usuarios")
                                .document(firebaseAuth.currentUser.email)
                                .update("sexo","M")
                    }
            }
        }
    }


}