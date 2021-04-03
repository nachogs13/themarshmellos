package com.muei.apm.fasterwho.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.muei.apm.fasterwho.FiltersActivity
import com.muei.apm.fasterwho.R

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        val view = inflater.inflate(R.layout.activity_inicio, container, false)

        val btnFiltros : TextView = view.findViewById(R.id.textViewFiltros)
        btnFiltros.setOnClickListener{
            val intent = Intent(activity, FiltersActivity::class.java)
            startActivity(intent)
        }
        return view
    }
}