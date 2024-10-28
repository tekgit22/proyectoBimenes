package com.bimenesvive.bimenesvive.UsuarioAdmin

import org.junit.Test
import org.junit.Assert.*

class UsuarioAdminTest {

    @Test
    fun testIsAdmin() {
        val usuarioAdmin = UsuarioAdmin()
        val userId = "ejemploAdminID"
        val resultado = usuarioAdmin.isAdmin(userId)
        println("testIsAdmin: $resultado")
        assertTrue(resultado)
    }

    @Test
    fun testIsNotAdmin() {
        val usuarioAdmin = UsuarioAdmin()
        val userId = "user_12345"
        val resultado = usuarioAdmin.isAdmin(userId)
        println("testIsNotAdmin: $resultado")
        assertFalse(resultado)
    }

    @Test
    fun testIsAdminWithNull() {
        val usuarioAdmin = UsuarioAdmin()
        val userId: String? = null
        val resultado = usuarioAdmin.isAdmin(userId)
        println("testIsAdminWithNull: $resultado")
        assertFalse(resultado)
    }
}