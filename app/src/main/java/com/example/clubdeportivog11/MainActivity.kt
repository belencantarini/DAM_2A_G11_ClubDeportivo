package com.example.clubdeportivog11

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }

        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener() {
            val intent = Intent(this, MenuActivity::class.java);
            startActivity(intent);
        }

        val chbPass = findViewById<CheckBox>(R.id.chbPassword)
        val txtPass = findViewById<EditText>(R.id.lgnPassword)


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