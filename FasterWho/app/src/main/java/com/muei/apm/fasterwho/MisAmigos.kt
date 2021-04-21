package com.muei.apm.fasterwho

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.RatingBar
import android.widget.Toast

class MisAmigos : Toolbar() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_mis_amigos)
        layoutInflater.inflate(R.layout.activity_mis_amigos,frameLayout)
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar2)
        toolbar.setTitle("Mis amigos")

        val btnAmigos : Button = findViewById(R.id.button)
        btnAmigos.setOnClickListener({
            Toast.makeText(this, "Se añade un nuevo amigo", Toast.LENGTH_SHORT).show()
        })
    }

    override fun onResume() {
        super.onResume()
        navView.menu.getItem(4).setChecked(true)
    }


}