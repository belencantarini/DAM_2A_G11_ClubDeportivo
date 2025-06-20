package com.example.clubdeportivog11.adapters
import com.example.clubdeportivog11.models.ActividadesDataClass
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.clubdeportivog11.R
import com.example.clubdeportivog11.models.PlanesDataClass


class ActividadesAdapterCheckBox(
    private val actividades: List<ActividadesDataClass>,
    private val onSeleccionado: (List<ActividadesDataClass>) -> Unit
) : RecyclerView.Adapter<ActividadesAdapterCheckBox.ActividadViewHolder>() {

    inner class ActividadViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkBox: CheckBox = itemView.findViewById(R.id.checkbox)
        val tvNombre: TextView = view.findViewById(R.id.tvNombre)
        val tvPrecio: TextView = view.findViewById(R.id.tvPrecio)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActividadViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_actividad_checkbox, parent, false)
        return ActividadViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActividadViewHolder, position: Int) {
        val actividad = actividades[position]
        holder.tvNombre.text = actividad.nombre
        holder.tvPrecio.text = String.format("$%.2f", actividad.precio)

        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.isChecked = actividad.seleccionado

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            actividad.seleccionado = isChecked
            val seleccionadas = actividades.filter { it.seleccionado }
            onSeleccionado(seleccionadas)
        }
    }

    override fun getItemCount() = actividades.size
}