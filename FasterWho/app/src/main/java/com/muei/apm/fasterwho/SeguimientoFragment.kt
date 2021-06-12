package com.muei.apm.fasterwho

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer

/**
 * Fragment para mostrar las estadísticas en tiempo real al hacer la ruta
 */
class SeguimientoFragment: Fragment() {
    private val viewModel: SeguimientoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_seguimiento, container, false)

        val textoDuracion : TextView = view.findViewById(R.id.meters_count)
        viewModel.getDistance.observe(viewLifecycleOwner, Observer {
            item ->
            if (item < 1000) {
                textoDuracion.text = String.format("%.2f",item) + " metros"
            } else {
                textoDuracion.text = String.format("%.2f",(item/1000)) + " km"
            }
        })

        return view
    }
}