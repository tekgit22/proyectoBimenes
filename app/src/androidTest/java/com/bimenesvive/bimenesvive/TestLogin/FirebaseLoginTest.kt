package com.bimenesvive.bimenesvive.TestLogin

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FirebaseLoginTest {

    @Test
    fun testLoginValido() = runBlocking {
        val email = "testing@bimenesvive.es"
        val password = "111111"
        val firebaseLogin = FirebaseLogin()

        val estaLogado = firebaseLogin.hacerLogin(email, password)

        assertTrue("El usuario debería de estar logado", estaLogado)
    }

    @Test
    fun testLoginDatosInvalidos() = runBlocking {
        val email = "invalido@bimenesvive.es"
        val password = "333333"
        val firebaseLogin = FirebaseLogin()

        val estaLogado = firebaseLogin.hacerLogin(email, password)

        assertFalse("El usuario NO debería de estar logado", estaLogado)
    }
}