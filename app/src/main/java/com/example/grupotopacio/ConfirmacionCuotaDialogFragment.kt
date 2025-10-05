package com.example.grupotopacio

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import android.view.ViewGroup.LayoutParams

class ConfirmacionCuotaDialogFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_confirmacion_pago_cuota, container, false)
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

        val btnCancelar = view.findViewById<Button>(R.id.btnCancelarCuota)
        val btnAceptar = view.findViewById<Button>(R.id.btnAceptarCuota)

        btnCancelar.setOnClickListener {
            dismiss()
        }

        btnAceptar.setOnClickListener {
            dismiss()
            val intent = Intent(activity, ReciboPagoActivity::class.java)
            startActivity(intent)
        }
    }
}