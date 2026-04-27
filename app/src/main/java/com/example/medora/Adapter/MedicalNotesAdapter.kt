package com.example.medora

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class MedicalNote(
    val id: Int,
    val title: String,
    val doctor: String,
    val date: String,
    val content: String,
    val category: String
)

class MedicalNotesAdapter(
    private val context: Context,
    private val notes: List<MedicalNote>,
    private val onClick: (MedicalNote) -> Unit
) : RecyclerView.Adapter<MedicalNotesAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.noteTitle)
        val doctor: TextView = view.findViewById(R.id.noteDoctor)
        val date: TextView = view.findViewById(R.id.noteDate)
        val content: TextView = view.findViewById(R.id.noteContent)
        val category: TextView = view.findViewById(R.id.noteCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_medical_note, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = notes[position]
        holder.title.text = note.title
        holder.doctor.text = note.doctor
        holder.date.text = note.date
        holder.content.text = note.content
        holder.category.text = note.category
        holder.itemView.setOnClickListener { onClick(note) }
    }

    override fun getItemCount() = notes.size
}
