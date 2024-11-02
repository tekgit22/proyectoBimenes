package com.bimenesvive.bimenesvive.UsuarioAdmin

class UsuarioAdmin {
    private val adminID = "ejemploAdminID"

    /**
     * Verifica si el usuario es administrador.
     * @param userId ID del usuario.
     * @return true si el usuario es administrador, false en caso contrario.
     */
    fun isAdmin(userId: String?): Boolean {
        // Verifica si el userId no es null y coincide con el adminID
        return userId != null && userId == adminID
    }
}