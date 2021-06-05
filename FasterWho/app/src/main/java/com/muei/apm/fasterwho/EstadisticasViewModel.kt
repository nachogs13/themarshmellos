package com.muei.apm.fasterwho

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * Clase necesaria para poder pasar datos entre EstadisticasActivity y el fragmente que muestra
 * la lista de estadísticas, EstadisticasFragment
 */
class EstadisticasViewModel : ViewModel() {
    private val mutabledStatics = MutableLiveData<List<ItemEstadistica>>()

    fun setEstadisticas(estadisticas: List<ItemEstadistica>) {
        mutabledStatics.value = estadisticas
    }

    val getEstadisticas : LiveData<List<ItemEstadistica>> get() = mutabledStatics
}