package com.example.medora

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DoctorDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_details)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // Get doctor data from intent
        val doctorId = intent.getStringExtra("doctorId") ?: intent.getIntExtra("doctorId", 0).toString()
        val doctorName = intent.getStringExtra("doctorName") ?: "Dr. Unknown"
        val doctorSpeciality = intent.getStringExtra("doctorSpeciality") ?: ""
        val doctorRating = intent.getFloatExtra("doctorRating", 0f)
        val doctorReviews = intent.getIntExtra("doctorReviews", 0)
        val doctorExperience = intent.getIntExtra("doctorExperience", 0)
        val doctorPatients = intent.getStringExtra("doctorPatients") ?: ""
        val doctorSuccessRate = intent.getStringExtra("doctorSuccessRate") ?: ""
        val doctorAvatar = intent.getIntExtra("doctorAvatar", R.drawable.ic_profile)
        val doctorAbout = intent.getStringExtra("doctorAbout") ?: ""
        val doctorWorkingHours = intent.getStringExtra("doctorWorkingHours") ?: ""
        val doctorLocation = intent.getStringExtra("doctorLocation") ?: ""

        // Set doctor information
        findViewById<ImageView>(R.id.imgDoctorAvatar).setImageResource(doctorAvatar)
        findViewById<TextView>(R.id.tvDoctorName).text = doctorName
        findViewById<TextView>(R.id.tvDoctorSpeciality).text = doctorSpeciality
        findViewById<TextView>(R.id.tvDoctorRating).text = doctorRating.toString()
        findViewById<TextView>(R.id.tvPatients).text = doctorPatients
        findViewById<TextView>(R.id.tvExperience).text = "$doctorExperience Years"
        findViewById<TextView>(R.id.tvSuccessRate).text = doctorSuccessRate
        findViewById<TextView>(R.id.tvAbout).text = doctorAbout
        findViewById<TextView>(R.id.tvWorkingHours).text = doctorWorkingHours
        findViewById<TextView>(R.id.tvLocation).text = doctorLocation

        // Book Appointment Button
        findViewById<Button>(R.id.btnBookAppointment).setOnClickListener {
            val intent = Intent(this, AppointmentBookingActivity::class.java)
            intent.putExtra("doctorId", doctorId)
            intent.putExtra("doctorName", doctorName)
            intent.putExtra("doctorSpeciality", doctorSpeciality)
            intent.putExtra("doctorAvatar", doctorAvatar)
            startActivity(intent)
        }
    }
}
