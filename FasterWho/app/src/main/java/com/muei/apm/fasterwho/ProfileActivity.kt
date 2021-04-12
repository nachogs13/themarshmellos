package com.muei.apm.fasterwho

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast

class ProfileActivity : Toolbar() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_profile)
        layoutInflater.inflate(R.layout.activity_profile,frameLayout)


        val btnImagen : ImageButton = findViewById(R.id.imageButton3)
        btnImagen.setOnClickListener({
            Toast.makeText(this, "Se cambia la imagen del perfil", Toast.LENGTH_SHORT).show()
        })

        val btnNombre : ImageButton = findViewById(R.id.imageButtonEdit)
        btnNombre.setOnClickListener({
            Toast.makeText(this, "Se cambia el nombre de usuario", Toast.LENGTH_SHORT).show()
        })
    }

    override fun onResume() {
        super.onResume()
        navView.menu.getItem(1).setChecked(true)
    }
}