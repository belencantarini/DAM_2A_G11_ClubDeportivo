package com.example.clubdeportivog11

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner

class NewClientActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_new_client)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val spinner: Spinner = findViewById(R.id.spinnerTipoDocumento)
        // Lista de tipos de documento
        val tiposDocumento = listOf("DNI", "Pasaporte", "Cédula")

        // Adaptador
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            tiposDocumento
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter


        val btnVolver = findViewById<Button>(R.id.btnVolverRC)

        btnVolver.setOnClickListener() {
            finish();
        }
    }
}