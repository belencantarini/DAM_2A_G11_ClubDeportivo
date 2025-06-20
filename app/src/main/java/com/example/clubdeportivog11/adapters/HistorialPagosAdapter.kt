package com.example.clubdeportivog11.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.clubdeportivog11.R
import com.example.clubdeportivog11.models.HistorialPagosDataClass

class HistorialPagosAdapter(private val lista: List<HistorialPagosDataClass>) :
    RecyclerView.Adapter<HistorialPagosAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val fecha: TextView = view.findViewById(R.id.textFecha)
        val descripcion: TextView = view.findViewById(R.id.textDescripcion)
        val monto: TextView = view.findViewById(R.id.textMonto)
        val metodo: TextView = view.findViewById(R.id.textMetodo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pago_realizado, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pago = lista[position]
        holder.fecha.text = pago.fecha
        holder.descripcion.text = pago.descripcion
        holder.monto.text = "$${String.format("%.2f", pago.monto)}"
        holder.metodo.text = pago.metodo
    }

    override fun getItemCount() = lista.size
}