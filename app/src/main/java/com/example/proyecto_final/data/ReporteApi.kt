package com.example.proyecto_final.data

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class ReporteDto(
    val reporteId: String,
    val latitud: Double,
    val longitud: Double,
    val calle: String?,
    val distrito: String?,
    val tipo: String,
    val usuarioId: String,
    val createdAt: String
)

data class NuevoReporteDto(
    val latitud: Double,
    val longitud: Double,
    val calle: String?,
    val distrito: String?,
    val tipo: String,
    val usuarioId: String
)

interface ReporteApi {
    @GET("reportes")
    suspend fun listarReportes(): List<ReporteDto>

    @POST("reportes")
    suspend fun crearReporte(@Body reporte: NuevoReporteDto): ReporteDto
}