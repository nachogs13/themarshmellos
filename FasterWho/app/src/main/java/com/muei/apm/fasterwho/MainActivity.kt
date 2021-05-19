package com.muei.apm.fasterwho

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {

    lateinit var passwd: EditText
    lateinit var email: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fun launchPopUp(type: String) {
            val popUpFragment = EulaDialogFragment()
            val args = Bundle()
            args.putString("type", type)
            popUpFragment.arguments = args;
            popUpFragment.show(supportFragmentManager, "Accept EULA")
        }

        val btnRegistrarse : Button = findViewById(R.id.buttonRegistrarse)
        btnRegistrarse.setOnClickListener {
            launchPopUp("Register")
        }

        val btnEntrar : Button = findViewById(R.id.buttonEntrar)
        btnEntrar.setOnClickListener{
            logIn()
        }

        val btnEntrarConGoogle : Button = findViewById(R.id.buttonEntrarGoogle)
        btnEntrarConGoogle.setOnClickListener{
            launchPopUp("Google")
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
        val firebaseAuth = FirebaseAuth.getInstance()
        passwd = findViewById(R.id.EditTextContraseña)
        email = findViewById(R.id.editTextUserName)

        if(validateInputs()){
            FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(email.text.toString(),
                            passwd.text.toString()).addOnCompleteListener(){
                            if(it.isSuccessful){
                                if(FirebaseAuth.getInstance().currentUser.isEmailVerified){
                                    val intent = Intent(this, InicioActivity::class.java)
                                    startActivity(intent)
                                    Toast.makeText(this, "Entrando en la aplicación", Toast.LENGTH_SHORT).show()
                                }else{
                                    showErrorEmailNotVerified()
                                }
                            }else{
                                Log.d("task", it.exception.toString())
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

    private fun showErrorEmailNotVerified(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Email no verificado, compruebe su correo")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}