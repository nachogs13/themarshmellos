package com.muei.apm.fasterwho

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

class RutaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ruta)

        val btnEmpezar : Button = findViewById(R.id.buttonEmpezar)
        btnEmpezar.setOnClickListener({
            val intent = Intent(this, IniciarRutaActivity::class.java)
            startActivity(intent)
        })

        val btnMaria : TextView = findViewById(R.id.maria)
        btnMaria.setOnClickListener {
            Toast.makeText(this, "Viendo usuario María", Toast.LENGTH_SHORT).show()
        }
        val btnAvatarMaria : ImageView = findViewById(R.id.avatar1)
        btnAvatarMaria.setOnClickListener {
            Toast.makeText(this, "Viendo usuario María", Toast.LENGTH_SHORT).show()
        }

        val btnElena : TextView = findViewById(R.id.elena)
        btnElena.setOnClickListener {
            Toast.makeText(this, "Viendo usuario Elena", Toast.LENGTH_SHORT).show()
        }
        val btnAvatarElena : ImageView = findViewById(R.id.avatar2)
        btnAvatarElena.setOnClickListener {
            Toast.makeText(this, "Viendo usuario Elena", Toast.LENGTH_SHORT).show()
        }
    }
}