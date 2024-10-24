package com.bimenesvive.bimenesvive.modelo

import com.google.firebase.firestore.GeoPoint



data class Vivienda(
    val descripcion: String = "",
    val habitaciones: Int = 0,
    val localizacion: GeoPoint? = null,
    val metros: Int = 0,
    val ubicacion: String = "",
    val titulo: String = "",
    val userID: String = "",
    val documentID: String = "",
    val telefono: String = "",
    val email: String = "",
    val disponible: Boolean = true,
    val visitas: Int = 0,
    val estado: EstadoPropiedad = EstadoPropiedad.VENTA,
    val precio: Int = 0
)

