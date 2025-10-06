package com.example.grupotopacio

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class EmitirCarnetValidoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emitir_carnet_valido)

        val btnVolverAtras = findViewById<LinearLayout>(R.id.btnVolverAtras)

        btnVolverAtras.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}