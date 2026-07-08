package com.example.proyecto_final.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyecto_final.components.BuscadorDeCalles
import com.example.proyecto_final.data.ReporteAgregado
import com.example.proyecto_final.data.SesionUsuario
import com.example.proyecto_final.data.distanciaEnMetros
import com.example.proyecto_final.presentation.screens.viewModel.MapaViewModel
import com.example.proyecto_final.presentation.screens.viewModel.ReporteViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

private const val RADIO_UNION_METROS = 40f

@Composable
fun ReportePantalla(
    reporteViewModel: ReporteViewModel = viewModel(),
    mapaViewModel: MapaViewModel = viewModel()
) {
    val usuarioId = SesionUsuario.correo ?: "usuario-anonimo"

    val reportesExistentes by mapaViewModel.reportesAgregados.collectAsState()

    val coordenadaInicial = LatLng(-12.0464, -77.0428)
    var ubicacionSeleccionada by remember { mutableStateOf<LatLng?>(null) }
    var calleSeleccionada by remember { mutableStateOf<String?>(null) }
    var tipoSeleccionado by remember { mutableStateOf<String?>(null) }
    var reporteExistente by remember { mutableStateOf<ReporteAgregado?>(null) }
    var enviando by remember { mutableStateOf(false) }
    var mensajeExito by remember { mutableStateOf(false) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(coordenadaInicial, 14f)
    }

    // Busca si el punto tocado está cerca de un reporte existente y decide si "unirse" o crear nuevo
    fun seleccionarPunto(latLng: LatLng) {
        val cercano = reportesExistentes
            .map { it to distanciaEnMetros(latLng.latitude, latLng.longitude, it.centro.latitude, it.centro.longitude) }
            .minByOrNull { it.second }

        if (cercano != null && cercano.second < RADIO_UNION_METROS) {
            reporteExistente = cercano.first
            ubicacionSeleccionada = cercano.first.centro
            calleSeleccionada = cercano.first.calle
        } else {
            reporteExistente = null
            ubicacionSeleccionada = latLng
            calleSeleccionada = null
        }
        tipoSeleccionado = null
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapClick = { latLngTocado -> seleccionarPunto(latLngTocado) }
        ) {
            // Reportes existentes: círculo + marcador con el conteo
            reportesExistentes.forEach { reporte ->
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
                    snippet = "${reporte.porcentajePeligro}% la marcó como peligrosa",
                    onClick = {
                        seleccionarPunto(reporte.centro)
                        false // deja que Google Maps también muestre el globo de info
                    }
                )
            }

            // Marcador para un punto NUEVO (todavía no existe reporte ahí)
            if (ubicacionSeleccionada != null && reporteExistente == null) {
                Marker(
                    state = MarkerState(position = ubicacionSeleccionada!!),
                    title = "Nuevo punto a reportar"
                )
            }
        }

        BuscadorDeCalles(
            modifier = Modifier.align(Alignment.TopCenter),
            onLugarSeleccionado = { lugarElegido ->
                calleSeleccionada = lugarElegido
                if (ubicacionSeleccionada == null) {
                    ubicacionSeleccionada = coordenadaInicial
                }
                reporteExistente = null
                tipoSeleccionado = null
            }
        )

        // Paso 1: elegir Segura/Peligrosa
        if (ubicacionSeleccionada != null && tipoSeleccionado == null) {
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (reporteExistente != null) {
                        Text(
                            "Ya hay ${reporteExistente!!.totalReportes} reporte(s) en este punto",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "${reporteExistente!!.porcentajePeligro}% lo marcó como peligrosa",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(8.dp))
                        Text("¿Tú también quieres reportar aquí?", fontSize = 18.sp)
                    } else {
                        Text("¿Cómo calificas esta calle?", fontSize = 18.sp)
                    }

                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = { tipoSeleccionado = "SEGURO" },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047))
                        ) { Text("Segura") }

                        Button(
                            onClick = { tipoSeleccionado = "PELIGROSO" },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
                        ) { Text("Peligrosa") }
                    }
                }
            }
        }

        // Paso 2: confirmar envío
        if (tipoSeleccionado != null && ubicacionSeleccionada != null) {
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Confirmar reporte: ${if (tipoSeleccionado == "SEGURO") "Segura" else "Peligrosa"}")
                    Spacer(Modifier.height(12.dp))
                    Button(
                        enabled = !enviando,
                        onClick = {
                            enviando = true
                            reporteViewModel.enviarReporte(
                                ubicacion = ubicacionSeleccionada!!,
                                calle = calleSeleccionada,
                                tipo = tipoSeleccionado!!,
                                usuarioId = usuarioId
                            ) { exito ->
                                enviando = false
                                if (exito) {
                                    mensajeExito = true
                                    ubicacionSeleccionada = null
                                    tipoSeleccionado = null
                                    reporteExistente = null
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (enviando) "Enviando..." else "Confirmar Reporte")
                    }
                }
            }
        }

        if (mensajeExito) {
            LaunchedEffect(mensajeExito) {
                kotlinx.coroutines.delay(2000)
                mensajeExito = false
            }
            Card(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 80.dp)
            ) {
                Text("¡Reporte enviado!", modifier = Modifier.padding(12.dp))
            }
        }
    }
}