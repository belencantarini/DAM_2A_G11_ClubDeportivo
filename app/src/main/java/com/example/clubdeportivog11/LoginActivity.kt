package com.example.clubdeportivog11

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class LoginActivity : AppCompatActivity() {

    private lateinit var dbHelper: ClubDBHelper
    private lateinit var txtUser: EditText
    private lateinit var txtPass: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnRegistrar: Button
    private lateinit var chbPass: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        dbHelper = ClubDBHelper(this)

        txtUser = findViewById<EditText>(R.id.lgnUser)
        txtPass = findViewById<EditText>(R.id.lgnPassword)
        btnLogin = findViewById<Button>(R.id.btnLogin)
        btnRegistrar = findViewById<Button>(R.id.btnRegistrarse)

        btnLogin.setOnClickListener {
            val u = txtUser.text.toString().trim()
            val p = txtPass.text.toString().trim()

            val datosUsuario = dbHelper.obtenerDatosUsuario(u, p)

            if (datosUsuario != null) {
                val rol = datosUsuario.first
                val clienteId = datosUsuario.second

                Toast.makeText(this, "Bienvenido $u", Toast.LENGTH_SHORT).show()

                when (rol) {
                    1 -> { // Administrador
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                    2 -> { // Cliente
                        if (clienteId != null) {
                            val intent = Intent(this, CarnetActivity::class.java)
                            intent.putExtra("clienteId", clienteId)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, "Error: Cliente no válido", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else -> {
                        Toast.makeText(this, "Rol desconocido", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            }
        }

        btnRegistrar.setOnClickListener(){
            val intent = Intent(this, RegistroUsuarioActivity::class.java)
            startActivity(intent)
        }

        chbPass = findViewById<CheckBox>(R.id.chbPassword)
        chbPass.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Mostrar contraseña
                txtPass.transformationMethod = null
            } else {
                // Ocultar contraseña
                txtPass.transformationMethod = PasswordTransformationMethod.getInstance()
            }
            // Mover cursor al final
            txtPass.setSelection(txtPass.text.length)
        }
        }

    override fun onResume() {
        super.onResume()
        // Limpiar campos para que al volver desde MainActivity estén vacíos
        txtUser.text?.clear()
        txtPass.text?.clear()

        // Asegurarnos de ocultar la contraseña al volver
        chbPass.isChecked = false
        txtPass.transformationMethod = PasswordTransformationMethod.getInstance()
    }
}