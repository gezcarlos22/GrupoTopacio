package com.example.grupotopacio

import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class VerVencimientosActivity : AppCompatActivity() {

    private lateinit var dbHelper: ClubDatabaseHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var sociosAdapter: SociosAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ver_vencimientos)

        dbHelper = ClubDatabaseHelper(this)
        recyclerView = findViewById(R.id.recycler_view_vencimientos)
        recyclerView.layoutManager = LinearLayoutManager(this)


        sociosAdapter = SociosAdapter(null) { _, _ ->

        }
        recyclerView.adapter = sociosAdapter

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnVolverAtras = findViewById<LinearLayout>(R.id.btnVolverAtras)
        btnVolverAtras.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        cargarVencimientos()
    }

    private fun getTodayDateString(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private fun cargarVencimientos() {
        val fechaHoy = getTodayDateString()
        val cursor = dbHelper.getSociosConVencimiento(fechaHoy)
        sociosAdapter.swapCursor(cursor)
    }
}