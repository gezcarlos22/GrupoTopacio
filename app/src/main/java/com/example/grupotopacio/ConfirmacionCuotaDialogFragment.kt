package com.example.grupotopacio

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ConfirmacionCuotaDialogFragment : DialogFragment() {

    private var cuotaId: Long = -1
    private lateinit var dbHelper: ClubDatabaseHelper
    private var tipoPago: String = ""
    private var firmaAdmin: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = ClubDatabaseHelper(requireContext())
        
        arguments?.let {
            cuotaId = it.getLong("CUOTA_ID", -1)
            tipoPago = it.getString("TIPO_PAGO", "EFECTIVO") ?: "EFECTIVO"
            firmaAdmin = it.getString("FIRMA_ADMIN", "") ?: ""
        }
    }

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


    private fun getTodayDateString(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnCancelar = view.findViewById<Button>(R.id.btnCancelarCuota)
        val btnAceptar = view.findViewById<Button>(R.id.btnAceptarCuota)

        btnCancelar.setOnClickListener {
            dismiss()
        }

        btnAceptar.setOnClickListener {
            if (cuotaId == -1L) {
                Toast.makeText(context, "Error al procesar el pago.", Toast.LENGTH_SHORT).show()
                dismiss()
                return@setOnClickListener
            }

            dbHelper.marcarCuotaPagada(cuotaId)

            val cursor = dbHelper.getDatosRecibo(cuotaId)
            if (cursor.moveToFirst()) {
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                val apellido = cursor.getString(cursor.getColumnIndexOrThrow("apellido"))
                val dni = cursor.getString(cursor.getColumnIndexOrThrow("dni"))
                val monto = cursor.getDouble(cursor.getColumnIndexOrThrow("monto"))

                val intent = Intent(activity, ReciboPagoActivity::class.java)


                intent.putExtra("FECHA", getTodayDateString())
                intent.putExtra("RECIBO_DE", "$nombre $apellido")
                intent.putExtra("DNI", dni)
                intent.putExtra("CONCEPTO", "Pago de cuota mensual")
                intent.putExtra("TOTAL", monto.toString())
                intent.putExtra("TIPO_PAGO", tipoPago)
                intent.putExtra("FIRMA_ADMIN", firmaAdmin)

                startActivity(intent)
            }
            cursor.close()
            dismiss()
        }
    }
}