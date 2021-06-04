package com.muei.apm.fasterwho

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import org.w3c.dom.Text
import java.sql.Timestamp

class ProfileActivity : Toolbar() {
    private val db = FirebaseFirestore.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_profile)

        layoutInflater.inflate(R.layout.activity_profile,frameLayout)
        getInfoUsuario()
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar2)
        toolbar.setTitle("Perfil")

        val btnImagen : ImageButton = findViewById(R.id.imageButton3)
        btnImagen.setOnClickListener({
            Toast.makeText(this, "Se cambia la imagen del perfil", Toast.LENGTH_SHORT).show()
        })

        val btnNombre : Button = findViewById(R.id.editarPerfilButton)
        btnNombre.setOnClickListener({
            startActivity(Intent(this, EditarPerfilActivity::class.java))
            Toast.makeText(this, "Se cambia el nombre de usuario", Toast.LENGTH_SHORT).show()
        })
    }

    override fun onResume() {
        super.onResume()
        navView.menu.getItem(1).setChecked(true)
    }

    private fun getInfoUsuario(){
        val storage = FirebaseStorage.getInstance()
        Log.d("currentUser", firebaseAuth.currentUser.email)
        Log.d("currentUser", firebaseAuth.currentUser.toString())
        var textViewSexo : TextView = findViewById(R.id.userInformationSex)
        var textViewFechNac : TextView = findViewById(R.id.userInformationDate)
        var textViewEstatura : TextView = findViewById(R.id.userInformationHeight)
        var textViewPeso : TextView = findViewById(R.id.userInformationWeight)
        var textView : TextView = findViewById(R.id.userName)

        db.collection("usuarios").document(firebaseAuth.currentUser.email).get()
                .addOnSuccessListener {
                    Log.d("usuario", it.toString())
                    val username = it.get("username").toString()
                    if(it.get("fecha_nacimiento")!=null){
                        val fecha_nac = it.get("fecha_nacimiento") as String
                        textViewFechNac.text = "Fecha de nacimiento: ${fecha_nac.toString()}"
                    }
                    if(it.get("sexo")!=null){
                        val sexo = it.get("sexo").toString()
                        textViewSexo.text = "Sexo: $sexo"
                    }
                    if(it.get("estatura")!=null){
                        val estatura = it.get("estatura") as Number
                        textViewEstatura.text = "Estatura: ${estatura.toString()} cm"
                    }
                    if(it.get("peso")!=null){

                        val peso = it.get("peso") as Number
                        textViewPeso.text = "Peso: ${peso.toString()} kg"

                    }
                    if(it.get("imgPerfil")!=null){
                        var img = it.get("imgPerfil") as DocumentReference
                        GlideApp.with(this).load(storage.getReference(img.path.toString())).into(this.findViewById(R.id.imageButton3))
                    }

                    textView.text = username

                }

    }
}