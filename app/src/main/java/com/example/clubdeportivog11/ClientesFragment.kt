package com.example.clubdeportivog11

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clubdeportivog11.adapters.ClientesAdapter
import com.example.clubdeportivog11.models.BotonesCardsDataClass
import com.example.clubdeportivog11.models.ClientesDataClass
import java.text.Normalizer
import java.util.Locale


class ClientesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_clientes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configuramos los botones de mis cards de clientes (solo muestro boton ver)
        val configBotones = BotonesCardsDataClass(mostrarVer = true)

        // Conectamos con la base de datos
        val dbHelper = ClubDBHelper(requireContext())


        // Solicitamos los datos de los clientes a la base de datos
        val listaClientesCompleta = dbHelper.obtenerClientes()

        // Configuramos la RECICLER VIEW para los clientes
        val clientesListaReciclerView = view.findViewById<RecyclerView>(R.id.recyclerViewClientesBusqueda)

        fun mostrarClientes(lista: List<ClientesDataClass>, configBotones: BotonesCardsDataClass, recyclerView: RecyclerView) {
            clientesListaReciclerView.layoutManager = LinearLayoutManager(requireContext())
            clientesListaReciclerView.adapter = ClientesAdapter(lista, configBotones,
                onVerClick = { cliente ->
                // Navegar a perfil del cliente
            })
        }

        mostrarClientes(listaClientesCompleta, configBotones, clientesListaReciclerView)

        // Configuramos el boton para buscar clientes por nombre, apellido o numero de documento
        view.findViewById<LinearLayout>(R.id.iconoBuscar).setOnClickListener {

            val filtroBusqueda = view.findViewById<EditText>(R.id.etBuscarCliente).text.toString()

            val listaClientesFiltrada = dbHelper.obtenerClientes(filtroBusqueda)

            mostrarClientes(listaClientesFiltrada, configBotones, clientesListaReciclerView)
        }
    }
}