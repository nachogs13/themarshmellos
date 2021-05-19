package com.muei.apm.fasterwho

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {
    lateinit var email: EditText
    lateinit var passwd: EditText
    lateinit var repeatPasswd: EditText
    lateinit var username: EditText
    private val firebaseAuth = FirebaseAuth.getInstance()
    val MIN_PASSWD_LENGTH = 8
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
            Registrarse()
        }
    }

    private fun validateInputs():Boolean {
        email = findViewById(R.id.emailEditText)
        passwd = findViewById(R.id.repeatPasswdEditText)
        repeatPasswd = findViewById(R.id.passwdEditText)
        username = findViewById(R.id.usernameEditText)
        if(email.text.toString().isEmpty()){
            email.setError("Introduzca el email")
            return false
        }
        if(username.text.toString().isEmpty()){
            username.setError("Introduca el nombre de usuario")
            return false
        }
        if(passwd.text.toString().isEmpty()){
            passwd.setError("Introduzca la contraseña")
            return false
        }else if(passwd.text.length < MIN_PASSWD_LENGTH){
            passwd.setError("La contraseña debe tener más 8 caracteres")
            return false
        }
        if(repeatPasswd.text.toString().isEmpty()){
            repeatPasswd.setError("Introduca la contraseña de nuevo")
            return false
        }

        if(!isEmailValid(email.text.toString())){
            email.setError("Dirección de correo incorrecta")
            return false
        }
        if(!passwd.text.toString().equals(repeatPasswd.text.toString())){
            repeatPasswd.setError("Las contraseñas deben coincidir")
            return false
        }
        return true
    }

    private fun isEmailValid(email:String): Boolean{
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun Registrarse(){
        if(validateInputs()){

            val dirEmail = email.text.toString()
            val nombreUsuario = username.text.toString()
            val contraseña = passwd.text.toString()
            val repetirContraseña = repeatPasswd.text.toString()

            /*user!!.sendEmailVerification()
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "Email sent.")
            }
        }*/
            firebaseAuth
                    .createUserWithEmailAndPassword(dirEmail,contraseña).addOnCompleteListener{
                        if(it.isSuccessful){
                            //val firebaseUser = this.firebaseAuth.currentUser!!
                            //Toast.makeText(this, "Success",Toast.LENGTH_SHORT)
                            //Log.d("log", firebaseUser.toString())
                            FirebaseAuth.getInstance().currentUser.sendEmailVerification()
                                    .addOnCompleteListener{
                                        Log.d("Send email", "Email")
                                    }
                            showSuccesAlert()
                        }else {
                            val exception = it.exception.toString()
                            when(exception){
                                "ERROR_EMAIL_ALREADY_IN_USE" -> showAlert("ERROR_EMAIL_ALREADY_IN_USE")
                                else -> {
                                    showAlert(exception)
                                }
                            }
                        }
                    }
            Toast.makeText(this, "Login success", Toast.LENGTH_SHORT)
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
        builder.setMessage("Se ha registrado con éxito. Compruebe el correo para verificar su email")
        builder.setPositiveButton("Aceptar", DialogInterface.OnClickListener { builder, which ->
            startActivity(Intent(this, MainActivity::class.java))
        })
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}