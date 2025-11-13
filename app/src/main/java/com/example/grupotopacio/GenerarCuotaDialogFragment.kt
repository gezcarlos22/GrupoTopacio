package com.example.grupotopacio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment

class GenerarCuotaDialogFragment : DialogFragment() {

    private var socioId: Long = -1
    private lateinit var dbHelper: ClubDatabaseHelper
    var onCuotaGenerada: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = ClubDatabaseHelper(requireContext())
        socioId = arguments?.getLong("SOCIO_ID") ?: -1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_generar_cuota, container, false)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val editMonto = view.findViewById<EditText>(R.id.edit_monto_cuota)
        val btnCancelar = view.findViewById<Button>(R.id.btnCancelar)
        val btnRegistrar = view.findViewById<Button>(R.id.btnRegistrar)

        btnCancelar.setOnClickListener {
            dismiss()
        }

        btnRegistrar.setOnClickListener {
            val montoTexto = editMonto.text.toString()
            if (montoTexto.isEmpty() || socioId == -1L) {
                Toast.makeText(context, "Monto inválido o error de socio.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val monto = montoTexto.toDoubleOrNull()
            if (monto == null || monto <= 0) {
                Toast.makeText(context, "Por favor, ingrese un monto válido.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            dbHelper.generarNuevaCuota(socioId, monto)
            Toast.makeText(context, "Cuota registrada. Vence en 1 mes.", Toast.LENGTH_LONG).show()

            onCuotaGenerada?.invoke()
            dismiss()
        }
    }
}