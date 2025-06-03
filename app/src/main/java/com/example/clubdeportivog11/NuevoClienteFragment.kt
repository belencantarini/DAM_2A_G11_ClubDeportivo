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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nuevo_cliente, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Configurar spinner de tipos de documento
        val spinner: Spinner = view.findViewById(R.id.spinnerTipoDocumento)
        val tiposDocumento = listOf("DNI", "Pasaporte", "Cédula")
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            tiposDocumento
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter


        // REGISTRAR CLIENTE
        fun nuevoCliente(){
            val nombre = view.findViewById<EditText>(R.id.etNombre).text.toString()
            val apellido = view.findViewById<EditText>(R.id.etApellido).text.toString()
            val tipoDoc = view.findViewById<Spinner>(R.id.spinnerTipoDocumento).selectedItem.toString()
            val numeroDocStr  = view.findViewById<EditText>(R.id.etNumeroDocumento).text.toString()
            val aptoFisico = view.findViewById<CheckBox>(R.id.checkAptoFisico).isChecked
            val radioGroup = view.findViewById<RadioGroup>(R.id.radioTipoCliente)
            val tipoCliente = when (radioGroup.checkedRadioButtonId) {
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
            val exito = dbHelper.registrarCliente(
                nombre,
                apellido,
                tipoDoc,
                numeroDoc,
                fechaHoy,
                aptoFisico,
                tipoCliente
            )

            if (exito) {
                Toast.makeText(requireContext(), "Cliente registrado correctamente", Toast.LENGTH_SHORT).show()
                // Opcional: limpiar campos o navegar a otra pantalla
            } else {
                Toast.makeText(requireContext(), "Error al registrar cliente", Toast.LENGTH_SHORT).show()
            }
        }


        // (Opcional) Botón Registrar Cliente
        val btnRegistrarCliente = view.findViewById<Button>(R.id.btnRegistrarCliente)
        btnRegistrarCliente.setOnClickListener {
            nuevoCliente()
        }

        // Botón Volver
        val btnVolver = view.findViewById<Button>(R.id.btnVolverRC)
        btnVolver.setOnClickListener {
            // Cerrar fragmento regresando
            activity?.onBackPressedDispatcher
        }
    }
}