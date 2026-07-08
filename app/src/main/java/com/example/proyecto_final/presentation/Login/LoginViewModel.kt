package com.example.proyecto_final.presentation.Login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_final.data.AuthRetrofitClient
import com.example.proyecto_final.data.LoginRequestDto
import com.example.proyecto_final.data.LoginResponseDto
import com.example.proyecto_final.data.SesionUsuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class LoginUiState {
    object Inactivo : LoginUiState()
    object Cargando : LoginUiState()
    data class Exito(val nombre: String?) : LoginUiState()
    data class Error(val mensaje: String) : LoginUiState()
}

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Inactivo)
    val uiState: StateFlow<LoginUiState> = _uiState

    fun login(correo: String, contrasena: String) {
        if (correo.isBlank() || contrasena.isBlank()) {
            _uiState.value = LoginUiState.Error("Completa correo y contraseña")
            return
        }

        viewModelScope.launch {
            _uiState.value = LoginUiState.Cargando
            try {
                val response = AuthRetrofitClient.api.login(
                    LoginRequestDto(correo = correo, contrasena = contrasena)
                )

                // El Lambda responde 200 si el login es correcto, y 401/404
                // si falla — en ambos casos el body trae JSON útil, así que
                // lo leemos manualmente en vez de dejar que Retrofit lance error
                val respuesta: LoginResponseDto? = if (response.isSuccessful) {
                    response.body()
                } else {
                    val json = response.errorBody()?.string()
                    json?.let {
                        com.google.gson.Gson().fromJson(it, LoginResponseDto::class.java)
                    }
                }

                _uiState.value = when {
                    respuesta?.login == true -> {
                        SesionUsuario.iniciarSesion(
                            correo = correo,
                            nombre = respuesta.nombre
                        )
                        LoginUiState.Exito(respuesta.nombre)
                    }
                    respuesta != null -> LoginUiState.Error(respuesta.mensaje ?: "Correo o contraseña incorrectos")
                    else -> LoginUiState.Error("Error del servidor (${response.code()})")
                }
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error("Sin conexión: ${e.localizedMessage}")
            }
        }
    }

    fun resetEstado() {
        _uiState.value = LoginUiState.Inactivo
    }
}