package com.example.proyecto_final.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class LoginRequestDto(
    val correo: String,
    val contrasena: String
)

data class LoginResponseDto(
    val login: Boolean,
    val mensaje: String? = null,
    val nombre: String? = null
)

data class RegistroRequestDto(
    val nombre: String,
    val correo: String,
    val contrasena: String
)

data class RegistroResponseDto(
    val registro: Boolean,
    val mensaje: String? = null,
    val nombre: String? = null
)

interface AuthApi {
    @POST("SafeRouteAPI")
    suspend fun login(@Body request: LoginRequestDto): Response<LoginResponseDto>

    @POST("registro")
    suspend fun registrar(@Body request: RegistroRequestDto): Response<RegistroResponseDto>
}