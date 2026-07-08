package com.example.proyecto_final.data

import com.example.proyecto_final.data.NuevoReporteDto
import com.example.proyecto_final.data.ReporteApi
import com.example.proyecto_final.data.ReporteDto

class ReporteRepository(private val api: ReporteApi) {

    suspend fun crearReporte(
        lat: Double,
        lng: Double,
        calle: String?,
        distrito: String?,
        tipo: String,
        usuarioId: String
    ): Result<ReporteDto> {
        return try {
            val nuevo = NuevoReporteDto(lat, lng, calle, distrito, tipo, usuarioId)
            Result.success(api.crearReporte(nuevo))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerReportes(): List<ReporteDto> {
        return try {
            api.listarReportes()
        } catch (e: Exception) {
            emptyList()
        }
    }
}