package com.example.grupotopacio

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

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.text_view_datos_cliente)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cliente, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (cursor?.moveToPosition(position) == true) {
            val nombre = cursor!!.getString(cursor!!.getColumnIndexOrThrow("nombre"))
            val apellido = cursor!!.getString(cursor!!.getColumnIndexOrThrow("apellido"))
            val dni = cursor!!.getString(cursor!!.getColumnIndexOrThrow("dni"))
            val id = cursor!!.getLong(cursor!!.getColumnIndexOrThrow("_id"))

            holder.textView.text = "$nombre $apellido - DNI: $dni"
            holder.itemView.setOnClickListener {
                onItemClick(id, "$nombre $apellido")
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