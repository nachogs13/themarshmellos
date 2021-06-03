package com.muei.apm.fasterwho

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class EstadisticasFragment: Fragment() {
    private var columnCount = 1
    private val viewModel : EstadisticasViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(EstadisticasFragment.ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_estadisticas_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                viewModel.getEstadisticas.observe(viewLifecycleOwner, Observer {
                    item -> adapter = EstadisticasRecyclerViewAdapter(item)
                })

            }
        }
        return view
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
                EstadisticasFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_COLUMN_COUNT, columnCount)
                    }
                }
    }

    /*
     * Función para crear un array con información de estadisticas
     */
    private fun setDataItem() : ArrayList<ItemEstadistica> {
        var listItem : ArrayList<ItemEstadistica> = ArrayList()

        listItem.add(ItemEstadistica(R.drawable.ic_directions_run_black_24dp,"Distancia","5 Km"))
        listItem.add(ItemEstadistica(R.drawable.ic_timer_black_24dp,"Duración","00:40:50"))
        listItem.add(ItemEstadistica(R.drawable.ic_calorie_24dp,"Calorías","120"))
        listItem.add(ItemEstadistica(R.drawable.ic_speed_black_24dp,"Vel. media","04:30 min/km"))
        listItem.add(ItemEstadistica(R.drawable.ic_speed_black_24dp,"Vel. máx","40 km/h"))
        listItem.add(ItemEstadistica(R.drawable.ic_elevation_24dp,"Elev. ganada","113m"))
        listItem.add(ItemEstadistica(R.drawable.ic_elevation_24dp,"Elev. perdida","110m"))
        listItem.add(ItemEstadistica(R.drawable.ic_clock_24dp,"Hora de inicio","20:36"))



        return listItem
    }
}