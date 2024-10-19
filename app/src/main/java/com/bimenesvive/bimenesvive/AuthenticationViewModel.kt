package com.bimenesvive.bimenesvive

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import android.content.Context

class AuthenticationViewModel : ViewModel() {

    //private val sharedPreferences: SharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    //el _ privado es necesario para las variables privadas que tienen un LiveData de una variable publica asociada
    private val _authenticationState = MutableLiveData<AuthenticationState>()
    val authenticationState: LiveData<AuthenticationState> = _authenticationState //

    //
    init {
        comprobarEstadoAutenticacion()
    }

    private fun comprobarEstadoAutenticacion() {
        if (auth.currentUser == null) {
            _authenticationState.value = AuthenticationState.Desautenticado
        } else {
            _authenticationState.value = AuthenticationState.Autenticado
        }
    }

    fun sendPasswordResetEmail(email: String, context: Context) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Se envió un correo a tu cuenta", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Se produjo un error, inténtalo más tarde", Toast.LENGTH_SHORT).show()
                }
            }
    }


    //asincronía, task, null, elvis operator, etc
    fun login(email : String , password : String) {

        //Para evitar trabajo innecesario, no permito el login si los campos están vacíos
        if (email.isEmpty() || password.isEmpty()) {
            _authenticationState.value = AuthenticationState.AutentificacionFallida("Por favor, rellena todos los campos")
            return
        }

        _authenticationState.value = AuthenticationState.ProcesandoAutentificacion
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authenticationState.value = AuthenticationState.Autenticado
                } else {
                    _authenticationState.value = AuthenticationState.AutentificacionFallida(task.exception?.message ?: "Se ha producido un error")
                }
            }

    }

    fun registro(email : String , password : String) {

        //Para evitar trabajo innecesario, no permito el login si los campos están vacíos
        if (email.isEmpty() || password.isEmpty()) {
            _authenticationState.value = AuthenticationState.AutentificacionFallida("Por favor, rellena todos los campos")
            return
        }

        _authenticationState.value = AuthenticationState.ProcesandoAutentificacion
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authenticationState.value = AuthenticationState.Autenticado
                } else {
                    _authenticationState.value = AuthenticationState.AutentificacionFallida(task.exception?.message ?: "Se ha producido un error")
                }
            }

    }

    fun cerrarSesion() {
        auth.signOut()
        _authenticationState.value = AuthenticationState.Desautenticado
    }

    fun reiniciarEstadoAutenticacion() {
        _authenticationState.value = AuthenticationState.Desautenticado
    }

}

//Sealed class para representar los estados de autenticación, son singlenton y deben declararse en el mismo archivo
sealed class AuthenticationState {
    object Desautenticado : AuthenticationState()
    object Autenticado : AuthenticationState()
    object ProcesandoAutentificacion : AuthenticationState()
    data class AutentificacionFallida(val message: String) : AuthenticationState()
    //data class genera automáticamente equals, hashcode, toString, copy
    //lo estoy utilizando para trasladar un mensaje de error, puedo refactorizar y simplificar


}