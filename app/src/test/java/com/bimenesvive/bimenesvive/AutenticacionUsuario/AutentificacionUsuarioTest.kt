package com.bimenesvive.bimenesvive.AutenticacionUsuario

import org.junit.Test
import org.junit.Assert.*

class AutenticacionUsuarioTest {

    @Test
    fun testIsUserAuthenticated() {
        val autenticacionUsuario = AutenticacionUsuario()
        val userId = "gB895udBWiaCKNMnGUidMbKyQii2"
        val resultado = autenticacionUsuario.isUserAuthenticated(userId)
        println("testIsUserAuthenticated: $resultado")
        assertTrue(resultado)
    }

    @Test
    fun testIsUserNotAuthenticated() {
        val autenticacionUsuario = AutenticacionUsuario()
        val userId = ""
        val resultado = autenticacionUsuario.isUserAuthenticated(userId)
        println("testIsUserNotAuthenticated: $resultado")
        assertFalse(resultado)
    }
}