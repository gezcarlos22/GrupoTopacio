package com.example.grupotopacio

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        val etUsuario = findViewById<EditText>(R.id.edit_text_usuario)
        val etPassword = findViewById<EditText>(R.id.edit_text_password)

        val btnIniciarSeccion = findViewById<Button>(R.id.btnIniciarSeccion)
        btnIniciarSeccion.setOnClickListener{
            val usuario = etUsuario.text.toString()
            val password = etPassword.text.toString()

            if(usuario.isEmpty() || password.isEmpty()){
                Toast.makeText(this, "Complete los campos obligatorios.", Toast.LENGTH_SHORT).show()
            } else if(usuario=="admin" && password=="1234"){
                val intent = Intent(this, MenuPrincipalActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Usuario o contraseña incorrecta.", Toast.LENGTH_SHORT).show()
            }

        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}