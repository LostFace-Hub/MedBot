package com.example.medora.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.medora.R
import com.example.medora.Appointment

class AppointmentAdapter(private val ctx: Context, private val list: List<Appointment>) :
    RecyclerView.Adapter<AppointmentAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val iv: ImageView = view.findViewById(R.id.ivAvatar)
        val name: TextView = view.findViewById(R.id.tvName)
        val specialization: TextView = view.findViewById(R.id.tvSpecialization)
        val date: TextView = view.findViewById(R.id.tvDate)
        val place: TextView = view.findViewById(R.id.tvPlace)
        val status: TextView = view.findViewById(R.id.tvStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_past_appointment, parent, false)
        return VH(v)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val a = list[position]
        holder.iv.setImageResource(a.avatarRes)
        holder.name.text = a.name
        holder.specialization.text = "Specialist" // You can add this to Appointment data class if needed
        holder.date.text = a.dateTime
        holder.place.text = a.place
        holder.status.text = "Completed"
    }
}
