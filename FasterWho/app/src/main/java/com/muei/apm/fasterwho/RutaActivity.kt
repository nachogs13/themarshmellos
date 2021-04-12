package com.muei.apm.fasterwho

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*

class RutaActivity : Toolbar() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_ruta)
        layoutInflater.inflate(R.layout.activity_ruta,frameLayout)
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

        val btnMapa : ImageView = findViewById(R.id.imagenMapa)
        btnMapa.setOnClickListener {
            Toast.makeText(this, "Viendo Mapa Completo", Toast.LENGTH_SHORT).show()
        }

        val btnRating : RatingBar = findViewById(R.id.ratingBar2)
        btnRating.setOnClickListener {
            Toast.makeText(this, "Poniendo estrellas dificultad", Toast.LENGTH_SHORT).show()
        }
    }
}