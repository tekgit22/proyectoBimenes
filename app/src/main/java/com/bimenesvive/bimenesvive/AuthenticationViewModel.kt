package com.bimenesvive.bimenesvive

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class AuthenticationViewModel : ViewModel() {

    //private val sharedPreferences: SharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _authenticationState = MutableLiveData<AuthenticationState>()  //recuerda mirar el _
    val authenticationState: LiveData<AuthenticationState> = _authenticationState //estudiar y entender por qué se hace esto

    //estudiar por qué init
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


    //Aquí tengo que estudiar asincronía, cómo funciona el task, el null, elvis operator, etc
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

//estudiar la sealed class
sealed class AuthenticationState {
    object Desautenticado : AuthenticationState()
    object Autenticado : AuthenticationState()
    object ProcesandoAutentificacion : AuthenticationState()
    data class AutentificacionFallida(val message: String) : AuthenticationState()  //estudiar


}