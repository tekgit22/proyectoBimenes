package com.bimenesvive.bimenesvive.autenticacion

// Sealed class para representar los estados de autenticación
// Compuesta por 4 objetos singleton y una data class
// El contenido de la sealed class debe de ser declarado en el mismo archivo que la sealed class

sealed class EstadoAutenticacion {
    object Desautenticado : EstadoAutenticacion()
    object Autenticado : EstadoAutenticacion()
    object ProcesandoAutentificacion : EstadoAutenticacion()
    data class AutentificacionFallida(val mensajeError: String) : EstadoAutenticacion()
}

