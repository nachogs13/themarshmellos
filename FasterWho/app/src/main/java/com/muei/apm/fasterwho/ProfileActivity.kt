package com.muei.apm.fasterwho

import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
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
            //startActivity(Intent(this, CameraActivity::class.java))
            showDialog()
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

    private fun showDialog(){
        val items = arrayOf("Tomar foto", "Seleccionar desde galería")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Cambiar foto de perfil")
            .setItems(items,
                DialogInterface.OnClickListener { dialog, which ->
                    when(which) {
                        0 -> startActivity(Intent(this, CameraActivity::class.java))
                        1 -> {startActivityForResult(Intent(Intent.
                        ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
                            2)
                        }
                    }

                })
        builder.setNegativeButton("Cancelar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        Log.d("DATA",data.toString())
        if (resultCode == RESULT_OK && null != data) {
            val selectedImage = data.data
            val btnImagen : ImageButton = findViewById(R.id.imageButton3)
            btnImagen.setImageURI(selectedImage)

            if (selectedImage != null) {
                storageRef.child("ImgPerfil/${firebaseAuth.currentUser.email}.jpg").putFile(selectedImage)


                db.collection("usuarios").document(firebaseAuth.currentUser.email).get()
                        .addOnSuccessListener {
                            val username = it.get("username").toString()
                            db.collection("usuarios")
                                    .document(firebaseAuth.currentUser.email)
                                    .update("imgPerfil",storageRef.child("ImgPerfil/${firebaseAuth.currentUser.email}.jpg"))
                            if(it.get("imgPerfil")!=null) {
                                storageRef.child("ImgPerfil/${firebaseAuth.currentUser.email}.jpg").delete()
                            }


                        }
            }
        }

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