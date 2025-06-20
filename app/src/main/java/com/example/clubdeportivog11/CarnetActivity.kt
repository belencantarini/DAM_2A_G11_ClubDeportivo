package com.example.clubdeportivog11

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class CarnetActivity : AppCompatActivity() {

    private lateinit var btnVolver: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carnet)

        // Volver
        btnVolver = findViewById(R.id.btnVolverCarnet)
        btnVolver.setOnClickListener {
            finish()
        }

        // Obtener el ID del cliente desde el Intent
        val clienteId = intent.getLongExtra("clienteId", -1)

        if (clienteId != -1L) {
            mostrarCarnet(clienteId)
        } else {
            Toast.makeText(this, "ID de cliente no recibido", Toast.LENGTH_SHORT).show()
        }
    }

    private fun mostrarCarnet(clienteId: Long) {
        val dbHelper = ClubDBHelper(this)
        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            """
            SELECT c.Nombre, c.Apellido, c.TipoDocumento, c.NumeroDocumento, c.FechaInscripcion, s.FechaVencimientoCuota 
            FROM Cliente c 
            LEFT JOIN Socio s ON c.ClienteID = s.ClienteID 
            WHERE c.ClienteID = ?
            """, arrayOf(clienteId.toString())
        )

        if (cursor.moveToFirst()) {
            val nombre = cursor.getString(0)
            val apellido = cursor.getString(1)
            val tipoDoc = cursor.getString(2)
            val dni = cursor.getString(3)
            val fechaInicio = cursor.getString(4)
            val fechaVenc = cursor.getString(5)

            findViewById<TextView>(R.id.textNombreCarnet).text = "Nombre: $nombre"
            findViewById<TextView>(R.id.textApellidoCarnet).text = "Apellido: $apellido"
            findViewById<TextView>(R.id.textDniCarnet).text = "$tipoDoc: $dni"
            findViewById<TextView>(R.id.textFechaInicioCarnet).text = "Fecha de Inscripción: $fechaInicio"
            findViewById<TextView>(R.id.textFechaVencCarnet).text = "Fecha de Vencimiento: $fechaVenc"
            val estadoTextView = findViewById<TextView>(R.id.textEstadoCarnet)

            // Validar vencimiento
            val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            try {
                val fechaVencDate = formato.parse(fechaVenc)
                val hoy = Date()

                if (fechaVencDate != null && fechaVencDate.before(hoy)) {
                    estadoTextView.visibility = View.VISIBLE
                    Toast.makeText(this, "⚠️ Carnet VENCIDO", Toast.LENGTH_LONG).show()
                } else {
                    estadoTextView.visibility = View.GONE
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Error al validar fecha", Toast.LENGTH_SHORT).show()
            }

        } else {
            Toast.makeText(this, "Socio no encontrado", Toast.LENGTH_SHORT).show()
        }

        cursor.close()
        db.close()
    }
}