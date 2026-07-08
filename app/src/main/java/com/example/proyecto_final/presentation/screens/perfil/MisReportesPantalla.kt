package com.example.proyecto_final.presentation.screens.perfil

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyecto_final.data.SesionUsuario
import com.example.proyecto_final.presentation.screens.viewModel.ReporteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisReportesPantalla(
    onVolver: () -> Unit,
    viewModel: ReporteViewModel = viewModel()
) {
    val usuarioId = SesionUsuario.correo ?: "usuario-anonimo"
    val reportes by viewModel.reportes.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarReportes()
    }

    val misReportes = reportes.filter { it.usuarioId == usuarioId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de Reportes") },
                navigationIcon = {
                    IconButton(onClick = { onVolver() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (misReportes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Aún no has hecho ningún reporte")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(misReportes) { reporte ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = reporte.calle ?: "Calle sin nombre",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 18.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Tipo: ${reporte.tipo}",
                                    color = if (reporte.tipo == "PELIGROSO") Color.Red else Color(0xFF43A047),
                                    fontSize = 14.sp
                                )
                                reporte.distrito?.let {
                                    Text(text = it, fontSize = 12.sp, color = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}