package com.example.grupotopacio

import android.annotation.SuppressLint
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SociosAdapter(
    private var cursor: Cursor?,
    private val onItemClick: (Long, String) -> Unit
) : RecyclerView.Adapter<SociosAdapter.ViewHolder>() {

    private var selectedPosition = -1 // Ninguno seleccionado inicialmente

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.text_view_datos_cliente)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cliente, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        if (cursor?.moveToPosition(position) == true) {
            val nombre = cursor!!.getString(cursor!!.getColumnIndexOrThrow("nombre"))
            val apellido = cursor!!.getString(cursor!!.getColumnIndexOrThrow("apellido"))
            val dni = cursor!!.getString(cursor!!.getColumnIndexOrThrow("dni"))
            val id = cursor!!.getLong(cursor!!.getColumnIndexOrThrow("_id"))

            holder.textView.text = "$nombre $apellido - DNI: $dni"

            // Cambiar el fondo según si está seleccionado o no
            if (position == selectedPosition) {
                holder.itemView.setBackgroundColor(holder.itemView.context.getColor(R.color.purple))
                holder.textView.setTextColor(holder.itemView.context.getColor(R.color.white))
            } else {
                holder.itemView.setBackgroundColor(holder.itemView.context.getColor(R.color.white))
                holder.textView.setTextColor(holder.itemView.context.getColor(R.color.black))
            }

            holder.itemView.setOnClickListener {
                selectedPosition = position
                notifyDataSetChanged() // Actualizar todas las vistas
                onItemClick(id, "$nombre $apellido")
            }
        }
    }

    override fun getItemCount(): Int = cursor?.count ?: 0

    fun swapCursor(newCursor: Cursor?) {
        cursor?.close()
        cursor = newCursor
        selectedPosition = -1 // Resetear selección al cambiar datos
        notifyDataSetChanged()
    }

    // Método para obtener el elemento seleccionado
    fun getSelectedItemId(): Long {
        return if (selectedPosition != -1 && cursor?.moveToPosition(selectedPosition) == true) {
            cursor!!.getLong(cursor!!.getColumnIndexOrThrow("_id"))
        } else {
            -1
        }
    }
}