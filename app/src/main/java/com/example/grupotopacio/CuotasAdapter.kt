package com.example.grupotopacio

import android.annotation.SuppressLint
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CuotasAdapter(
    private var cursor: Cursor?,
    private val onItemClick: (Long) -> Unit
) : RecyclerView.Adapter<CuotasAdapter.ViewHolder>() {

    private var selectedPosition = -1 // Ninguna cuota seleccionada inicialmente

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.text_datos_cuota)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cuota, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        if (cursor?.moveToPosition(position) == true) {
            val monto = cursor!!.getDouble(cursor!!.getColumnIndexOrThrow("monto"))
            val vencimiento = cursor!!.getString(cursor!!.getColumnIndexOrThrow("fecha_vencimiento"))
            val id = cursor!!.getLong(cursor!!.getColumnIndexOrThrow("_id"))

            holder.textView.text = "Vence: $vencimiento - Monto: $$monto"

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
                onItemClick(id)
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

    // Método para obtener la cuota seleccionada
    fun getSelectedItemId(): Long {
        return if (selectedPosition != -1 && cursor?.moveToPosition(selectedPosition) == true) {
            cursor!!.getLong(cursor!!.getColumnIndexOrThrow("_id"))
        } else {
            -1
        }
    }

    // Método para limpiar la selección
    fun clearSelection() {
        selectedPosition = -1
        notifyDataSetChanged()
    }
}