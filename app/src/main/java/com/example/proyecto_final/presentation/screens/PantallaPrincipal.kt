package com.example.proyecto_final.presentation.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.proyecto_final.presentation.navigation.Pantalla
import androidx.navigation.compose.rememberNavController
import com.example.proyecto_final.presentation.screens.perfil.EditarPerfilPantalla
import com.example.proyecto_final.presentation.screens.perfil.MisReportesPantalla
import com.example.proyecto_final.presentation.screens.perfil.PerfilPantalla

@Composable
fun PrincipalPantalla(
    onCerrarSesion: () -> Unit = {}
){
    val navController= rememberNavController()

    val listaOpciones=listOf(
        Pantalla.Mapa,
        Pantalla.Reporte,
        Pantalla.Perfil,
    )

    Scaffold (
        bottomBar = {
            NavigationBar() {
                val rutaActual=navController.currentBackStackEntryAsState().value?.destination?.route

                listaOpciones.forEach { pantalla ->
                    NavigationBarItem(
                        selected = rutaActual==pantalla.ruta,
                        onClick = {
                            navController.navigate(pantalla.ruta){
                                popUpTo(Pantalla.Mapa.ruta)
                                launchSingleTop=true
                            }
                        },
                        icon = {
                            Icon(pantalla.icono, contentDescription = pantalla.titulo)
                        },
                        label={
                            Text(pantalla.titulo)
                        }
                    )
                }
            }
        }
    ) {
            paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Pantalla.Mapa.ruta,
            modifier = Modifier.padding(paddingValues)
        ) {

            composable(Pantalla.Mapa.ruta) { MapaPantalla() }
            composable(Pantalla.Reporte.ruta) { ReportePantalla() }

            composable(Pantalla.Perfil.ruta) {
                PerfilPantalla(
                    onCerrarSesion = onCerrarSesion,
                    onNavegarAMisReportes = {
                        navController.navigate("mis_reportes_ruta")
                    },
                    onNavegarAEditarPerfil = {
                        navController.navigate("editar_perfil_ruta")
                    }
                )
            }

            composable("mis_reportes_ruta") {
                MisReportesPantalla(
                    onVolver = {
                        navController.popBackStack()
                    }
                )
            }
            composable("editar_perfil_ruta") {
                EditarPerfilPantalla(
                    onVolver = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}