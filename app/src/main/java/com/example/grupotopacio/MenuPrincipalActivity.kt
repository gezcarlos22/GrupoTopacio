package com.example.grupotopacio

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.appcompat.app.AlertDialog

class MenuPrincipalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu_principal)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnRegistroPago = findViewById<LinearLayout>(R.id.btnRegistroPago)
        val btnRegistroCliente = findViewById<LinearLayout>(R.id.btnRegistroCliente)
        val btnCerrarSesion = findViewById<Button>(R.id.btnCerrarSeccion)

        btnRegistroPago.setOnClickListener {
            val intent = Intent(this, RegistrarPagoActivity::class.java)
            startActivity(intent)
        }

        btnRegistroCliente.setOnClickListener {
            val intent = Intent(this, RegistrarSocioActivity::class.java)
            startActivity(intent)
        }

        btnCerrarSesion.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Desea cerrar la sesión?")
                .setPositiveButton("Sí") { _, _ ->
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("No", null)
                .show()
        }

        val btnVerVencimiento = findViewById<LinearLayout>(R.id.btnVerVencimiento)

        btnVerVencimiento.setOnClickListener {
            val intent = Intent(this, VerVencimientosActivity::class.java)
            startActivity(intent)
        }

        val btnEmitirCarnet = findViewById<LinearLayout>(R.id.btnEmitirCarnet)

        btnEmitirCarnet.setOnClickListener {
            val intent = Intent(this, EmitirCarnetActivity::class.java)
            startActivity(intent)
        }
    }
}
