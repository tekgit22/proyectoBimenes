package com.bimenesvive.bimenesvive.TestLogin

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class FirebaseLogin {

    // Instancia de FirebaseAuth para poder hacer login
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    /**
     * Función que hace login con un usuario y contraseña
     * @param email Correo del usuario
     * @param password Contraseña del usuario
     * @return true si el login fue correcto, false si hubo algún fallo
     * @throws Exception si hubo algún fallo en el login
     */
    suspend fun hacerLogin(email: String, password: String): Boolean {
        return try {
            val resultado = auth.signInWithEmailAndPassword(email, password).await()
            val user = resultado.user
            if (user != null) {
                Log.d("FirebaseLogin", "Se hizo Login correctamente con el correo: ${user.email}")
                true
            } else {
                Log.d("FirebaseLogin", "Hubo un fallo: El usuario es null")
                false
            }
        } catch (e: Exception) {
            Log.d("FirebaseLogin", "El login falló: ${e.message}")
            false
        }
    }
}