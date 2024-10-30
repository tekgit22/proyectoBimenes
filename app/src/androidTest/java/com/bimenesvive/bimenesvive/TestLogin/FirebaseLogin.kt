package com.bimenesvive.bimenesvive.TestLogin

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class FirebaseLogin {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

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