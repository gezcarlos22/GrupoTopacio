package com.example.grupotopacio

import android.database.Cursor
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegistrarPagoFinalActivity : AppCompatActivity() {

    private var cuotaId: Long = -1
    private var montoFinal: Double = 0.0
    private lateinit var dbHelper: ClubDatabaseHelper
    private lateinit var editMontoFinal: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registrar_pago_final)

        dbHelper = ClubDatabaseHelper(this)
        editMontoFinal = findViewById(R.id.edit_monto_final)

        cuotaId = intent.getLongExtra("CUOTA_ID", -1)
        if (cuotaId == -1L) {
            Toast.makeText(this, "Error: ID de cuota no encontrado.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        cargarDatosCuota()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnVolverAtras = findViewById<LinearLayout>(R.id.btnVolverAtras)
        btnVolverAtras.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val btnPagar = findViewById<Button>(R.id.btnPagar)
        btnPagar.setOnClickListener {
            mostrarConfirmacionTransaccion()
        }
    }

    private fun cargarDatosCuota() {
        val cursor = dbHelper.getCuota(cuotaId)
        if (cursor.moveToFirst()) {
            montoFinal = cursor.getDouble(cursor.getColumnIndexOrThrow("monto"))
            editMontoFinal.setText(montoFinal.toString())
        }
        cursor.close()
    }

    private fun mostrarConfirmacionTransaccion() {
        val dialog = ConfirmacionCuotaDialogFragment().apply {
            arguments = Bundle().apply {
                putLong("CUOTA_ID", cuotaId)
            }
        }
        dialog.show(supportFragmentManager, "confirmacion_transaccion_dialog")
    }
}