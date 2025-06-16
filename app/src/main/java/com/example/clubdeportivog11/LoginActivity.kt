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
    private lateinit var chbPass: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        dbHelper = ClubDBHelper(this)

        txtUser = findViewById<EditText>(R.id.lgnUser)
        txtPass = findViewById<EditText>(R.id.lgnPassword)
        btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener() {
            val u = txtUser.text.toString().trim()
            val p = txtPass.text.toString().trim()
            val intent = Intent(this, MainActivity::class.java);

            if (dbHelper.login(u, p)) {
                Toast.makeText(this, "Bienvenido $u", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            }

            //Pongo el pase al main directo para ahorrarme pasos pero deberia ir dentro del if
            startActivity(intent);
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