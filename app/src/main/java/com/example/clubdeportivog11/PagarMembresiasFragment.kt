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
import com.example.clubdeportivog11.adapters.PlanesAdapter


class PagarMembresiasFragment : Fragment() {

    private var clienteId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        clienteId = arguments?.getLong("clienteId") ?: -1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pagar_membresias, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (clienteId == -1L) {
            Toast.makeText(requireContext(), "Cliente inválido", Toast.LENGTH_SHORT).show()
            return
        }
        // Cargar datos del cliente y mostrar en la UI
        cargarDatosCliente(view)



        val btnVolver = view.findViewById<Button>(R.id.btnVolverPagoMembresia)
        btnVolver.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun cargarDatosCliente(view: View) {

        val dbHelper = ClubDBHelper(requireContext())
        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            """
            SELECT 
                c.Nombre, 
                c.Apellido, 
                c.TipoDocumento, 
                c.NumeroDocumento, 
                CASE 
                    WHEN s.SocioID IS NOT NULL THEN 'Socio' 
                    ELSE 'No Socio' 
                END AS TipoCliente,
                s.FechaVencimientoCuota
            FROM Cliente c
            LEFT JOIN Socio s ON c.ClienteID = s.ClienteID
            WHERE c.ClienteID = ?   
            """.trimIndent(),
            arrayOf(clienteId.toString())
        )

        if (cursor.moveToFirst()) {
            val nombre = cursor.getString(0)
            val apellido = cursor.getString(1)
            val tipoDoc = cursor.getString(2)
            val nroDoc = cursor.getInt(3)
            val tipoCliente = cursor.getString(4)
            val fechaVenc = cursor.getString(5) ?: "Adeuda pago"

            view.findViewById<TextView>(R.id.textApellidoNombre).text = "$apellido, $nombre"
            view.findViewById<TextView>(R.id.textDOCyNro).text = "$tipoDoc $nroDoc"
            view.findViewById<TextView>(R.id.textTipoClienteFechaVenc).text =
                "$tipoCliente - Vence: $fechaVenc"
        } else {
            Toast.makeText(requireContext(), "No se encontró el cliente", Toast.LENGTH_SHORT).show()
        }

        cursor.close()
        db.close()

    }
}