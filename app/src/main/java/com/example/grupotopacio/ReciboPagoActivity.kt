package com.example.grupotopacio

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ReciboPagoActivity : AppCompatActivity() {

    private var datosRecibo: Bundle? = null


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
        datosRecibo = intent.extras
        findViewById<EditText>(R.id.inputFecha).setText(datosRecibo?.getString("FECHA"))
        findViewById<EditText>(R.id.inputReciboDe).setText(datosRecibo?.getString("RECIBO_DE"))
        findViewById<EditText>(R.id.inputDNI).setText(datosRecibo?.getString("DNI"))
        findViewById<EditText>(R.id.inputConcepto).setText(datosRecibo?.getString("CONCEPTO"))
        findViewById<EditText>(R.id.inputTotal).setText("$$${datosRecibo?.getString("TOTAL")}")

        findViewById<EditText>(R.id.inputMedioPago).setText(datosRecibo?.getString("TIPO_PAGO"))
        findViewById<EditText>(R.id.inputFormaPago).setText(datosRecibo?.getString("TIPO_PAGO"))
        findViewById<EditText>(R.id.inputRecibidoPor).setText(datosRecibo?.getString("FIRMA_ADMIN"))
        findViewById<EditText>(R.id.inputRol).setText("Administración")


        val btnImprimir = findViewById<Button>(R.id.btnImprimirRecibo)
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




        val viewToPrint = findViewById<View>(R.id.scroll_view_recibo)


        viewToPrint.measure(
            View.MeasureSpec.makeMeasureSpec(viewToPrint.width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        val measuredHeight = viewToPrint.measuredHeight
        val measuredWidth = viewToPrint.width

        if (measuredHeight == 0 || measuredWidth == 0) {
            Toast.makeText(this, "Error al medir la vista para el PDF", Toast.LENGTH_SHORT).show()
            return
        }


        viewToPrint.layout(0, 0, measuredWidth, measuredHeight)


        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(measuredWidth, measuredHeight, 1).create()
        val page = document.startPage(pageInfo)


        viewToPrint.draw(page.canvas)
        document.finishPage(page)




        val nombreArchivo = "Recibo_${datosRecibo?.getString("DNI")}_${System.currentTimeMillis()}.pdf"

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