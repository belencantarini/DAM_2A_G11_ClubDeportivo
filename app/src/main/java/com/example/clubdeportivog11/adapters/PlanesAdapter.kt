package com.example.clubdeportivog11.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.clubdeportivog11.R
import com.example.clubdeportivog11.models.PlanesDataClass


class PlanesAdapter(private val planes: List<PlanesDataClass>) :
    RecyclerView.Adapter<PlanesAdapter.PlanViewHolder>() {


        // En mi clase interna de Plan View Holder lo que hago es tomar id de los text view
        // de mis items creados para mostrar datos (item_plan_actividad).
        // Luego vemos en la linea 13 que al adapter le pasamos el viewholder que creamos
        // recien dentro de la clase adapter: PlanesAdapter.PlanViewHolder

        inner class PlanViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvNombre: TextView = view.findViewById(R.id.tvNombre)
            val tvPrecio: TextView = view.findViewById(R.id.tvPrecio)

        }

        // El onCreateViewHolder es el que se encarga de inflar los items necesarios que entran
        // en mi vista, si me entran 8 items se llama a esta funcion 8 veces
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_plan_actividad, parent, false)
            return PlanViewHolder(view)
        }


        // Luego con el onBind relleno los text view de mi item que identifique previamente en el
        // View Holder y le pongo el texto que yo misma estoy asignando (desde la base de datos)
        // en base a la posicion en la que estamos (por ejemplo si los datos terminan siendo
        // scrolleables, me va a asignar los items segun la posicion en la que estoy

        override fun onBindViewHolder(holder: PlanViewHolder, position: Int) {
            val plan = planes[position]
            holder.tvNombre.text = plan.nombre
            holder.tvPrecio.text = String.format("$%.2f", plan.precio)
        }

        override fun getItemCount() = planes.size
    }