package com.example.proyecto_final.presentation.Login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto_final.data.AuthRetrofitClient
import com.example.proyecto_final.data.RegistroRequestDto
import com.example.proyecto_final.data.SesionUsuario
import kotlinx.coroutines.launch

@Composable
fun RegistroPantalla(
    onRegistroExitoso: () -> Unit,
    onNavegarALogin: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var confirmarContrasena by remember { mutableStateOf("") }

    var contrasenaVisible by remember { mutableStateOf(false) }
    var confirmarContrasenaVisible by remember { mutableStateOf(false) }

    var cargando by remember { mutableStateOf(false) }
    var mensajeError by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Crear Cuenta",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo electrónico") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = contrasena,
            onValueChange = { contrasena = it },
            label = { Text("Contraseña") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (contrasenaVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            trailingIcon = {
                val imagen = if (contrasenaVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { contrasenaVisible = !contrasenaVisible }) {
                    Icon(imageVector = imagen, contentDescription = null)
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = confirmarContrasena,
            onValueChange = { confirmarContrasena = it },
            label = { Text("Repetir Contraseña") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (confirmarContrasenaVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            trailingIcon = {
                val imagen = if (confirmarContrasenaVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { confirmarContrasenaVisible = !confirmarContrasenaVisible }) {
                    Icon(imageVector = imagen, contentDescription = null)
                }
            }
        )

        if (mensajeError != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = mensajeError!!, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            enabled = !cargando,
            onClick = {
                mensajeError = null

                if (nombre.isBlank() || correo.isBlank() || contrasena.isBlank()) {
                    mensajeError = "Completa todos los campos"
                    return@Button
                }
                if (contrasena != confirmarContrasena) {
                    mensajeError = "Las contraseñas no coinciden"
                    return@Button
                }

                cargando = true
                scope.launch {
                    try {
                        val respuesta = AuthRetrofitClient.api.registrar(
                            RegistroRequestDto(nombre = nombre, correo = correo, contrasena = contrasena)
                        )

                        if (respuesta.isSuccessful && respuesta.body()?.registro == true) {
                            SesionUsuario.iniciarSesion(correo, nombre)
                            onRegistroExitoso()
                        } else {
                            mensajeError = respuesta.body()?.mensaje ?: "No se pudo crear la cuenta"
                        }
                    } catch (e: Exception) {
                        mensajeError = "Error de conexión: ${e.message}"
                    } finally {
                        cargando = false
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(if (cargando) "Creando cuenta..." else "Registrarse", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { onNavegarALogin() }) {
            Text("¿Ya tienes cuenta? Inicia sesión aquí")
        }
    }
}