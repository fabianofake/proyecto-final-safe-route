package com.example.proyecto_final.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.proyecto_final.presentation.Login.LoginPantalla
import com.example.proyecto_final.presentation.Login.RegistroPantalla
import com.example.proyecto_final.presentation.screens.PrincipalPantalla

@Composable
fun EnrutadorPrincipal() {
    val rootNavController = rememberNavController()

    NavHost(
        navController = rootNavController,
        startDestination = "login_ruta"
    ) {
        composable("login_ruta") {
            LoginPantalla(
                onLoginExitoso = { nombre ->
                    rootNavController.navigate("principal_ruta") {
                        popUpTo("login_ruta") { inclusive = true }
                    }
                },
                onNavegarARegistro = {
                    rootNavController.navigate("registro_ruta")
                }
            )
        }

        composable("registro_ruta") {
            RegistroPantalla(
                onRegistroExitoso = {
                    rootNavController.navigate("principal_ruta") {
                        popUpTo("login_ruta") { inclusive = true }
                    }
                },
                onNavegarALogin = {
                    rootNavController.popBackStack()
                }
            )
        }

        composable("principal_ruta") {
            PrincipalPantalla(
                onCerrarSesion = {
                    rootNavController.navigate("login_ruta") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}