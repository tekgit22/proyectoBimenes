package com.bimenesvive.bimenesvive.AutenticacionUsuario


class AutenticacionUsuario {
    fun isUserAuthenticated(userId: String): Boolean {
        // Simula la lógica de autenticación, si el userId no está vacío, el usuario está autenticado
        return userId.isNotEmpty()
    }
}