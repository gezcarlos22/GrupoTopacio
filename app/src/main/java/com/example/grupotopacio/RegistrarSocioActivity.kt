package com.example.grupotopacio

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegistrarSocioActivity : AppCompatActivity() {

    private lateinit var dbHelper: ClubDatabaseHelper
    private lateinit var editNombre: EditText
    private lateinit var editApellido: EditText
    private lateinit var editDni: EditText
    private lateinit var editTelefono: EditText
    private lateinit var editCorreo: EditText
    private lateinit var radioGroupTipoCliente: RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registrar_socio)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = ClubDatabaseHelper(this)

        editNombre = findViewById(R.id.edit_nombre)
        editApellido = findViewById(R.id.edit_apellido)
        editDni = findViewById(R.id.edit_dni)
        editTelefono = findViewById(R.id.edit_telefono)
        editCorreo = findViewById(R.id.edit_correo)
        radioGroupTipoCliente = findViewById(R.id.radioGroupTipoCliente)

        val btnVolverAtras = findViewById<LinearLayout>(R.id.btnVolverAtras)
        btnVolverAtras.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val btnContinuar = findViewById<Button>(R.id.btnContinuar)
        btnContinuar.setOnClickListener {
            registrarSocio()
        }
    }

    private fun registrarSocio() {
        val nombre = editNombre.text.toString().trim()
        val apellido = editApellido.text.toString().trim()
        val dni = editDni.text.toString().trim()
        val telefono = editTelefono.text.toString().trim()
        val correo = editCorreo.text.toString().trim()

        val selectedRadioId = radioGroupTipoCliente.checkedRadioButtonId
        if (nombre.isEmpty() || apellido.isEmpty() || dni.isEmpty() || selectedRadioId == -1) {
            Toast.makeText(this, "Por favor, complete los campos obligatorios.", Toast.LENGTH_SHORT).show()
            return
        }

        val tipoCliente = findViewById<RadioButton>(selectedRadioId).text.toString()


        if (dbHelper.checkDniUnico(dni)) {
            Toast.makeText(this, "ERROR: El DNI ya está registrado.", Toast.LENGTH_LONG).show()
            editDni.requestFocus()
        } else {
            val id = dbHelper.registrarSocio(nombre, apellido, dni, telefono, correo, tipoCliente)
            if (id > -1) {
                Toast.makeText(this, "Cliente registrado exitosamente.", Toast.LENGTH_LONG).show()
                finish()
            } else {
                Toast.makeText(this, "Error al registrar el cliente.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}