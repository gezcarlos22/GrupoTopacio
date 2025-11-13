package com.example.grupotopacio

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ClubDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "club.db"
        private const val DATABASE_VERSION = 1

        // Tabla Socios
        private const val TABLE_SOCIOS = "socios"
        private const val COL_SOCIO_ID = "id"
        private const val COL_SOCIO_NOMBRE = "nombre"
        private const val COL_SOCIO_APELLIDO = "apellido"
        private const val COL_SOCIO_DNI = "dni"
        private const val COL_SOCIO_TELEFONO = "telefono"
        private const val COL_SOCIO_CORREO = "correo"
        private const val COL_SOCIO_TIPO = "tipo"
        private const val COL_SOCIO_ALIAS = "alias"

        // Tabla Cuotas
        private const val TABLE_CUOTAS = "cuotas"
        private const val COL_CUOTA_ID = "id"
        private const val COL_CUOTA_SOCIO_ID = "socio_id"
        private const val COL_CUOTA_MONTO = "monto"
        private const val COL_CUOTA_FECHA_VENCIMIENTO = "fecha_vencimiento"
        private const val COL_CUOTA_PAGADA = "pagada"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createSociosTable = """
            CREATE TABLE $TABLE_SOCIOS (
                $COL_SOCIO_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_SOCIO_NOMBRE TEXT,
                $COL_SOCIO_APELLIDO TEXT,
                $COL_SOCIO_DNI TEXT UNIQUE,
                $COL_SOCIO_TELEFONO TEXT,
                $COL_SOCIO_CORREO TEXT,
                $COL_SOCIO_TIPO TEXT,
                $COL_SOCIO_ALIAS TEXT UNIQUE
            )
        """.trimIndent()

        val createCuotasTable = """
            CREATE TABLE $TABLE_CUOTAS (
                $COL_CUOTA_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_CUOTA_SOCIO_ID INTEGER,
                $COL_CUOTA_MONTO REAL,
                $COL_CUOTA_FECHA_VENCIMIENTO TEXT,
                $COL_CUOTA_PAGADA INTEGER DEFAULT 0,
                FOREIGN KEY($COL_CUOTA_SOCIO_ID) REFERENCES $TABLE_SOCIOS($COL_SOCIO_ID)
            )
        """.trimIndent()

        db?.execSQL(createSociosTable)
        db?.execSQL(createCuotasTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_CUOTAS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_SOCIOS")
        onCreate(db)
    }

    /**
     * TEMA I: Verifica si un alias (o DNI) ya existe.
     * Basado en la lógica de "Alias único" del PDF
     */
    fun checkDniUnico(dni: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT COUNT(*) FROM $TABLE_SOCIOS WHERE $COL_SOCIO_DNI = ?"
        val cursor = db.rawQuery(query, arrayOf(dni))
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        db.close()
        return count > 0
    }

    /**
     * TEMA I: Inserta un nuevo socio en la base de datos.
     */
    fun registrarSocio(nombre: String, apellido: String, dni: String, telefono: String, correo: String, tipo: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COL_SOCIO_NOMBRE, nombre)
            put(COL_SOCIO_APELLIDO, apellido)
            put(COL_SOCIO_DNI, dni)
            put(COL_SOCIO_TELEFONO, telefono)
            put(COL_SOCIO_CORREO, correo)
            put(COL_SOCIO_TIPO, tipo)

        }
        val id = db.insert(TABLE_SOCIOS, null, values)
        db.close()
        return id
    }

    /**
     * TEMA II y III: Obtiene todos los socios para listarlos.
     * Agregamos 'as _id' para que sea compatible con SimpleCursorAdapter
     */
    fun getSocios(): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT $COL_SOCIO_ID as _id, $COL_SOCIO_NOMBRE, $COL_SOCIO_APELLIDO, $COL_SOCIO_DNI FROM $TABLE_SOCIOS ORDER BY $COL_SOCIO_APELLIDO", null)
    }

    /**
     * TEMA III: Obtiene socios cuya cuota vence hoy.
     * Basado en "Lógica para obtener el listado" y "Uso de fechas"
     */
    fun getSociosConVencimiento(fechaHoy: String): Cursor {
        val db = this.readableDatabase
        val query = """
            SELECT s.$COL_SOCIO_ID as _id, s.$COL_SOCIO_NOMBRE, s.$COL_SOCIO_APELLIDO, s.$COL_SOCIO_DNI, c.$COL_CUOTA_FECHA_VENCIMIENTO
            FROM $TABLE_SOCIOS s
            JOIN $TABLE_CUOTAS c ON s.$COL_SOCIO_ID = c.$COL_CUOTA_SOCIO_ID
            WHERE c.$COL_CUOTA_FECHA_VENCIMIENTO = ? AND c.$COL_CUOTA_PAGADA = 0
            ORDER BY s.$COL_SOCIO_APELLIDO
        """.trimIndent()
        return db.rawQuery(query, arrayOf(fechaHoy))
    }



    fun getCuotasPorSocio(socioId: Long): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT $COL_CUOTA_ID as _id, $COL_CUOTA_MONTO, $COL_CUOTA_FECHA_VENCIMIENTO FROM $TABLE_CUOTAS WHERE $COL_CUOTA_SOCIO_ID = ? AND $COL_CUOTA_PAGADA = 0", arrayOf(socioId.toString()))
    }

    /**
     * TEMA II: Genera una nueva cuota, calculando el vencimiento a 1 mes.
     * Basado en "Uso de fechas" del PDF
     * CORREGIDO: Usando Calendar en lugar de LocalDate (API 26+)
     */
    fun generarNuevaCuota(socioId: Long, monto: Double) {
        val db = this.writableDatabase


        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, 1)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val fechaVencimiento = dateFormat.format(calendar.time)

        val values = ContentValues().apply {
            put(COL_CUOTA_SOCIO_ID, socioId)
            put(COL_CUOTA_MONTO, monto)
            put(COL_CUOTA_FECHA_VENCIMIENTO, fechaVencimiento)
            put(COL_CUOTA_PAGADA, 0)
        }
        db.insert(TABLE_CUOTAS, null, values)
        db.close()
    }

    fun getCuota(cuotaId: Long): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_CUOTAS WHERE $COL_CUOTA_ID = ?", arrayOf(cuotaId.toString()))
    }

    fun marcarCuotaPagada(cuotaId: Long) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COL_CUOTA_PAGADA, 1)
        }
        db.update(TABLE_CUOTAS, values, "$COL_CUOTA_ID = ?", arrayOf(cuotaId.toString()))
        db.close()
    }

    fun getSocioById(socioId: Long): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_SOCIOS WHERE $COL_SOCIO_ID = ?", arrayOf(socioId.toString()))
    }

    /**
     * TEMA II: Obtiene los datos para el recibo, uniendo tablas.
     * Basado en la lógica de "Código del documento"
     */
    fun getDatosRecibo(cuotaId: Long): Cursor {
        val db = this.readableDatabase
        val query = """
            SELECT s.$COL_SOCIO_NOMBRE, s.$COL_SOCIO_APELLIDO, s.$COL_SOCIO_DNI, 
                   c.$COL_CUOTA_MONTO, c.$COL_CUOTA_FECHA_VENCIMIENTO
            FROM $TABLE_SOCIOS s
            JOIN $TABLE_CUOTAS c ON s.$COL_SOCIO_ID = c.$COL_CUOTA_SOCIO_ID
            WHERE c.$COL_CUOTA_ID = ?
        """.trimIndent()
        return db.rawQuery(query, arrayOf(cuotaId.toString()))
    }
}