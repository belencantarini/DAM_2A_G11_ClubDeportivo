package com.example.clubdeportivog11

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import java.text.Normalizer
import java.util.Locale


// Configuramos una funcion para ignorar acentos que funcionara en toda la base
fun String.normalizar(): String {
    val normalizada = Normalizer.normalize(this, Normalizer.Form.NFD)
    return normalizada.replace("[\\p{InCombiningDiacriticalMarks}]".toRegex(), "")
        .lowercase(Locale.getDefault())
}

class MainActivity : AppCompatActivity() {

    private var menuAbierto = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val menuButton = findViewById<ImageView>(R.id.btnHamburguesa)
        val menuOverlayContainer = findViewById<FrameLayout>(R.id.menuOverlayContainer)

        // Seteo mi menu en el menuOverlayContainer y luego lo dejo oculto
        supportFragmentManager.beginTransaction()
            .add(R.id.menuOverlayContainer, MenuFragment())
            .commit()
        menuOverlayContainer.visibility = View.GONE

        // Mostrar fragmento con su fondo por defecto
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, InicioFragment())
            .addToBackStack("fragmentoInicio") //Creo una pila de fragmentos
            .commit()

        menuButton.setOnClickListener {
            toggleMenu()
        }
    }



     fun toggleMenu() {
        val menuOverlayContainer = findViewById<FrameLayout>(R.id.menuOverlayContainer)
        val menuButton = findViewById<ImageView>(R.id.btnHamburguesa)
        if (menuAbierto) {
            // Cerrar menú
            menuOverlayContainer.visibility = View.GONE
            menuButton.setImageResource(R.drawable.icon_btn_menu)
            menuAbierto = false
        } else {
            // Abrir menú
            menuOverlayContainer.visibility = View.VISIBLE
            menuButton.setImageResource(R.drawable.icon_btn_close) // ícono de cerrar
            menuAbierto = true
        }
    }


    fun closeMenu() {
        if (menuAbierto) {
            val menuOverlayContainer = findViewById<FrameLayout>(R.id.menuOverlayContainer)
            val menuButton = findViewById<ImageView>(R.id.btnHamburguesa)
            menuOverlayContainer.visibility = View.GONE
            menuButton.setImageResource(R.drawable.icon_btn_menu)
            menuAbierto = false
        }
    }


    fun cambiarFondo(resourceId: Int) {
        val bgImage = findViewById<ImageView>(R.id.bgImage)
        bgImage.setImageResource(resourceId)
    }


    fun irASeccion(idFondo: Int, fragmento: Fragment) {
        cambiarFondo(idFondo)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragmento)
            .addToBackStack(null)
            .commit()

        // Además de ir a la sección, cerramos el menú si está abierto
        closeMenu()
    }


    fun irASeccion(fragmento: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragmento)
            .addToBackStack(null)
            .commit()
    }


}
