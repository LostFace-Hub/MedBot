package com.example.medora

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class DoctorProfile(
    val id: Int,
    val name: String,
    val speciality: String,
    val rating: Float,
    val reviews: Int,
    val experience: Int,
    val patients: String,
    val successRate: String,
    val available: Boolean,
    val avatarRes: Int,
    val about: String,
    val workingHours: String,
    val location: String
)

class DoctorSearchAdapter(
    private val context: Context,
    private val doctors: MutableList<DoctorProfile>,
    private val onDoctorClick: (DoctorProfile) -> Unit
) : RecyclerView.Adapter<DoctorSearchAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val avatar: ImageView = view.findViewById(R.id.imgDoctorAvatar)
        val name: TextView = view.findViewById(R.id.tvDoctorName)
        val speciality: TextView = view.findViewById(R.id.tvDoctorSpeciality)
        val rating: TextView = view.findViewById(R.id.tvDoctorRating)
        val availability: TextView = view.findViewById(R.id.tvDoctorAvailability)
        val experience: TextView = view.findViewById(R.id.tvDoctorExperience)

        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onDoctorClick(doctors[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_doctor_search, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val doctor = doctors[position]
        holder.avatar.setImageResource(doctor.avatarRes)
        holder.name.text = doctor.name
        holder.speciality.text = doctor.speciality
        holder.rating.text = doctor.rating.toString()
        holder.experience.text = "${doctor.experience} years exp"
        
        if (doctor.available) {
            holder.availability.text = "Available Today"
            holder.availability.setBackgroundColor(android.graphics.Color.parseColor("#D1FAE5"))
            holder.availability.setTextColor(android.graphics.Color.parseColor("#059669"))
        } else {
            holder.availability.text = "Next Available: Tomorrow"
            holder.availability.setBackgroundColor(android.graphics.Color.parseColor("#FEE2E2"))
            holder.availability.setTextColor(android.graphics.Color.parseColor("#DC2626"))
        }
    }

    override fun getItemCount() = doctors.size

    fun updateList(newList: List<DoctorProfile>) {
        doctors.clear()
        doctors.addAll(newList)
        notifyDataSetChanged()
    }
}
