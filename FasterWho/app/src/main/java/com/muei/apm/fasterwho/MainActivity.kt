package com.muei.apm.fasterwho

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.phone.SmsRetriever.getClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignIn.getClient
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider


class MainActivity : AppCompatActivity() {

    lateinit var passwd: EditText
    lateinit var email: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnRegistrarse : Button = findViewById(R.id.buttonRegistrarse)
        btnRegistrarse.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            Toast.makeText(this, "Registrarse en la app", Toast.LENGTH_SHORT).show()
        }

        val btnEntrar : Button = findViewById(R.id.buttonEntrar)
        btnEntrar.setOnClickListener{
            logIn()
        }

        val btnEntrarConGoogle : Button = findViewById(R.id.buttonEntrarGoogle)
        btnEntrarConGoogle.setOnClickListener{
            val intent = Intent(this, GoogleSignInActivity::class.java)
            startActivity(intent)
            Toast.makeText(this, "SignIn", Toast.LENGTH_SHORT).show()
        }
    }
    private fun validateInputs():Boolean {
        if(email.text.toString().isEmpty()){
            email.setError("Introduzca el email")
            return false
        }
        if(passwd.text.toString().isEmpty()){
            passwd.setError("Introduzca la contraseña")
            return false
        }
        return true
    }

    private fun logIn(){

        passwd = findViewById(R.id.EditTextContraseña)
        email = findViewById(R.id.editTextUserName)

        if(validateInputs()){
            FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(email.text.toString(),
                            passwd.text.toString()).addOnCompleteListener(){
                        if(it.isSuccessful){
                            val intent = Intent(this, InicioActivity::class.java)
                            startActivity(intent)
                            Toast.makeText(this, "Entrando en la aplicación", Toast.LENGTH_SHORT).show()
                        }else{
                            showAlert()
                        }
                    }
        }

    }

    private fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Email o contraseña incorrectos")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}