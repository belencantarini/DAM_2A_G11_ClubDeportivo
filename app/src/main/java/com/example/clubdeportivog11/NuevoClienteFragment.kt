package com.example.clubdeportivog11

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class NuevoClienteFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nuevo_cliente, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Configurar spinner de tipos de documento
        val spinner: Spinner = view.findViewById(R.id.spinnerTipoDocumento)
        val tiposDocumento = listOf("DNI", "Pasaporte", "Cédula")
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            tiposDocumento
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Botón Volver
        val btnVolver = view.findViewById<Button>(R.id.btnVolverRC)
        btnVolver.setOnClickListener {
            // Cerrar fragmento regresando
            activity?.onBackPressedDispatcher
        }

        // (Opcional) Botón Registrar Cliente
        val btnRegistrarCliente = view.findViewById<Button>(R.id.btnRegistrarCliente)
        btnRegistrarCliente.setOnClickListener {
            // Lógica para registrar cliente...
        }
    }


}