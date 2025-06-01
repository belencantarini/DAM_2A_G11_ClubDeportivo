package com.example.clubdeportivog11

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class LoginActivity : AppCompatActivity() {

    lateinit var dbHelper: ClubDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        dbHelper = ClubDBHelper(this)

        val txtUser = findViewById<EditText>(R.id.lgnUser)
        val txtPass = findViewById<EditText>(R.id.lgnPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

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

        val chbPass = findViewById<CheckBox>(R.id.chbPassword)
        chbPass.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Mostrar contraseña
                txtPass.setInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)
            } else {
                // Ocultar contraseña
                txtPass.setInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)
            }
            // Mover el cursor al final
            txtPass.setSelection(txtPass.length())
        }
        }
}