package com.bimenesvive.bimenesvive.modelos


//Data class proporciona automáticamente métodos como toString(), equals(), hashCode(), y copy()
data class Anuncio(
    val titulo: String = "",
    val contacto: String = "",
    val mensaje: String = "",
    val userID: String = ""
) {


    // Constructor sin argumentos:
    // constructor() : this("", "", "", "")

    // El constructor sin argumentos es necesario para poder deserializar los datos de Firestore en un objeto Anuncio

    // Como estamos utilizando una data class, no es necesario definir un constructor sin argumentos
    // porque Kotlin ya lo hace automáticamente si todas las propiedades tienen valores por defecto

    // Data class genera automáticamente: toString(), equals(), hashCode(), copy(), constructor sin argumentos y constructor con todos los argumentos
}