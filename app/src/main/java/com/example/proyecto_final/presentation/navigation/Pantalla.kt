package com.example.proyecto_final.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person2
import androidx.compose.material.icons.filled.Report
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Pantalla (val ruta: String, val titulo: String, val icono: ImageVector){
    object Mapa: Pantalla("mapa", "Mapa", Icons.Default.Home)
    object Reporte: Pantalla("reporte","Reporte", Icons.Default.Report)
    object Perfil: Pantalla("perfil","Perfil", Icons.Default.Person2)
}