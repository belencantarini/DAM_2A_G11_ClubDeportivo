package com.example.clubdeportivog11

import android.content.ContentValues
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale



class NuevoClienteFragment : Fragment() {

    private lateinit var etNombre: EditText
    private lateinit var etApellido: EditText
    private lateinit var spinnerTipoDoc: Spinner
    private lateinit var etNumeroDoc: EditText
    private lateinit var checkAptoFisico: CheckBox
    private lateinit var radioGroupTipo: RadioGroup
    private lateinit var btnRegistrar: Button
    private lateinit var btnVolver: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nuevo_cliente, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Configuramos todas las variables
        etNombre = view.findViewById(R.id.etNombre)
        etApellido = view.findViewById(R.id.etApellido)
        spinnerTipoDoc = view.findViewById(R.id.spinnerTipoDocumento)
        etNumeroDoc = view.findViewById(R.id.etNumeroDocumento)
        checkAptoFisico= view.findViewById(R.id.checkAptoFisico)
        radioGroupTipo = view.findViewById(R.id.radioTipoCliente)
        btnRegistrar = view.findViewById(R.id.btnRegistrarCliente)
        btnVolver = view.findViewById(R.id.btnVolverRC)



        // Configurar spinner de tipos de documento
        val tiposDocumento = listOf("DNI", "Pasaporte", "Cédula")
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            tiposDocumento
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipoDoc.adapter = adapter


        // REGISTRAR CLIENTE
        fun nuevoCliente(){
            val nombre = etNombre.text.toString()
            val apellido = etApellido.text.toString()
            val tipoDoc = spinnerTipoDoc.selectedItem.toString()
            val numeroDocStr  = etNumeroDoc.text.toString()
            val aptoFisico = checkAptoFisico.isChecked
            val tipoCliente = when (radioGroupTipo.checkedRadioButtonId) {
                R.id.radioSocio -> true
                R.id.radioNoSocio -> false
                else -> null
            }
            val fechaHoy = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            // Valido que se completen todos los datos
            if (
                nombre.isBlank() ||
                apellido.isBlank() ||
                tipoCliente == null ||
                tipoDoc == "Seleccionar..." || // Evitá valores de spinner no válidos
                numeroDocStr.isBlank() || !numeroDocStr.all { it.isDigit() }
            ) {
                Toast.makeText(requireContext(), "Por favor, completá todos los campos correctamente", Toast.LENGTH_SHORT).show()
                return
            }

            val numeroDoc = numeroDocStr.toInt()


            // Conecto la base de datos
            val dbHelper = ClubDBHelper(requireContext())

            // Insertar en Cliente
            // Aquí llamás a tu función del ClubDBHelper para insertar:
            val newId = dbHelper.registrarCliente(
                nombre,
                apellido,
                tipoDoc,
                numeroDoc,
                fechaHoy,
                aptoFisico,
                tipoCliente
            )

            if (newId == -1L) {
                Toast.makeText(requireContext(),
                    "Error al registrar cliente, verifique que no exista un cliente ya registrado bajo ese tipo y número de documento.",
                    Toast.LENGTH_LONG).show()
            } else {

                Toast.makeText(requireContext(), "Nuevo cliente registrado correctamente.", Toast.LENGTH_SHORT).show()


                // Guardo el argumento del cliente Id en el Fragmento PerfilClienteFragment
                val verClientePerfil = PerfilClienteFragment().apply {
                    arguments = Bundle().apply {
                        putLong("clienteId", newId)
                    }
                }

                // Le paso el fragmento con los argumentos al FragmentManager con la funcion irASeccion de mi MainActivity
                (activity as? MainActivity)?.irASeccion(verClientePerfil)

            }
        }


        // (Opcional) Botón Registrar Cliente
        btnRegistrar.setOnClickListener {
            nuevoCliente()
        }

        // Botón Volver
        btnVolver.setOnClickListener {
            // Cierro el fragmento y vuelvo a la pila que genero al punto de fragmentoInicio
            parentFragmentManager.popBackStack("fragmentoInicio", 0)
            (activity as? MainActivity)?.cambiarFondo(R.drawable.bg_bolsas)
        }
    }

    override fun onResume() {
        super.onResume()
        // Limpiar campos
        etNombre.text?.clear()
        etApellido.text?.clear()
        spinnerTipoDoc.setSelection(0)
        etNumeroDoc.text?.clear()
        checkAptoFisico.isChecked = false
        radioGroupTipo.clearCheck()
    }
}