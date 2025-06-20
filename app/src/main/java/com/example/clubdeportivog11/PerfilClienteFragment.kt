package com.example.clubdeportivog11

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale


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
        cargarTipoSocio(view)
        db.close()

        // Botón Volver
        val btnVolver = view.findViewById<Button>(R.id.btnVolverPerfil)
        btnVolver.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Boton ir a pagar membresias

        val btnIrAPagarSocios = view.findViewById<Button>(R.id.btnIrAPagarSocios)
        btnIrAPagarSocios.setOnClickListener {
            val pagarFragment = PagarMembresiasFragment().apply {
                arguments = Bundle().apply {
                    putLong("clienteId", clienteId)
                }
            }
            (activity as? MainActivity)?.irASeccion(pagarFragment)
        }

        // Boton ir a pagar actividades
        val btnIrAPagarNoSocios = view.findViewById<Button>(R.id.btnIrAPagarNoSocios)
        btnIrAPagarNoSocios.setOnClickListener {
            val pagarFragment = PagarActividadesFragment().apply {
                arguments = Bundle().apply {
                    putLong("clienteId", clienteId)
                }
            }
            (activity as? MainActivity)?.irASeccion(pagarFragment)
        }

        // Boton ir ver Historial de Pagos
        val btnVerHistorialPagos = view.findViewById<Button>(R.id.btnHistorialPagos)
        btnVerHistorialPagos.setOnClickListener {
            val historialFragment = HistorialPagosFragment().apply {
                arguments = Bundle().apply {
                    putLong("clienteId", clienteId)
                }
            }
            (activity as? MainActivity)?.irASeccion(historialFragment)
        }


        // Boton Quiero ser Socio
        val btnQuieroSerSocio = view.findViewById<Button>(R.id.btnQuieroSerSocio)
        btnQuieroSerSocio.setOnClickListener {
            val fueExitoso = dbHelper.convertirNoSocioEnSocio(clienteId)
            if (fueExitoso) {
                Toast.makeText(requireContext(), "Ahora sos socio 🎉", Toast.LENGTH_SHORT).show()
                // Podés recargar datos o volver atrás
                cargarDatosCliente(view)
            } else {
                Toast.makeText(requireContext(), "Error al convertir en socio", Toast.LENGTH_SHORT).show()
            }
        }


        // Boton de Dar de Baja como Socio
        val btnDarDeBaja = view.findViewById<Button>(R.id.btnDarDeBaja)
        btnDarDeBaja.setOnClickListener {
            val fueExitoso = dbHelper.darDeBajaSocio(clienteId)
            if (fueExitoso) {
                Toast.makeText(requireContext(), "Sos No Socio ahora", Toast.LENGTH_SHORT).show()
                // Podés recargar datos o volver atrás
                cargarDatosCliente(view)
            } else {
                Toast.makeText(requireContext(), "Error al dar de baja", Toast.LENGTH_SHORT).show()
            }
        }


        // Boton Ver Carnet de Socio
        val btnVerCarnet = view.findViewById<Button>(R.id.btnVerCarnet)

        btnVerCarnet.setOnClickListener {
            val intent = Intent(requireContext(), CarnetActivity::class.java)
            intent.putExtra("clienteId", clienteId) // pasás el ID del socio
            startActivity(intent)
        }

    }



    private fun cargarTipoSocio(view: View) {
        val dbHelper = ClubDBHelper(requireContext())
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT FechaVencimientoCuota FROM Socio WHERE ClienteID = ?",
            arrayOf(clienteId.toString())
        )

        val esSocio = cursor.moveToFirst()
        val fechaVenc = if (esSocio) cursor.getString(0) else null
        cursor.close()

        if (esSocio) {
            mostrarSocioUI(view, fechaVenc)
        } else {
            mostrarNoSocioUI(view)
        }
    }

    private fun mostrarSocioUI(view: View, fechaVenc: String?) = with(view) {
        findViewById<TextView>(R.id.textTipoCliente).text = "Socio"
        findViewById<TextView>(R.id.LabelProxVenc).visibility = View.VISIBLE
        findViewById<TextView>(R.id.textProxVenc).visibility = View.VISIBLE
        findViewById<Button>(R.id.btnDarDeBaja).visibility = View.VISIBLE
        findViewById<Button>(R.id.btnQuieroSerSocio).visibility = View.GONE
        findViewById<Button>(R.id.btnIrAPagarNoSocios).visibility = View.GONE
        findViewById<Button>(R.id.btnIrAPagarSocios).visibility = View.VISIBLE

        val textProxVenc = findViewById<TextView>(R.id.textProxVenc)
        val btnDarDeBaja = findViewById<Button>(R.id.btnDarDeBaja)
        val btnVerCarnet = findViewById<Button>(R.id.btnVerCarnet)

        if (fechaVenc.isNullOrBlank()) {
            textProxVenc.text = "ADEUDA CUOTA"
            btnVerCarnet.visibility = View.GONE
        } else {
            btnVerCarnet.visibility = View.VISIBLE

            val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            try {
                val fecha = formato.parse(fechaVenc)
                val hoy = Date()
                val vencida = fecha != null && fecha.before(hoy)

                textProxVenc.text = if (vencida) "$fechaVenc (vencida)" else fechaVenc
                btnDarDeBaja.visibility = if (vencida) View.VISIBLE else View.GONE
            } catch (e: Exception) {
                textProxVenc.text = "Fecha inválida"
                btnDarDeBaja.visibility = View.VISIBLE
            }
        }
    }


    private fun mostrarNoSocioUI(view: View) = with(view) {
        findViewById<TextView>(R.id.textTipoCliente).text = "No Socio"
        findViewById<TextView>(R.id.LabelProxVenc).visibility = View.GONE
        findViewById<TextView>(R.id.textProxVenc).visibility = View.GONE
        findViewById<Button>(R.id.btnQuieroSerSocio).visibility = View.VISIBLE
        findViewById<Button>(R.id.btnIrAPagarNoSocios).visibility = View.VISIBLE
        findViewById<Button>(R.id.btnIrAPagarSocios).visibility = View.GONE
        findViewById<Button>(R.id.btnDarDeBaja).visibility = View.GONE
        findViewById<Button>(R.id.btnVerCarnet).visibility = View.GONE
    }
}