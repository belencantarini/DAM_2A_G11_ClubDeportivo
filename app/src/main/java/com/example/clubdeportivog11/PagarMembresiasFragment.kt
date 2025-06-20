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
import com.example.clubdeportivog11.adapters.PlanesAdapter
import com.example.clubdeportivog11.adapters.PlanesAdapterRadioButton
import com.example.clubdeportivog11.models.PlanesDataClass
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class PagarMembresiasFragment : Fragment() {

    private var clienteId: Long = -1
    private lateinit var dbHelper: ClubDBHelper
    private lateinit var textNombreCliente: TextView
    private lateinit var textDocCliente: TextView
    private var planSeleccionado: PlanesDataClass? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        clienteId = arguments?.getLong("clienteId") ?: -1
        dbHelper = ClubDBHelper(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_pagar_membresias, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (clienteId == -1L) {
            Toast.makeText(requireContext(), "Cliente inválido", Toast.LENGTH_SHORT).show()
            return
        }

        textNombreCliente = view.findViewById(R.id.textApellidoNombre)
        textDocCliente = view.findViewById(R.id.textDOCyNro)

        cargarDatosCliente(view)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewMembresiasPago)
        val textTotal = view.findViewById<TextView>(R.id.textTotalMonto)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val listaPlanes = dbHelper.obtenerPlanes()
        val adapter = PlanesAdapterRadioButton(listaPlanes) { plan ->
            planSeleccionado = plan
            textTotal.text = String.format("$%.2f", plan.precio)
        }

        recyclerView.adapter = adapter

        view.findViewById<Button>(R.id.btnVolverPagoMembresia).setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        view.findViewById<Button>(R.id.btnConfirmarPagoMembresias).setOnClickListener {
            if (planSeleccionado == null) {
                Toast.makeText(requireContext(), "Seleccione una membresía", Toast.LENGTH_SHORT).show()
            } else {
                mostrarDialogoMetodoPago(view)
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
                CASE WHEN s.SocioID IS NOT NULL THEN 'Socio' ELSE 'No Socio' END AS TipoCliente,
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

            textNombreCliente.text = "$apellido, $nombre"
            textDocCliente.text = "$tipoDoc $nroDoc"
            view.findViewById<TextView>(R.id.textTipoClienteFechaVenc).text =
                "$tipoCliente - Vence: $fechaVenc"
        }

        cursor.close()
        db.close()
    }

    private fun mostrarDialogoMetodoPago(view: View) {
        val metodos = arrayOf("Efectivo", "Tarjeta", "Transferencia")
        var metodoSeleccionado = metodos[0]

        AlertDialog.Builder(requireContext())
            .setTitle("Seleccioná un método de pago")
            .setSingleChoiceItems(metodos, 0) { _, which ->
                metodoSeleccionado = metodos[which]
            }
            .setPositiveButton("Confirmar") { _, _ ->
                if (metodoSeleccionado == "Tarjeta") {
                    mostrarDialogoCuotas(view)
                } else {
                    procesarPago(view, metodoSeleccionado, cuotas = 1)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarDialogoCuotas(view: View) {
        val opcionesCuotas = arrayOf("1 cuota", "3 cuotas", "6 cuotas")
        var cuotasSeleccionadas = 1

        AlertDialog.Builder(requireContext())
            .setTitle("Elegí la cantidad de cuotas")
            .setSingleChoiceItems(opcionesCuotas, 0) { _, which ->
                cuotasSeleccionadas = when (which) {
                    0 -> 1
                    1 -> 3
                    2 -> 6
                    else -> 1
                }
            }
            .setPositiveButton("Confirmar") { _, _ ->
                procesarPago(view, "Tarjeta", cuotasSeleccionadas)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun procesarPago(view: View, metodo: String, cuotas: Int) {
        val plan = planSeleccionado ?: return
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val fechaPago = sdf.format(Date())

        val exito = dbHelper.ingresarPago(
            clienteID = clienteId,
            monto = plan.precio,
            metodoPago = metodo,
            cuotas = cuotas,
            fechaPago = fechaPago,
            membresiaID = plan.planId
        )

        if (exito) {
            val db = dbHelper.readableDatabase
            val cursor = db.rawQuery(
                "SELECT FechaVencimientoCuota FROM Socio WHERE ClienteID = ?",
                arrayOf(clienteId.toString())
            )

            val fechaBase = Calendar.getInstance()

            if (cursor.moveToFirst()) {
                val fechaActualStr = cursor.getString(0)
                if (!fechaActualStr.isNullOrBlank()) {
                    val fechaActual = Calendar.getInstance().apply {
                        time = sdf.parse(fechaActualStr)!!
                    }
                    if (fechaActual.after(Calendar.getInstance())) {
                        fechaBase.time = fechaActual.time
                    }
                }
            }
            cursor.close()

            // Sumar los meses del plan
            fechaBase.add(Calendar.MONTH, plan.meses)
            val nuevaFechaVenc = sdf.format(fechaBase.time)

            // Actualizar fecha
            val updateStmt = db.compileStatement(
                "UPDATE Socio SET FechaVencimientoCuota = ? WHERE ClienteID = ?"
            )
            updateStmt.bindString(1, nuevaFechaVenc)
            updateStmt.bindLong(2, clienteId)
            updateStmt.executeUpdateDelete()
            db.close()

            // Mostrar comprobante
            val resumen = buildString {
                append("Comprobante de Pago\n\n")
                append("Cliente: ${textNombreCliente.text}\n")
                append("Documento: ${textDocCliente.text}\n")
                append("Método: $metodo\n")
                append("Cuotas: $cuotas\n")
                append("Membresía: ${plan.nombre} - $${String.format("%.2f", plan.precio)}\n")
                append("Fecha: $fechaPago\n")
                append("Nueva fecha de vencimiento: $nuevaFechaVenc\n")
                append("\nTotal pagado: $${String.format("%.2f", plan.precio)}")
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