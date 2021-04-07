package com.muei.apm.fasterwho

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {
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
            val intent = Intent(this, InicioActivity::class.java)
            startActivity(intent)
            Toast.makeText(this, "Entrando en la aplicación", Toast.LENGTH_SHORT).show()
        }

        /*val btnEntrarConGoogle : Button = findViewById(R.id.buttonEntrarGoogle)
        btnEntrarConGoogle.setOnClickListener{

        }*/
    }
}