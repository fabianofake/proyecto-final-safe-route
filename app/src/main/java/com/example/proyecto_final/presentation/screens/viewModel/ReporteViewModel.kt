package com.example.proyecto_final.presentation.screens.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_final.data.ReporteDto
import com.example.proyecto_final.data.ReporteRepository
import com.example.proyecto_final.data.RetrofitClient
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReporteViewModel(
    private val repository: ReporteRepository = ReporteRepository(RetrofitClient.api)
) : ViewModel() {

    private val _reportes = MutableStateFlow<List<ReporteDto>>(emptyList())
    val reportes: StateFlow<List<ReporteDto>> = _reportes

    fun cargarReportes() {
        viewModelScope.launch {
            _reportes.value = repository.obtenerReportes()
        }
    }

    fun enviarReporte(
        ubicacion: LatLng,
        calle: String?,
        tipo: String, // "SEGURO" o "PELIGROSO"
        usuarioId: String,
        onResultado: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val resultado = repository.crearReporte(
                lat = ubicacion.latitude,
                lng = ubicacion.longitude,
                calle = calle,
                distrito = null,
                tipo = tipo,
                usuarioId = usuarioId
            )
            onResultado(resultado.isSuccess)
        }
    }
}