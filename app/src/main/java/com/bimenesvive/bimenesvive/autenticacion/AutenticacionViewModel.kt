package com.bimenesvive.bimenesvive.autenticacion

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import android.content.Context

class AutenticacionViewModel : ViewModel() {


    // NO es necesario SharedPreferences, ya que Firebase Auth mantiene el estado de autenticación
    private val autenticacion: FirebaseAuth = FirebaseAuth.getInstance()
    //el _ privado es necesario para las variables privadas que tienen un LiveData de una variable publica asociada
    private val _estadoAutenticacion = MutableLiveData<AuthenticationState>()
    val estadoAutenticacion: LiveData<AuthenticationState> = _estadoAutenticacion

    /** variable _privada con una pública asociada:
     * Mantiene la encapsulación de la variable privada y permite que la variable pública sea mutable
     * Esto asegura que solo el ViewModel pueda cambiar el estado de autenticación
     * El resto de la app (las otras clases) sólo pueden observar el estado de autenticación
     */


    /**
     * Inicializa el ViewModel, comprobando si el usuario ya está autenticado
     */
    init {
        comprobarEstadoAutenticacion()
    }


    /**
     * Comprueba si el usuario ya está autenticado
     */
    private fun comprobarEstadoAutenticacion() {
        if (autenticacion.currentUser == null) {
            _estadoAutenticacion.value = AuthenticationState.Desautenticado
        } else {
            _estadoAutenticacion.value = AuthenticationState.Autenticado
        }
    }

    /**
     * Envía un correo al usuario para recordarle su contraseña
     */
    fun enviarCorreoRecordarPassword(email: String, context: Context) {
        autenticacion.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Se envió un correo a tu cuenta", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Se produjo un error, inténtalo más tarde", Toast.LENGTH_SHORT).show()
                }
            }
    }


    //asincronía, task, null, elvis operator, etc
    /**
     * Inicia sesión con el email y la contraseña proporcionados
     */
    fun login(email : String , password : String) {

        //Para evitar trabajo innecesario, no permito el login si los campos están vacíos
        if (email.isEmpty() || password.isEmpty()) {
            _estadoAutenticacion.value =
                AuthenticationState.AutentificacionFallida("Por favor, rellena todos los campos")
            return
        }

        _estadoAutenticacion.value = AuthenticationState.ProcesandoAutentificacion
        autenticacion.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _estadoAutenticacion.value = AuthenticationState.Autenticado
                } else {
                    _estadoAutenticacion.value = AuthenticationState.AutentificacionFallida(
                        task.exception?.message ?: "Se ha producido un error"
                    )
                }
            }

    }

    /**
     * Registra un nuevo usuario con el email y la contraseña proporcionados
     */
    fun registro(email : String , password : String) {

        //Para evitar trabajo innecesario, no permito el login si los campos están vacíos
        if (email.isEmpty() || password.isEmpty()) {
            _estadoAutenticacion.value =
                AuthenticationState.AutentificacionFallida("Por favor, rellena todos los campos")
            return
        }

        _estadoAutenticacion.value = AuthenticationState.ProcesandoAutentificacion
        autenticacion.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _estadoAutenticacion.value = AuthenticationState.Autenticado
                } else {
                    _estadoAutenticacion.value = AuthenticationState.AutentificacionFallida(
                        task.exception?.message ?: "Se ha producido un error"
                    )
                }
            }

    }

    /**
     * Cierra la sesión del usuario actual
     */
    fun cerrarSesion() {
        autenticacion.signOut()
        _estadoAutenticacion.value = AuthenticationState.Desautenticado
    }

    /**
     * Reinicia el estado de autenticación a Desautenticado
     */
    fun reiniciarEstadoAutenticacion() {
        _estadoAutenticacion.value = AuthenticationState.Desautenticado
    }

}

//Sealed class para representar los estados de autenticación, son singlenton y deben declararse en el mismo archivo
sealed class AuthenticationState {
    object Desautenticado : AuthenticationState()
    object Autenticado : AuthenticationState()
    object ProcesandoAutentificacion : AuthenticationState()
    data class AutentificacionFallida(val message: String) : AuthenticationState()
}
//data class genera automáticamente equals, hashcode, toString, copy
//Lo dejo como placeholder porque sirve para trasladar el mensaje de error que recibimos de Firebase