package com.example.proyecto_final.data

// Guarda al usuario actual en memoria mientras la app está abierta.
// Simple y suficiente para empezar; si luego quieres que sobreviva
// a cerrar la app, lo migramos a DataStore.
object SesionUsuario {
    var correo: String? = null
        private set
    var nombre: String? = null
        private set

    fun iniciarSesion(correo: String, nombre: String?) {
        this.correo = correo
        this.nombre = nombre
    }

    fun cerrarSesion() {
        correo = null
        nombre = null
    }

    val estaLogueado: Boolean
        get() = correo != null
}