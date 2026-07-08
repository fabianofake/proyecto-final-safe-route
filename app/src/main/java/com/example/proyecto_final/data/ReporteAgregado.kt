package com.example.proyecto_final.data

import androidx.compose.ui.graphics.Color
import com.example.proyecto_final.data.ReporteDto
import com.google.android.gms.maps.model.LatLng
import kotlin.math.roundToInt

data class ReporteAgregado(
    val centro: LatLng,
    val calle: String?,
    val totalReportes: Int,
    val porcentajePeligro: Int,
    val color: Color
)

private fun claveDeAgrupacion(lat: Double, lng: Double): String {
    val latRedondeada = (lat * 10000).roundToInt() / 10000.0
    val lngRedondeada = (lng * 10000).roundToInt() / 10000.0
    return "$latRedondeada,$lngRedondeada"
}

fun agruparReportes(reportes: List<ReporteDto>): List<ReporteAgregado> {
    return reportes
        .groupBy { claveDeAgrupacion(it.latitud, it.longitud) }
        .map { (_, grupo) ->
            val total = grupo.size
            val peligrosos = grupo.count { it.tipo == "PELIGROSO" }
            val porcentaje = ((peligrosos.toDouble() / total) * 100).roundToInt()

            val color = when {
                porcentaje >= 60 -> Color(0xFFE53935) // rojo
                porcentaje >= 30 -> Color(0xFFFFA000) // ámbar
                else -> Color(0xFF43A047) // verde
            }

            ReporteAgregado(
                centro = LatLng(grupo.map { it.latitud }.average(), grupo.map { it.longitud }.average()),
                calle = grupo.first().calle,
                totalReportes = total,
                porcentajePeligro = porcentaje,
                color = color
            )
        }
}