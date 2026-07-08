package com.example.proyecto_final.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyecto_final.components.BuscadorDeCalles
import com.example.proyecto_final.presentation.screens.viewModel.MapaViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Marker

@Composable
fun MapaPantalla(
    viewModel: MapaViewModel = viewModel()
) {
    val reportes by viewModel.reportesAgregados.collectAsState()

    val coordenadaInicial = LatLng(-12.0464, -77.0428)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(coordenadaInicial, 13f)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            reportes.forEach { reporte ->
                Circle(
                    center = reporte.centro,
                    radius = 40.0,
                    fillColor = reporte.color.copy(alpha = 0.35f),
                    strokeColor = reporte.color,
                    strokeWidth = 3f
                )
                Marker(
                    state = MarkerState(position = reporte.centro),
                    title = "${reporte.totalReportes} reporte(s)",
                    snippet = "${reporte.porcentajePeligro}% la marcó como peligrosa"
                )
            }
        }

        BuscadorDeCalles(
            modifier = Modifier.align(Alignment.TopCenter),
            onLugarSeleccionado = { lugarElegido ->
                println("El usuario buscó: $lugarElegido")
            }
        )
    }
}