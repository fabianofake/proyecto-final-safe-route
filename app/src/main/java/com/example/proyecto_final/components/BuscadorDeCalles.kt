package com.example.proyecto_final.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest

// Un modelo sencillo para guardar los resultados de Google
data class LugarGoogle(
    val placeId: String,
    val calle: String,
    val distrito: String
)

@Composable
fun BuscadorDeCalles(
    modifier: Modifier = Modifier,
    onLugarSeleccionado: (String) -> Unit
) {
    var textoBusqueda by remember { mutableStateOf("") }
    var mostrarResultados by remember { mutableStateOf(false) }

    // Aquí guardaremos los resultados reales de internet
    var resultados by remember { mutableStateOf<List<LugarGoogle>>(emptyList()) }

    // Obtenemos el cliente de Places
    val context = LocalContext.current
    val placesClient = remember { Places.createClient(context) }

    // Función que se conecta a Google cada vez que escribes una letra
    fun buscarEnGoogle(query: String) {
        if (query.isEmpty()) {
            resultados = emptyList()
            return
        }

        val token = AutocompleteSessionToken.newInstance()
        val request = FindAutocompletePredictionsRequest.builder()
            // Filtramos para que preferentemente devuelva direcciones en Perú (opcional pero recomendado)


            ///////////////BORRAR///////////////////.setCountry("PE")


            .setSessionToken(token)
            .setQuery(query)
            .build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response ->
                // Transformamos la respuesta de Google a nuestra lista
                resultados = response.autocompletePredictions.map { prediccion ->
                    LugarGoogle(
                        placeId = prediccion.placeId,
                        calle = prediccion.getPrimaryText(null).toString(),
                        distrito = prediccion.getSecondaryText(null).toString()
                    )
                }
            }
            .addOnFailureListener { exception ->
                println("Error buscando lugares: ${exception.message}")
            }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Barra de búsqueda
            TextField(
                value = textoBusqueda,
                onValueChange = { nuevoTexto ->
                    textoBusqueda = nuevoTexto
                    mostrarResultados = nuevoTexto.isNotEmpty()
                    buscarEnGoogle(nuevoTexto) // Llamamos a Google al escribir
                },
                placeholder = { Text("Buscar calle o zona...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                singleLine = true
            )

            // Lista de resultados desplegable (Datos Reales)
            if (mostrarResultados && resultados.isNotEmpty()) {
                HorizontalDivider()
                LazyColumn(
                    modifier = Modifier.heightIn(max = 250.dp)
                ) {
                    items(resultados) { lugar ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    textoBusqueda = lugar.calle
                                    mostrarResultados = false
                                    // Más adelante enviaremos el placeId para obtener la coordenada exacta
                                    onLugarSeleccionado(lugar.calle)
                                }
                                .padding(16.dp)
                        ) {
                            Text(text = lugar.calle, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                            Text(text = lugar.distrito, color = Color.Gray, fontSize = 14.sp)
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                    }
                }
            }
        }
    }
}