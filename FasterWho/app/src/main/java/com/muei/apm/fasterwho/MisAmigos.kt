package com.muei.apm.fasterwho

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*

class MisAmigos : Toolbar() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutInflater.inflate(R.layout.activity_mis_amigos,frameLayout)
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar2)
        toolbar.title = "Mis amigos"

        val btnAmigos : Button = findViewById(R.id.button)
        btnAmigos.setOnClickListener {
            val intent = Intent(this, AddFriendActivity::class.java)
            startActivity(intent)
            Toast.makeText(this, "Se añade un nuevo amigo", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        navView.menu.getItem(4).isChecked = true
    }

    fun sinResultados(isEmp: Boolean) {
        val sinRes : TextView = findViewById(R.id.textViewSinResultados4)
        val rel : RelativeLayout = findViewById(R.id.loadingPanel4)
        rel.visibility = View.GONE
        if (isEmp) {
            sinRes.visibility = View.VISIBLE
        } else {
            sinRes.visibility = View.GONE
        }
    }

}