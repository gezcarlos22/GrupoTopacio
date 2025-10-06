package com.example.grupotopacio

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ReciboPagoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_recibo_pago)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnVolverAtras = findViewById<LinearLayout>(R.id.btnVolverAtras)

        btnVolverAtras.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val btnImprimir = findViewById<Button>(R.id.btnImprimirRecibo)
        btnImprimir.setOnClickListener {
            val dialog = ImpresionExitosaDialogFragment()
            dialog.show(supportFragmentManager, "ImpresionExitosaDialog")
        }
    }
}