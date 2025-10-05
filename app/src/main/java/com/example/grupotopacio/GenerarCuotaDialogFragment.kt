package com.example.grupotopacio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import android.view.ViewGroup.LayoutParams

class GenerarCuotaDialogFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_generar_cuota, container, false)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
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

            if (montoTexto.isEmpty() || montoTexto.toDoubleOrNull() == null) {
                Toast.makeText(context, "Por favor, ingrese un monto válido.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val monto = montoTexto.toDouble()
            Toast.makeText(context, "Cuota de $${monto} registrada exitosamente.", Toast.LENGTH_LONG).show()

            dismiss()
        }
    }
}