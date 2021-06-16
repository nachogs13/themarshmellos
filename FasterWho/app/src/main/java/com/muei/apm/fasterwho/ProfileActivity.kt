package com.muei.apm.fasterwho

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class ProfileActivity : Toolbar() {
    private val db = FirebaseFirestore.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_profile)

        layoutInflater.inflate(R.layout.activity_profile,frameLayout)
        getInfoUsuario()
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar2)
        toolbar.title = "Perfil"

        val btnImagen : ImageButton = findViewById(R.id.imageButton3)
        btnImagen.setOnClickListener {
            //startActivity(Intent(this, CameraActivity::class.java))
            showDialog()
            Toast.makeText(this, "Se cambia la imagen del perfil", Toast.LENGTH_SHORT).show()
        }

        val btnNombre : Button = findViewById(R.id.editarPerfilButton)
        btnNombre.setOnClickListener {
            startActivity(Intent(this, EditarPerfilActivity::class.java))
            Toast.makeText(this, "Se cambia el nombre de usuario", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        navView.menu.getItem(1).isChecked = true
    }

    private fun showDialog(){
        val items = arrayOf("Tomar foto", "Seleccionar desde galería")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Cambiar foto de perfil")
            .setItems(items
            ) { _, which ->
                when (which) {
                    0 -> startActivity(Intent(this, CameraActivity::class.java))
                    1 -> {
                        startActivityForResult(
                            Intent(
                                Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                            ),
                            2
                        )
                    }
                }

            }
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
                storageRef.child("ImgPerfil/${firebaseAuth.currentUser!!.email!!}.jpg").putFile(selectedImage)


                db.collection("usuarios").document(firebaseAuth.currentUser!!.email!!).get()
                        .addOnSuccessListener {
                            db.collection("usuarios")
                                    .document(firebaseAuth.currentUser!!.email!!)
                                    .update("imgPerfil",storageRef.child("ImgPerfil/${firebaseAuth.currentUser!!.email!!}.jpg"))
                            if(it.get("imgPerfil")!=null) {
                                storageRef.child("ImgPerfil/${firebaseAuth.currentUser!!.email!!}.jpg").delete()
                            }


                        }
            }
        }

    }

    private fun getInfoUsuario(){
        val storage = FirebaseStorage.getInstance()
        Log.d("currentUser", firebaseAuth.currentUser!!.email!!.toString())
        Log.d("currentUser", firebaseAuth.currentUser!!.toString())
        val textViewSexo : TextView = findViewById(R.id.userInformationSex)
        val textViewFechNac : TextView = findViewById(R.id.userInformationDate)
        val textViewEstatura : TextView = findViewById(R.id.userInformationHeight)
        val textViewPeso : TextView = findViewById(R.id.userInformationWeight)
        val textView : TextView = findViewById(R.id.userName)

        db.collection("usuarios").document(firebaseAuth.currentUser!!.email!!.toString()).get()
                .addOnSuccessListener {
                    Log.d("usuario", it.toString())
                    val username = it.get("username").toString()
                    emptyAttr(it, textViewFechNac, "Fecha de nacimiento", "fecha_nacimiento")
                    emptyAttr(it, textViewSexo, "Sexo", "sexo")
                    emptyAttr(it, textViewEstatura, "Estatura", "estatura")
                    emptyAttr(it, textViewPeso, "Peso", "peso")

                    if(it.get("imgPerfil")!=null){
                        val img = it.get("imgPerfil") as DocumentReference
                        GlideApp.with(this).load(storage.getReference(img.path)).into(this.findViewById(R.id.imageButton3))
                    }

                    textView.text = username
                    val rel : RelativeLayout = findViewById(R.id.loadingPanel)
                    rel.visibility = View.GONE

                }

    }

    private fun emptyAttr(
        it: DocumentSnapshot,
        textView: TextView,
        label: String,
        ifStr: String
    ) {
        var text = "$label: -"
        if (it.get(ifStr) != null && (it.get(ifStr).toString()).isNotBlank()) {
                val str = it.get(ifStr).toString()
                text = "$label: $str"
            if (ifStr == "peso") {
                text = "$text kg"
            }
            if (ifStr == "estatura") {
                text = "$text cm"
            }
        }
        textView.text = text
    }
}