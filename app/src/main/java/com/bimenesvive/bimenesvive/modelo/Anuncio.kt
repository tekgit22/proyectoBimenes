package com.bimenesvive.bimenesvive.modelo


//Data class proporciona automáticamente métodos como toString(), equals(), hashCode(), y copy()
data class Anuncio(
    val titulo: String = "",
    val contacto: String = "",
    val mensaje: String = "",
    val userID: String = ""
) {
//    El constructor sin argumentos permite a Firebase crear una instancia de la clase Anuncio sin necesidad
//    de proporcionar valores iniciales para sus propiedades
//    Es Esencial para que Firebase pueda deserializar los datos del documento en un objeto Anuncio
    constructor() : this("", "", "", "")
}