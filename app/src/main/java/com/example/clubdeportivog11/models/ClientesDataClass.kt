package com.example.clubdeportivog11.models

import org.w3c.dom.Text

data class ClientesDataClass(
    val clienteID: Long,
    val nombre: String,
    val apellido: String,
    val tipoDoc: String,
    val dni: Int,
    val esSocio: Boolean,
    val fechaVencimientoCuota: String? = null
)
