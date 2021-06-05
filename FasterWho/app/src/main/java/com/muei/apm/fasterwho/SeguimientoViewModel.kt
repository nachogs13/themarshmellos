package com.muei.apm.fasterwho

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SeguimientoViewModel : ViewModel() {
    private val mutabledDistance = MutableLiveData<Double>()

    fun setDistance(distance: Double) {
        mutabledDistance.value = distance
    }

    val getDistance : LiveData<Double> get() = mutabledDistance
}