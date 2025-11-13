package com.example.grupotopacio

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

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.text_datos_cuota)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cuota, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (cursor?.moveToPosition(position) == true) {
            val monto = cursor!!.getDouble(cursor!!.getColumnIndexOrThrow("monto"))
            val vencimiento = cursor!!.getString(cursor!!.getColumnIndexOrThrow("fecha_vencimiento"))
            val id = cursor!!.getLong(cursor!!.getColumnIndexOrThrow("_id"))

            holder.textView.text = "Vence: $vencimiento - Monto: $$monto"
            holder.itemView.setOnClickListener {
                onItemClick(id)
            }
        }
    }

    override fun getItemCount(): Int = cursor?.count ?: 0


    fun swapCursor(newCursor: Cursor?) {
        cursor?.close()
        cursor = newCursor
        notifyDataSetChanged()
    }
}