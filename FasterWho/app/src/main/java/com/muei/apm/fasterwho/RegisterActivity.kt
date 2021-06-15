package com.muei.apm.fasterwho

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {
    lateinit var email: EditText
    lateinit var passwd: EditText
    private lateinit var repeatPasswd: EditText
    private lateinit var username: EditText
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val minPasswdLength = 8
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val btnCancelar: Button = findViewById(R.id.buttonCancelar)
        btnCancelar.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val btnRegistrarse: Button = findViewById(R.id.buttonAceptar)
        btnRegistrarse.setOnClickListener{
            registrarse()
        }
    }

    private fun validateInputs():Boolean {
        email = findViewById(R.id.emailEditText)
        passwd = findViewById(R.id.repeatPasswdEditText)
        repeatPasswd = findViewById(R.id.passwdEditText)
        username = findViewById(R.id.usernameEditText)
        if(email.text.toString().isEmpty()){
            email.error = "Introduzca el email"
            return false
        }
        if(username.text.toString().isEmpty()){
            username.error = "Introduca el nombre de usuario"
            return false
        }
        if(passwd.text.toString().isEmpty()){
            passwd.error = "Introduzca la contraseña"
            return false
        }else if(passwd.text.length < minPasswdLength){
            passwd.error = "La contraseña debe tener más 8 caracteres"
            return false
        }
        if(repeatPasswd.text.toString().isEmpty()){
            repeatPasswd.error = "Introduca la contraseña de nuevo"
            return false
        }

        if(!isEmailValid(email.text.toString())){
            email.error = "Dirección de correo incorrecta"
            return false
        }
        if(passwd.text.toString() != repeatPasswd.text.toString()){
            repeatPasswd.error = "Las contraseñas deben coincidir"
            return false
        }
        return true
    }

    private fun isEmailValid(email:String): Boolean{
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun registrarse(){
        if(validateInputs()){

            val dirEmail = email.text.toString()
            val nombreUsuario = username.text.toString()
            val contrasinal = passwd.text.toString()


            firebaseAuth
                    .createUserWithEmailAndPassword(dirEmail,contrasinal).addOnCompleteListener{
                        if(it.isSuccessful){
                            FirebaseAuth.getInstance().currentUser!!.sendEmailVerification()
                                    .addOnCompleteListener{
                                        Log.d("Send email", "Email")
                                    }
                            showSuccesAlert()
                            db.collection("usuarios").document(FirebaseAuth.getInstance().currentUser!!.email!!.toString())
                                    .set(hashMapOf("username" to nombreUsuario))
                        }else {
                            when(val exception = it.exception.toString()){
                                "ERROR_EMAIL_ALREADY_IN_USE" -> showAlert("ERROR_EMAIL_ALREADY_IN_USE")
                                else -> {
                                    showAlert(exception)
                                }
                            }
                        }
                    }
            Toast.makeText(this, "Login success", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showAlert(message : String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        if(message!="ERROR_EMAIL_ALREADY_IN_USE"){
            builder.setMessage("Email ya en uso")
        }else{
            builder.setMessage(message)
        }
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showSuccesAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("¡Bienvenido!")
        builder.setMessage("Se ha registrado con éxito. Compruebe el correo para verificar su email.")
        builder.setPositiveButton("Aceptar") { _, _ ->
            startActivity(Intent(this, MainActivity::class.java))
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}