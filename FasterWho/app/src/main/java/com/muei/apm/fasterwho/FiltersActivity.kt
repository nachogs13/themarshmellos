package com.muei.apm.fasterwho

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageButton
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
        }

        val btnCancelar : Button = findViewById(R.id.buttonCancelar)
        btnCancelar.setOnClickListener {
            val intent = Intent(this, InicioActivity::class.java)
            startActivity(intent)
        }
    }
}