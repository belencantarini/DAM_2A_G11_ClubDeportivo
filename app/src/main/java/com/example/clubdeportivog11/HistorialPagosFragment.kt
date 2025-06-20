package com.example.clubdeportivog11

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clubdeportivog11.adapters.HistorialPagosAdapter
import com.example.clubdeportivog11.adapters.PlanesAdapterRadioButton


class HistorialPagosFragment : Fragment() {
    private var clienteId: Long = -1
    private lateinit var dbHelper: ClubDBHelper
    private lateinit var textNombreCliente: TextView
    private lateinit var textDocCliente: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        clienteId = arguments?.getLong("clienteId") ?: -1
        dbHelper = ClubDBHelper(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_historial_pagos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (clienteId == -1L) {
            Toast.makeText(requireContext(), "Cliente inválido", Toast.LENGTH_SHORT).show()
            return
        }

        textNombreCliente = view.findViewById(R.id.textApellidoNombre)
        textDocCliente = view.findViewById(R.id.textDOCyNro)

        cargarDatosCliente(view)


        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewHistorialPagos)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val listaPagos = dbHelper.obtenerHistorialPagos(clienteId)

        recyclerView.adapter = HistorialPagosAdapter(listaPagos)


        view.findViewById<Button>(R.id.btnVolverHistorialPagos).setOnClickListener {
            parentFragmentManager.popBackStack()
        }


    }

    private fun cargarDatosCliente(view: View) {
        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            """
            SELECT 
                c.Nombre, 
                c.Apellido, 
                c.TipoDocumento, 
                c.NumeroDocumento
            FROM Cliente c
            WHERE c.ClienteID = ?
            """.trimIndent(),
            arrayOf(clienteId.toString())
        )

        if (cursor.moveToFirst()) {
            val nombre = cursor.getString(0)
            val apellido = cursor.getString(1)
            val tipoDoc = cursor.getString(2)
            val nroDoc = cursor.getInt(3)

            textNombreCliente.text = "$apellido, $nombre"
            textDocCliente.text = "$tipoDoc $nroDoc"
        }

        cursor.close()
        db.close()
    }


}