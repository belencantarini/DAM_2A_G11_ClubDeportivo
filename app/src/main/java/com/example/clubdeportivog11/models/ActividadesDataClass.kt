package com.example.clubdeportivog11.models

data class ActividadesDataClass(
    val actividadId: Int,
    val nombre: String,
    val precio: Double,
    var seleccionado: Boolean = false
)
