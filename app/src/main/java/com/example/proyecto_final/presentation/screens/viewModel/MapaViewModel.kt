package com.example.proyecto_final.presentation.screens.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_final.data.ReporteAgregado
import com.example.proyecto_final.data.ReporteRepository
import com.example.proyecto_final.data.RetrofitClient
import com.example.proyecto_final.data.agruparReportes
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MapaViewModel(
    private val repository: ReporteRepository = ReporteRepository(RetrofitClient.api)
) : ViewModel() {

    private val _reportesAgregados = MutableStateFlow<List<ReporteAgregado>>(emptyList())
    val reportesAgregados: StateFlow<List<ReporteAgregado>> = _reportesAgregados.asStateFlow()

    init {
        viewModelScope.launch {
            while (isActive) {
                val reportes = repository.obtenerReportes()
                _reportesAgregados.value = agruparReportes(reportes)
                delay(5000) // refresca cada 5 segundos
            }
        }
    }
}