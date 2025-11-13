package com.example.grupotopacio

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class EmitirCarnetValidoActivity : AppCompatActivity() {

    private var dniSocio: String? = null
    private var nombreSocio: String? = null
    private var apellidoSocio: String? = null
    private var telefonoSocio: String? = null
    private var correoSocio: String? = null


    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Permiso concedido. Intente de nuevo.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permiso denegado. No se puede guardar el PDF.", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emitir_carnet_valido)

        val btnVolverAtras = findViewById<LinearLayout>(R.id.btnVolverAtras)
        btnVolverAtras.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }


        nombreSocio = intent.getStringExtra("NOMBRE")
        apellidoSocio = intent.getStringExtra("APELLIDO")
        dniSocio = intent.getStringExtra("DNI")
        telefonoSocio = intent.getStringExtra("TELEFONO")
        correoSocio = intent.getStringExtra("CORREO")


        findViewById<EditText>(R.id.edit_nombre).setText(nombreSocio)
        findViewById<EditText>(R.id.edit_apellido).setText(apellidoSocio)
        findViewById<EditText>(R.id.edit_dni).setText(dniSocio)
        findViewById<EditText>(R.id.edit_telefono).setText(telefonoSocio)
        findViewById<EditText>(R.id.edit_correo).setText(correoSocio)


        val btnImprimir = findViewById<Button>(R.id.btnImprimir)
        btnImprimir.setOnClickListener {

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                } else {
                    generarPdf()
                }
            } else {
                generarPdf()
            }
        }
    }


    private fun generarPdf() {


        val inflater = LayoutInflater.from(this)
        val carnetView = inflater.inflate(R.layout.pdf_carnet_template, null)


        carnetView.findViewById<TextView>(R.id.pdf_nombre_apellido).text = "$nombreSocio $apellidoSocio"
        carnetView.findViewById<TextView>(R.id.pdf_dni).text = "DNI: $dniSocio"
        carnetView.findViewById<TextView>(R.id.pdf_correo).text = "Correo: $correoSocio"
        carnetView.findViewById<TextView>(R.id.pdf_telefono).text = "Tel: $telefonoSocio"



        val viewWidth = 600
        carnetView.measure(
            View.MeasureSpec.makeMeasureSpec(viewWidth, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        val viewHeight = carnetView.measuredHeight


        if (viewHeight == 0) {
            Toast.makeText(this, "Error al medir la vista para el PDF", Toast.LENGTH_SHORT).show()
            return
        }

        carnetView.layout(0, 0, viewWidth, viewHeight)


        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(viewWidth, viewHeight, 1).create()
        val page = document.startPage(pageInfo)


        carnetView.draw(page.canvas)
        document.finishPage(page)


        val nombreArchivo = "Carnet_${dniSocio}_${System.currentTimeMillis()}.pdf"

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, nombreArchivo)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                if (uri != null) {
                    resolver.openOutputStream(uri).use { outputStream ->
                        document.writeTo(outputStream)
                    }
                }
            } else {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(downloadsDir, nombreArchivo)
                FileOutputStream(file).use { outputStream ->
                    document.writeTo(outputStream)
                }
            }

            document.close()
            Toast.makeText(this, "PDF guardado en la carpeta Descargas", Toast.LENGTH_LONG).show()

            val dialog = ImpresionExitosaDialogFragment()
            dialog.show(supportFragmentManager, "ImpresionExitosaDialog")

        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Error al guardar PDF: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

}