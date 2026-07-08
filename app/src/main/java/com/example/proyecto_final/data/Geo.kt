package com.example.proyecto_final.data

import android.location.Location

fun distanciaEnMetros(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
    val resultados = FloatArray(1)
    Location.distanceBetween(lat1, lon1, lat2, lon2, resultados)
    return resultados[0]
}