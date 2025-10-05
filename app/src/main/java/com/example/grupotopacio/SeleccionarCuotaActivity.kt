package com.example.grupotopacio


import android.content.Intent

import android.os.Bundle

import android.widget.Button

import androidx.activity.enableEdgeToEdge

import androidx.appcompat.app.AppCompatActivity

import androidx.core.view.ViewCompat

import androidx.core.view.WindowInsetsCompat


class SeleccionarCuotaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContentView(R.layout.activity_seleccionar_cuota)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->

            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)

            insets

        }


        val btnGenerarCuota = findViewById<Button>(R.id.btnGenerarCuota)

        btnGenerarCuota.setOnClickListener {

            mostrarDialogoGenerarCuota()

        }


        val btnRealizarPago = findViewById<Button>(R.id.btnRealizarPago)

        btnRealizarPago.setOnClickListener {

            iniciarRegistroPago()

        }

    }


    private fun mostrarDialogoGenerarCuota() {

        val dialog = GenerarCuotaDialogFragment()

        dialog.show(supportFragmentManager, "generar_cuota_dialog")

    }


    private fun iniciarRegistroPago() {

        val intent = Intent(this, RegistrarPagoFinalActivity::class.java)

        startActivity(intent)

    }

}