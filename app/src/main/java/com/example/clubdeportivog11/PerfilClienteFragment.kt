package com.example.clubdeportivog11

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast


class PerfilClienteFragment : Fragment() {

    private var clienteId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Obtener el clienteId pasado en el Bundle
        clienteId = arguments?.getLong("clienteId") ?: -1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_perfil_cliente, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Cargar datos del cliente y mostrar en la UI
        cargarDatosCliente(view)
    }

    private fun cargarDatosCliente(view: View) {
        if (clienteId == -1L) {
            Toast.makeText(requireContext(), "Cliente inválido", Toast.LENGTH_SHORT).show()
            return
        }

        val dbHelper = ClubDBHelper(requireContext())
        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            """
            SELECT Nombre, Apellido, TipoDocumento, NumeroDocumento, FechaInscripcion, AptoFisico 
            FROM Cliente 
            WHERE ClienteID = ?
            """.trimIndent(),
            arrayOf(clienteId.toString())
        )

        if (cursor.moveToFirst()) {
            view.findViewById<TextView>(R.id.textNombre).text = cursor.getString(0)
            view.findViewById<TextView>(R.id.textApellido).text = cursor.getString(1)
            view.findViewById<TextView>(R.id.textTipoDoc).text = cursor.getString(2)
            view.findViewById<TextView>(R.id.textNroDoc).text = cursor.getInt(3).toString()
            view.findViewById<TextView>(R.id.textFechaInicio).text = cursor.getString(4)
            view.findViewById<CheckBox>(R.id.checkAptoFisicoEntregado).isChecked = cursor.getInt(5) == 1
        } else {
            Toast.makeText(requireContext(), "Cliente no encontrado", Toast.LENGTH_SHORT).show()
        }
        cursor.close()

        val socioCursor = db.rawQuery(
            "SELECT FechaVencimientoCuota FROM Socio WHERE ClienteID = ?",
            arrayOf(clienteId.toString())
        )

        if (socioCursor.moveToFirst()) {
            view.findViewById<TextView>(R.id.textTipoCliente).text = "Socio"
            view.findViewById<TextView>(R.id.textProxVenc).text = socioCursor.getString(0)
        } else {
            view.findViewById<TextView>(R.id.textTipoCliente).text = "No Socio"
            view.findViewById<TextView>(R.id.textProxVenc).text = "-"
        }

        socioCursor.close()

        db.close()

        // Botón Volver
        val btnVolver = view.findViewById<Button>(R.id.btnVolverPerfil)
        btnVolver.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        
    }
}
