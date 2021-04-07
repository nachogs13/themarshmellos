package com.muei.apm.fasterwho

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class FiltersActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filters)

        val dialogDifLevelButton : ImageButton = findViewById(R.id.imageButtonInfo)
        dialogDifLevelButton.setOnClickListener {
            val dialogView = LayoutInflater.from(this).inflate(R.layout.info_dialog, null)

            val builder = AlertDialog.Builder(this)
                    .setView(dialogView)
            //show dialog
            builder.show()
            Toast.makeText(this, "Información de los niveles de dificultad", Toast.LENGTH_SHORT).show()
        }

        val btnCancelar : Button = findViewById(R.id.buttonCancelar)
        btnCancelar.setOnClickListener {
            val intent = Intent(this, InicioActivity::class.java)
            startActivity(intent)
        }

        val btnAplicar : Button = findViewById(R.id.buttonAplicar)
        btnAplicar.setOnClickListener {
            Toast.makeText(this, "Se aplican los filtros para las rutas", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, InicioActivity::class.java)
            startActivity(intent)
        }
    }
}