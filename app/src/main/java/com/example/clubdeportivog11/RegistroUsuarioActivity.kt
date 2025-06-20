package com.example.clubdeportivog11

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class RegistroUsuarioActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro_usuario) // Asegurate de tener este layout creado

        val spinnerTipoDoc = findViewById<Spinner>(R.id.spinnerTipoDoc)
        val editNumeroDoc = findViewById<EditText>(R.id.editNumeroDoc)
        val editNombreUsuario = findViewById<EditText>(R.id.editNombreUsuario)
        val editPassword = findViewById<EditText>(R.id.editPassword)
        val editRepetirPassword = findViewById<EditText>(R.id.editRepetirPassword)
        val checkMostrarPassword = findViewById<CheckBox>(R.id.chbMostrarPassword)
        val btnRegistrarUsuario = findViewById<Button>(R.id.btnCrearUsuario)
        val btnVolverRegistrarUsuario = findViewById<Button>(R.id.btnVolverCrearUsuario)

        // Configurar spinner de tipos de documento
        val tiposDocumento = listOf("DNI", "Pasaporte", "Cédula")
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            tiposDocumento
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipoDoc.adapter = adapter

        // Mostrar / ocultar contraseña
        checkMostrarPassword.setOnCheckedChangeListener { _, isChecked ->
            val tipo = if (isChecked)
                android.text.method.HideReturnsTransformationMethod.getInstance()
            else
                android.text.method.PasswordTransformationMethod.getInstance()

            editPassword.transformationMethod = tipo
            editRepetirPassword.transformationMethod = tipo
        }

        btnRegistrarUsuario.setOnClickListener {
            val tipoDoc = spinnerTipoDoc.selectedItem.toString()
            val numeroDoc = editNumeroDoc.text.toString().toIntOrNull()
            val nombreUsuario = editNombreUsuario.text.toString().trim()
            val pass = editPassword.text.toString()
            val repetirPass = editRepetirPassword.text.toString()

            if (numeroDoc == null || nombreUsuario.isEmpty() || pass.isEmpty() || repetirPass.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (pass != repetirPass) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val dbHelper = ClubDBHelper(this)
            val db = dbHelper.readableDatabase

            // Verificar si existe un cliente con esos datos
            val cursor = db.rawQuery(
                """
                SELECT Cliente.ClienteID 
                FROM Cliente 
                JOIN Socio ON Cliente.ClienteID = Socio.ClienteID
                WHERE TipoDocumento = ? AND NumeroDocumento = ?
                """.trimIndent(),
                arrayOf(tipoDoc, numeroDoc.toString())
            )

            if (cursor.moveToFirst()) {
                val clienteId = cursor.getLong(0)
                cursor.close()

                val cursor2 = db.rawQuery(
                    "SELECT * FROM Usuario WHERE ClienteID = ?",
                    arrayOf(clienteId.toString())
                )

                if (cursor2.moveToFirst()) {
                    cursor2.close()
                    Toast.makeText(this, "Ya existe un usuario para este cliente", Toast.LENGTH_SHORT).show()
                } else {
                    cursor2.close()
                    val exito = dbHelper.crearUsuarioCliente(nombreUsuario, pass, clienteId)
                    if (exito) {
                        Toast.makeText(this, "Usuario creado correctamente", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this, CarnetActivity::class.java)
                        intent.putExtra("clienteId", clienteId)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Error al crear usuario", Toast.LENGTH_SHORT).show()
                    }
                }

            } else {
                cursor.close()
                Toast.makeText(this, "No se encontró un socio con esos datos", Toast.LENGTH_SHORT).show()
            }
        }

        btnVolverRegistrarUsuario.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Opcional: para que no se pueda volver a esta pantalla con "Atrás"
        }
    }
}