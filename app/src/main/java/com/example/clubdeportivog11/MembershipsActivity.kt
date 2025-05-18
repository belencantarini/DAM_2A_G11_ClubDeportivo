package com.example.clubdeportivog11

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MembershipsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_memberships)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnVolver = findViewById<Button>(R.id.btnVolverM)

        btnVolver.setOnClickListener() {
            val intent = Intent(this, MenuActivity::class.java);
            startActivity(intent);
        }


        val recuadroPlanes = findViewById<LinearLayout>(R.id.verPlanes)
        val recuadroActividades = findViewById<LinearLayout>(R.id.verActividades)

        val btnVerPlanes = findViewById<LinearLayout>(R.id.btnVerPlanes)

        btnVerPlanes.setOnClickListener() {
            recuadroPlanes.visibility = View.VISIBLE
            recuadroActividades.visibility = View.GONE
        }

        val btnVerActividades = findViewById<LinearLayout>(R.id.btnVerActividades)

        btnVerActividades.setOnClickListener() {
            recuadroActividades.visibility = View.VISIBLE
            recuadroPlanes.visibility = View.GONE
        }

        val btnCerrarPlanes = findViewById<Button>(R.id.btnCerrarPlanes)

        btnCerrarPlanes.setOnClickListener() {

            recuadroPlanes.visibility = View.GONE
        }

        val btnCerrarActividades = findViewById<Button>(R.id.btnCerrarActividades)

        btnCerrarActividades.setOnClickListener() {
            recuadroActividades.visibility = View.GONE
        }

    }
}