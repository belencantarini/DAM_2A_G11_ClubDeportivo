package com.example.clubdeportivog11

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.LinearLayout

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnNuevosClientes = findViewById<LinearLayout>(R.id.btnNuevosClientes)
        val btnListadoClientes = findViewById<LinearLayout>(R.id.btnListadoClientes)
        val btnRegistraPagos = findViewById<LinearLayout>(R.id.btnRegistraPagos)
        val btnListadoPlanes = findViewById<LinearLayout>(R.id.btnListadoPlanes)
        val btnCuotasVencidas = findViewById<LinearLayout>(R.id.btnCuotasVencidas)
        val btnLogout = findViewById<LinearLayout>(R.id.btnCerrarSesion)

        btnNuevosClientes.setOnClickListener() {
            val intent = Intent(this, NewClientActivity::class.java);
            startActivity(intent);
        }

        btnListadoClientes.setOnClickListener() {
            val intent = Intent(this, ClientListActivity::class.java);
            startActivity(intent);
        }

        btnRegistraPagos.setOnClickListener() {
            val intent = Intent(this, PaymentsActivity::class.java);
            startActivity(intent);
        }

        btnListadoPlanes.setOnClickListener() {
            val intent = Intent(this, MembershipsActivity::class.java);
            startActivity(intent);
        }

        btnCuotasVencidas.setOnClickListener() {
            val intent = Intent(this, UnpaidFeesActivity::class.java);
            startActivity(intent);
        }

        btnLogout.setOnClickListener() {
            val intent = Intent(this, MainActivity::class.java);
            startActivity(intent);
        }
    }
}