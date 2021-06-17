package com.muei.apm.fasterwho

import android.os.Bundle
import android.widget.*

class MisAmigos : Toolbar() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutInflater.inflate(R.layout.activity_mis_amigos,frameLayout)
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar2)
        toolbar.title = "Mis amigos"

        val btnAmigos : Button = findViewById(R.id.button)
        btnAmigos.setOnClickListener {
            val popUpFragment = AddFriendDialogFragment()
            popUpFragment.show(supportFragmentManager, "Save Route")
            Toast.makeText(this, "Se añade un nuevo amigo", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        navView.menu.getItem(4).isChecked = true
    }


}