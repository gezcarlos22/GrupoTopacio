package com.example.grupotopacio

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RegistrarPagoActivity : AppCompatActivity() {

    private lateinit var dbHelper: ClubDatabaseHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var sociosAdapter: SociosAdapter
    private var selectedSocioId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registrar_pago)

        dbHelper = ClubDatabaseHelper(this)
        recyclerView = findViewById(R.id.recycler_view_clientes)
        recyclerView.layoutManager = LinearLayoutManager(this)


        sociosAdapter = SociosAdapter(null) { id, nombre ->

            selectedSocioId = id
            Toast.makeText(this, "Socio seleccionado: $nombre", Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = sociosAdapter

        cargarSocios()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnVolverAtras = findViewById<LinearLayout>(R.id.btnVolverAtras)
        btnVolverAtras.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }


        val tiposDeCobro = resources.getStringArray(R.array.tipo_cobro_opciones)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, tiposDeCobro)
        val autoCompleteTextView = findViewById<AutoCompleteTextView>(R.id.text_seleccionar_cliente)
        autoCompleteTextView.setAdapter(adapter)


        val btnContinuar = findViewById<Button>(R.id.btnContinuar)
        btnContinuar.setOnClickListener {
            if (selectedSocioId == -1L) {
                Toast.makeText(this, "Por favor, seleccione un cliente.", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, SeleccionarCuotaActivity::class.java)
                intent.putExtra("SOCIO_ID", selectedSocioId)
                startActivity(intent)
            }
        }
    }

    private fun cargarSocios() {
        val cursor = dbHelper.getSocios()
        sociosAdapter.swapCursor(cursor)
    }

    override fun onResume() {
        super.onResume()

        cargarSocios()
    }
}