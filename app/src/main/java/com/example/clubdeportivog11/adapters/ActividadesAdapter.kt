package com.example.clubdeportivog11.adapters
import com.example.clubdeportivog11.models.ActividadesDataClass
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.clubdeportivog11.R


class ActividadesAdapter(private val actividades: List<ActividadesDataClass>) :
    RecyclerView.Adapter<ActividadesAdapter.ActividadViewHolder>() {




    inner class ActividadViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombre)
        val tvPrecio: TextView = view.findViewById(R.id.tvPrecio)

    }

    // El onCreateViewHolder es el que se encarga de inflar los items necesarios que entran
    // en mi vista, si me entran 8 items se llama a esta funcion 8 veces
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActividadViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_plan_actividad, parent, false)
        return ActividadViewHolder(view)
    }


    // Luego con el onBind relleno los text view de mi item que identifique previamente en el
    // View Holder y le pongo el texto que yo misma estoy asignando (desde la base de datos)
    // en base a la posicion en la que estamos (por ejemplo si los datos terminan siendo
    // scrolleables, me va a asignar los items segun la posicion en la que estoy

    override fun onBindViewHolder(holder: ActividadViewHolder, position: Int) {
        val actividad = actividades[position]
        holder.tvNombre.text = actividad.nombre
        holder.tvPrecio.text = String.format("$%.2f", actividad.precio)
    }

    override fun getItemCount() = actividades.size
}