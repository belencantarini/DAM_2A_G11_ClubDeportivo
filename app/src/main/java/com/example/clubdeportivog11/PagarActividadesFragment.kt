package com.example.clubdeportivog11

import android.app.AlertDialog
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
import com.example.clubdeportivog11.adapters.ActividadesAdapterCheckBox
import com.example.clubdeportivog11.models.ActividadesDataClass
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PagarActividadesFragment : Fragment() {

    private var clienteId: Long = -1
    private var actividadesSeleccionadas: List<ActividadesDataClass> = emptyList()
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
    ): View = inflater.inflate(R.layout.fragment_pagar_actividades, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (clienteId == -1L) {
            Toast.makeText(requireContext(), "Cliente inválido", Toast.LENGTH_SHORT).show()
            return
        }

        // Referencias de UI
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewActividadesPago)
        val textTotal = view.findViewById<TextView>(R.id.textTotalMonto)
        val btnVolver = view.findViewById<Button>(R.id.btnVolverPagoActividad)
        val btnConfirmar = view.findViewById<Button>(R.id.btnConfirmarPagoActividades)

        textNombreCliente = view.findViewById(R.id.textApellidoNombre)
        textDocCliente = view.findViewById(R.id.textDOCyNro)

        cargarDatosCliente(view)

        val listaActividades = dbHelper.obtenerActividades()
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = ActividadesAdapterCheckBox(listaActividades) { seleccionadas ->
            actividadesSeleccionadas = seleccionadas
            val total = actividadesSeleccionadas.sumOf { it.precio }
            textTotal.text = String.format("$%.2f", total)
        }

        btnVolver.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        btnConfirmar.setOnClickListener {
            if (actividadesSeleccionadas.isEmpty()) {
                Toast.makeText(requireContext(), "Seleccione al menos una actividad", Toast.LENGTH_SHORT).show()
            } else {
                mostrarDialogoMetodoPago()
            }
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
                c.NumeroDocumento, 
                CASE WHEN s.SocioID IS NOT NULL THEN 'Socio' ELSE 'No Socio' END AS TipoCliente
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

            textNombreCliente.text = "$apellido, $nombre"
            textDocCliente.text = "$tipoDoc $nroDoc"
            view.findViewById<TextView>(R.id.textTipoClienteFechaVenc).text = tipoCliente
        } else {
            Toast.makeText(requireContext(), "No se encontró el cliente", Toast.LENGTH_SHORT).show()
        }

        cursor.close()
        db.close()
    }

    private fun mostrarDialogoMetodoPago() {
        val metodos = arrayOf("Efectivo", "Tarjeta", "Transferencia")
        var metodoSeleccionado = metodos[0]

        AlertDialog.Builder(requireContext())
            .setTitle("Seleccioná un método de pago")
            .setSingleChoiceItems(metodos, 0) { _, which ->
                metodoSeleccionado = metodos[which]
            }
            .setPositiveButton("Confirmar") { _, _ ->
                procesarPago(metodoSeleccionado)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun procesarPago(metodo: String) {
        val fechaPago = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val cuotas = if (metodo == "Tarjeta") 3 else 1

        var todosExitosos = true

        for (actividad in actividadesSeleccionadas) {
            val exito = dbHelper.ingresarPago(
                clienteID = clienteId,
                monto = actividad.precio,
                metodoPago = metodo,
                cuotas = cuotas,
                fechaPago = fechaPago,
                actividadID = actividad.actividadId
            )
            if (!exito) todosExitosos = false
        }

        if (todosExitosos) {
            val resumen = buildString {
                append("Comprobante de Pago\n\n")
                append("Cliente: ${textNombreCliente.text}\n")
                append("Documento: ${textDocCliente.text}\n")
                append("Método: $metodo\n")
                append("Actividades abonadas:\n")
                actividadesSeleccionadas.forEach {
                    append("- ${it.nombre}: $${String.format("%.2f", it.precio)}\n")
                }
                val total = actividadesSeleccionadas.sumOf { it.precio }
                append("\nTotal pagado: $${String.format("%.2f", total)}")
            }

            AlertDialog.Builder(requireContext())
                .setTitle("Pago exitoso")
                .setMessage(resumen)
                .setPositiveButton("Aceptar") { dialog, _ ->
                    dialog.dismiss()
                    parentFragmentManager.popBackStack()
                }
                .show()
        } else {
            Toast.makeText(requireContext(), "Error al registrar el pago", Toast.LENGTH_SHORT).show()
        }
    }
}