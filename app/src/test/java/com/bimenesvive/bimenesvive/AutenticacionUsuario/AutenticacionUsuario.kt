package com.bimenesvive.bimenesvive.AutenticacionUsuario


class AutenticacionUsuario {
    /**
     * Verifica si el usuario está autenticado.
     * @param userId ID del usuario.
     * @return true si el usuario está autenticado, false en caso contrario.
     */
    fun isUserAuthenticated(userId: String): Boolean {
        // Simula la lógica de autenticación, si el userId no está vacío, el usuario está autenticado
        return userId.isNotEmpty()
    }
}