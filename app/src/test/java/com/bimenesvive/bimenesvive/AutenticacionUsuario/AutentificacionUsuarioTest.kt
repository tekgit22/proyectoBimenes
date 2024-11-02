package com.bimenesvive.bimenesvive.AutenticacionUsuario

import org.junit.Test
import org.junit.Assert.*

class AutenticacionUsuarioTest {

    //Este test simula el login de un usuario para comprobar si está autenticado
    //NO utiliza Firebase, por lo que no es necesario tener conexión a internet ni emular un dispositivo

    /**
     * Prueba si el usuario está autenticado.
     */
    @Test
    fun testEstaAutenticado() {
        val autenticacionUsuario = AutenticacionUsuario()
        val userId = "gB895udBWiaCKNMnGUidMbKyQii2"
        val resultado = autenticacionUsuario.isUserAuthenticated(userId)
        println("testEstaAutenticado - :  Autenticacion = $resultado")
        assertTrue(resultado)
    }

    /**
     * Prueba si el usuario no está autenticado.
     */
    @Test
    fun testNoEstaAutenticado() {
        val autenticacionUsuario = AutenticacionUsuario()
        val userId = ""
        val resultado = autenticacionUsuario.isUserAuthenticated(userId)
        println("testNoEstaAutenticado - :  Autenticacion = $resultado")
        assertFalse(resultado)
    }
}