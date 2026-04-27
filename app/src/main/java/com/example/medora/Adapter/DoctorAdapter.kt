package com.example.medora.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.medora.R
import com.example.medora.Doctor

class DoctorAdapter(private val list: List<Doctor>, private val onClick: (Doctor)->Unit) :
    RecyclerView.Adapter<DoctorAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val iv: ImageView = view.findViewById(R.id.doctorImage)
        val name: TextView = view.findViewById(R.id.doctorName)
        val sub: TextView = view.findViewById(R.id.doctorSpec)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_doctor_card, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val d = list[position]
        holder.iv.setImageResource(d.avatarRes)
        holder.name.text = d.name
        holder.sub.text = d.speciality
        holder.itemView.setOnClickListener { onClick(d) }
    }

    override fun getItemCount(): Int = list.size
}
