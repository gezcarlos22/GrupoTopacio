package com.example.grupotopacio

import android.database.Cursor
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegistrarPagoFinalActivity : AppCompatActivity() {

    private var cuotaId: Long = -1
    private var montoBase: Double = 0.0
    private var montoFinal: Double = 0.0
    private var tipoPagoSeleccionado: String = "EFECTIVO"
    private lateinit var dbHelper: ClubDatabaseHelper
    private lateinit var editMontoFinal: EditText
    private lateinit var radioGroupCobro: RadioGroup
    private lateinit var editFirmaAdministrador: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registrar_pago_final)

        dbHelper = ClubDatabaseHelper(this)
        editMontoFinal = findViewById(R.id.edit_monto_final)
        radioGroupCobro = findViewById(R.id.radio_group_cobro)
        editFirmaAdministrador = findViewById(R.id.edit_firma_administrador)

        cuotaId = intent.getLongExtra("CUOTA_ID", -1)
        if (cuotaId == -1L) {
            Toast.makeText(this, "Error: ID de cuota no encontrado.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        cargarDatosCuota()
        configurarRadioButtons()

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

    private fun configurarRadioButtons() {
        radioGroupCobro.setOnCheckedChangeListener { _, checkedId ->
            tipoPagoSeleccionado = when (checkedId) {
                R.id.radio_efectivo -> "EFECTIVO"
                R.id.radio_tarjeta -> "TARJETA"
                else -> "EFECTIVO"
            }
            calcularMontoFinal()
        }
    }

    private fun calcularMontoFinal() {
        montoFinal = montoBase

        // Aplicar recargo del 10% si es tarjeta
        if (tipoPagoSeleccionado == "TARJETA") {
            montoFinal *= 1.10 // 10% de recargo
        }

        // Actualizar el EditText con el monto calculado
        editMontoFinal.setText(String.format("%.2f", montoFinal))
    }

    private fun cargarDatosCuota() {
        val cursor = dbHelper.getCuota(cuotaId)
        if (cursor.moveToFirst()) {
            montoBase = cursor.getDouble(cursor.getColumnIndexOrThrow("monto"))
            calcularMontoFinal() // Calcular el monto inicial
        }
        cursor.close()
    }

    private fun mostrarConfirmacionTransaccion() {
        val firmaAdministrador = editFirmaAdministrador.text.toString()

        val dialog = ConfirmacionCuotaDialogFragment().apply {
            arguments = Bundle().apply {
                putLong("CUOTA_ID", cuotaId)
                putDouble("MONTO_FINAL", montoFinal)
                putString("TIPO_PAGO", tipoPagoSeleccionado)
                putString("FIRMA_ADMIN", firmaAdministrador)
            }
        }
        dialog.show(supportFragmentManager, "confirmacion_transaccion_dialog")
    }
}