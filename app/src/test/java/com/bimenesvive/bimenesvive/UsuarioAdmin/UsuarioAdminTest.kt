package com.bimenesvive.bimenesvive.UsuarioAdmin

import org.junit.Test
import org.junit.Assert.*

class UsuarioAdminTest {

    //Este test simula el login de un usuario para comprobar si es administrador
    //NO utiliza Firebase, por lo que no es necesario tener conexión a internet ni emular un dispositivo

    /**
     * Prueba si el usuario es un administrador.
     */
    @Test
    fun testEsAdmin() {
        val usuarioAdmin = UsuarioAdmin()
        val userId = "ejemploAdminID"
        val resultado = usuarioAdmin.isAdmin(userId)
        println("Test Es Admin :  Admin = $resultado")
        assertTrue(resultado)
    }

    /**
     * Prueba si el usuario no es un administrador.
     */
    @Test
    fun testNoEsAdmin() {
        val usuarioAdmin = UsuarioAdmin()
        val userId = "user_12345"
        val resultado = usuarioAdmin.isAdmin(userId)
        println("Test No es Admin :  Admin = $resultado")
        assertFalse(resultado)
    }

    /**
     * Si el usuario es null, no es un administrador.
     */
    @Test
    fun testNoEsAdminNull() {
        val usuarioAdmin = UsuarioAdmin()
        val userId: String? = null
        val resultado = usuarioAdmin.isAdmin(userId)
        println("Test No es Admin (null) :  Admin = $resultado")
        assertFalse(resultado)
    }
}