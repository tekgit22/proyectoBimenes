package com.bimenesvive.bimenesvive.UsuarioAdmin

class UsuarioAdmin {
    private val adminID = "ejemploAdminID"

    fun isAdmin(userId: String?): Boolean {
        // Verifica si el userId no es null y coincide con el adminID
        return userId != null && userId == adminID
    }
}