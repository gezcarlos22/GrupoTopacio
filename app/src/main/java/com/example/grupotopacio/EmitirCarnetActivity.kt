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

class EmitirCarnetActivity : AppCompatActivity() {

    private lateinit var dbHelper: ClubDatabaseHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var sociosAdapter: SociosAdapter
    private var selectedSocioId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_emitir_carnet)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = ClubDatabaseHelper(this)
        recyclerView = findViewById(R.id.recycler_view_clientes)
        recyclerView.layoutManager = LinearLayoutManager(this)


        sociosAdapter = SociosAdapter(null) { id, nombre ->
            selectedSocioId = id
            Toast.makeText(this, "Socio seleccionado: $nombre", Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = sociosAdapter

        cargarSocios()

        val btnVolverAtras = findViewById<LinearLayout>(R.id.btnVolverAtras)
        btnVolverAtras.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val btnGenerar = findViewById<Button>(R.id.btnGenerarCarnet)
        btnGenerar.setOnClickListener {
            if (selectedSocioId == -1L) {
                Toast.makeText(this, "Por favor, seleccione un socio.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            val cursor = dbHelper.getSocioById(selectedSocioId)
            if (cursor.moveToFirst()) {
                val intent = Intent(this, EmitirCarnetValidoActivity::class.java)


                intent.putExtra("NOMBRE", cursor.getString(cursor.getColumnIndexOrThrow("nombre")))
                intent.putExtra("APELLIDO", cursor.getString(cursor.getColumnIndexOrThrow("apellido")))
                intent.putExtra("DNI", cursor.getString(cursor.getColumnIndexOrThrow("dni")))
                intent.putExtra("TELEFONO", cursor.getString(cursor.getColumnIndexOrThrow("telefono")))
                intent.putExtra("CORREO", cursor.getString(cursor.getColumnIndexOrThrow("correo")))

                startActivity(intent)
            }
            cursor.close()
        }
    }

    private fun cargarSocios() {
        val cursor = dbHelper.getSocios()
        sociosAdapter.swapCursor(cursor)
    }

    override fun onResume() {
        super.onResume()
        cargarSocios()
    }
}