package com.example.clubdeportivog11
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.clubdeportivog11.models.PlanesDataClass
import com.example.clubdeportivog11.adapters.PlanesAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clubdeportivog11.adapters.ActividadesAdapter
import com.example.clubdeportivog11.models.ActividadesDataClass


class PlanesFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_planes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnPlanes = view.findViewById<LinearLayout>(R.id.btnVerPlanes)
        val verPlanes = view.findViewById<LinearLayout>(R.id.verPlanes)
        val btnActividades = view.findViewById<LinearLayout>(R.id.btnVerActividades)
        val verActividades = view.findViewById<LinearLayout>(R.id.verActividades)
        val cerrarPlanes = view.findViewById<Button>(R.id.btnCerrarPlanes)
        val cerrarActividades = view.findViewById<Button>(R.id.btnCerrarActividades)
        val menuPlanesyActividades = view.findViewById<LinearLayout>(R.id.menuPlanesyActividades)

        btnPlanes.setOnClickListener {
            verPlanes.visibility = View.VISIBLE
            verActividades.visibility = View.GONE
            menuPlanesyActividades.gravity = Gravity.CENTER_HORIZONTAL
        }

        btnActividades.setOnClickListener {
            verActividades.visibility = View.VISIBLE
            verPlanes.visibility = View.GONE
            menuPlanesyActividades.gravity = Gravity.CENTER_HORIZONTAL
        }

        cerrarPlanes.setOnClickListener {
            verPlanes.visibility = View.GONE
            menuPlanesyActividades.gravity = Gravity.CENTER
        }

        cerrarActividades.setOnClickListener {
            verActividades.visibility = View.GONE
            menuPlanesyActividades.gravity = Gravity.CENTER
        }


        // Conectamos con la base de datos
        val dbHelper = ClubDBHelper(requireContext())

        // Solicitamos los datos de los planes y las actividades a la base de datos
        val listaPlanesSocios = dbHelper.obtenerPlanes()
        val listaActividadesNoSocios = dbHelper.obtenerActividades()

        // Configuramos la RECICLER VIEW para PLANES y ACTIVIDADES

        val planesReciclerView = view.findViewById<RecyclerView>(R.id.recyclerViewPlanes)
        planesReciclerView.layoutManager = LinearLayoutManager(requireContext())
        planesReciclerView.adapter = PlanesAdapter(listaPlanesSocios)

        val actividadesReciclerView = view.findViewById<RecyclerView>(R.id.recyclerViewActividades)
        actividadesReciclerView.layoutManager = LinearLayoutManager(requireContext())
        actividadesReciclerView.adapter = ActividadesAdapter(listaActividadesNoSocios)

        // Botón Volver
        val btnVolver = view.findViewById<Button>(R.id.btnVolverPlanes)
        btnVolver.setOnClickListener {
            // Cierro el fragmento y vuelvo a la pila que genero al punto de fragmentoInicio
            parentFragmentManager.popBackStack("fragmentoInicio", 0)
            (activity as? MainActivity)?.cambiarFondo(R.drawable.bg_bolsas)
        }



    }


}

