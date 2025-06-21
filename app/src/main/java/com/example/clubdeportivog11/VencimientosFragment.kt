package com.example.clubdeportivog11

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clubdeportivog11.adapters.ClientesAdapter
import com.example.clubdeportivog11.models.BotonesCardsDataClass
import com.example.clubdeportivog11.models.ClientesDataClass
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale


class VencimientosFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_vencimientos, container, false)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configuramos los botones de mis cards de clientes (solo muestro boton ver)
        val configBotones = BotonesCardsDataClass(mostrarIrAPagar = true, mostrarDarDeBaja = true)

        // Conectamos con la base de datos
        val dbHelper = ClubDBHelper(requireContext())


        // Solicitamos los datos de los clientes a la base de datos
        val listaClientesCuotaVencida = dbHelper.obtenerClientes(soloCuotaVencida = true)

        // Configuramos la RECICLER VIEW para los clientes
        val clientesVencimientosReciclerView = view.findViewById<RecyclerView>(R.id.recyclerViewClientesVencimientos)

        fun mostrarClientes(lista: List<ClientesDataClass>, configBotones: BotonesCardsDataClass, recyclerView: RecyclerView) {
            clientesVencimientosReciclerView.layoutManager = LinearLayoutManager(requireContext())
            clientesVencimientosReciclerView.adapter = ClientesAdapter(lista, configBotones,
                onPagarClick = { cliente ->
                    val fragmentDestino = if (cliente.esSocio) {
                        PagarMembresiasFragment()
                    } else {
                        PagarActividadesFragment()
                    }

                    fragmentDestino.arguments = Bundle().apply {
                        putLong("clienteId", cliente.clienteID)
                    }

                    (activity as? MainActivity)?.irASeccion(fragmentDestino)
                },
                onDarDeBajaClick = { cliente ->
                    val fueExitoso = dbHelper.darDeBajaSocio(cliente.clienteID)
                    if (fueExitoso) {
                        Toast.makeText(requireContext(), "Sos No Socio ahora", Toast.LENGTH_SHORT).show()
                        // Recargamos la lista actualizada
                        val listaActualizada = dbHelper.obtenerClientes(soloCuotaVencida = true)
                        mostrarClientes(listaActualizada, configBotones, clientesVencimientosReciclerView)
                    } else {
                        Toast.makeText(requireContext(), "Error al dar de baja", Toast.LENGTH_SHORT).show()
                    }
                })
        }

        mostrarClientes(listaClientesCuotaVencida, configBotones, clientesVencimientosReciclerView)


        // Botón Volver
        val btnVolver = view.findViewById<Button>(R.id.btnVolverVenc)
        btnVolver.setOnClickListener {
            // Cierro el fragmento y vuelvo a la pila que genero al punto de fragmentoInicio
            parentFragmentManager.popBackStack("fragmentoInicio", 0)
            (activity as? MainActivity)?.cambiarFondo(R.drawable.bg_bolsas)
        }
    }
}