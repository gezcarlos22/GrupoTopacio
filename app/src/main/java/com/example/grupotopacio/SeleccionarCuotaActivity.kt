package com.example.grupotopacio

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SeleccionarCuotaActivity : AppCompatActivity() {

    private var socioId: Long = -1
    private lateinit var dbHelper: ClubDatabaseHelper
    private lateinit var recyclerViewCuotas: RecyclerView
    private lateinit var cuotasAdapter: CuotasAdapter
    private var selectedCuotaId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_seleccionar_cuota)

        dbHelper = ClubDatabaseHelper(this)

        socioId = intent.getLongExtra("SOCIO_ID", -1)
        if (socioId == -1L) {
            Toast.makeText(this, "Error: ID de socio no encontrado.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        recyclerViewCuotas = findViewById(R.id.recycler_view_cuotas)
        recyclerViewCuotas.layoutManager = LinearLayoutManager(this)


        cuotasAdapter = CuotasAdapter(null) { id ->

            selectedCuotaId = id
            Toast.makeText(this, "Cuota seleccionada.", Toast.LENGTH_SHORT).show()
        }
        recyclerViewCuotas.adapter = cuotasAdapter

        cargarCuotas()


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val btnVolverAtras = findViewById<LinearLayout>(R.id.btnVolverAtras)
        btnVolverAtras.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val btnGenerarCuota = findViewById<Button>(R.id.btnGenerarCuota)
        btnGenerarCuota.setOnClickListener {
            mostrarDialogoGenerarCuota()
        }

        val btnRealizarPago = findViewById<Button>(R.id.btnRealizarPago)
        btnRealizarPago.setOnClickListener {
            if (selectedCuotaId == -1L) {
                Toast.makeText(this, "Seleccione una cuota a pagar.", Toast.LENGTH_SHORT).show()
            } else {
                iniciarRegistroPago(selectedCuotaId)
            }
        }
    }

    private fun cargarCuotas() {
        val cursor = dbHelper.getCuotasPorSocio(socioId)
        cuotasAdapter.swapCursor(cursor)
    }

    private fun mostrarDialogoGenerarCuota() {
        val dialog = GenerarCuotaDialogFragment().apply {
            arguments = Bundle().apply {
                putLong("SOCIO_ID", socioId)
            }
        }

        dialog.onCuotaGenerada = {
            cargarCuotas()
        }
        dialog.show(supportFragmentManager, "generar_cuota_dialog")
    }

    private fun iniciarRegistroPago(cuotaId: Long) {
        val intent = Intent(this, RegistrarPagoFinalActivity::class.java)
        intent.putExtra("CUOTA_ID", cuotaId)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()

        cargarCuotas()
    }
}