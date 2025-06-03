package com.example.clubdeportivog11.adapters
import com.example.clubdeportivog11.models.ClientesDataClass
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.clubdeportivog11.R
import com.example.clubdeportivog11.models.BotonesCardsDataClass

class ClientesAdapter (
    private val clientes: List<ClientesDataClass>,
    private val configBotones: BotonesCardsDataClass,
    private val onVerClick: (ClientesDataClass) -> Unit = {},
    private val onPagarClick: (ClientesDataClass) -> Unit = {},
    private val onDarDeBajaClick: (ClientesDataClass) -> Unit = {},
    private val onHistorialClick: (ClientesDataClass) -> Unit = {}
) : RecyclerView.Adapter<ClientesAdapter.ClientesViewHolder>() {

    // Clase interna de Clientes View Holder para asignarles variables a todos mis items de mi vista al ClientesAdapter
    inner class ClientesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txNombre: TextView = view.findViewById(R.id.textNombre)
        val txDNI: TextView = view.findViewById(R.id.textDNI)
        val txSocio: TextView = view.findViewById(R.id.textSocio)
        val btnVer = view.findViewById<Button>(R.id.btnVer)
        val btnPagar = view.findViewById<Button>(R.id.btnIrAPagar)
        val btnBaja = view.findViewById<Button>(R.id.btnDarDeBaja)
        val btnHistorial = view.findViewById<Button>(R.id.btnVerHistorial)
    }

    // Le indicamos el item layout que usa el recycler
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientesViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cliente, parent, false)
        return ClientesViewHolder(view)
    }

    // Ahora le decimos que debe mostrar el recycler
    override fun onBindViewHolder(holder: ClientesViewHolder, position: Int) {
        val cliente = clientes[position]
        holder.txNombre.text =  "${cliente.apellido} ${cliente.nombre}"
        holder.txDNI.text =  "${cliente.tipoDoc}: ${cliente.dni}"
        holder.txSocio.text = "${if (cliente.esSocio) "Socio" else "No Socio"}"
        
        // Mostrar - Ocultar botones (esto lo configuramos en el fragment correspondiente)
        holder.btnVer.visibility = if (configBotones.mostrarVer) View.VISIBLE else View.GONE
        holder.btnPagar.visibility = if (configBotones.mostrarIrAPagar) View.VISIBLE else View.GONE
        holder.btnBaja.visibility = if (configBotones.mostrarDarDeBaja) View.VISIBLE else View.GONE
        holder.btnHistorial.visibility = if (configBotones.mostrarVerHistorial) View.VISIBLE else View.GONE
        
        // Acciones de los botones
        holder.btnVer.setOnClickListener {
            onVerClick(cliente)
        }
        holder.btnPagar.setOnClickListener {
            onPagarClick(cliente)
        }
        holder.btnBaja.setOnClickListener {
            onDarDeBajaClick(cliente)
        }
        holder.btnHistorial.setOnClickListener {
            onHistorialClick(cliente)
        }
    }

    override fun getItemCount() = clientes.size

}