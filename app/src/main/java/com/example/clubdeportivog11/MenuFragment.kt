package com.example.clubdeportivog11

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment

class MenuFragment : Fragment(R.layout.fragment_menu) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Cerrar menú al hacer click fuera de menuCard
        view.setOnClickListener {
            (activity as? MainActivity)?.closeMenu()
        }

        // Cerrar menú al hacer click en el icono de cierre
        val menuButton = view.findViewById<LinearLayout>(R.id.btnCerrarMenu)
        menuButton.setOnClickListener {
            (activity as? MainActivity)?.closeMenu()
        }

        // Prevenir cierre al hacer click dentro de menuCard
        view.findViewById<ConstraintLayout>(R.id.menuCard).isClickable = true


        // Navego por todas las secciones de mi menu
        view.findViewById<LinearLayout>(R.id.btnNuevosClientes).setOnClickListener {
            (activity as? MainActivity)?.irASeccion(R.drawable.bg_pelotas, NuevoClienteFragment())
        }
        view.findViewById<LinearLayout>(R.id.btnListadoClientes).setOnClickListener {
            (activity as? MainActivity)?.irASeccion(R.drawable.bg_tenis, ClientesFragment())
        }
        view.findViewById<LinearLayout>(R.id.btnRegistraPagos).setOnClickListener {
            (activity as? MainActivity)?.irASeccion(R.drawable.bg_futbol, PagosFragment())
        }
        view.findViewById<LinearLayout>(R.id.btnListadoPlanes).setOnClickListener {
            (activity as? MainActivity)?.irASeccion(R.drawable.bg_banderin, PlanesFragment())
        }
        view.findViewById<LinearLayout>(R.id.btnCuotasVencidas).setOnClickListener {
            (activity as? MainActivity)?.irASeccion(R.drawable.bg_carrera, VencimientosFragment())
        }
        view.findViewById<LinearLayout>(R.id.btnCerrarSesion).setOnClickListener {
            activity?.finish()
        }
    }
}

