package com.example.medora

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DoctorProfileActivity : AppCompatActivity() {

    private lateinit var adapter: DoctorSearchAdapter
    private val allDoctors = mutableListOf<DoctorProfile>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_profile)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        setupTopDoctors()
        setupSearch()
        setupBottomNavigation()
    }

    private fun setupTopDoctors() {
        // Top doctors list
        allDoctors.addAll(
            listOf(
                DoctorProfile(
                    1, "Dr. Sarah Johnson", "Cardiologist", 4.9f, 128, 15, "5000+", "98%",
                    true, R.drawable.ic_profile,
                    "Dr. Sarah Johnson is a highly experienced cardiologist with over 15 years of experience in treating heart conditions. She specializes in interventional cardiology and has performed over 2000 successful procedures.",
                    "Mon - Fri: 9:00 AM - 6:00 PM\nSat: 10:00 AM - 2:00 PM",
                    "Aster Hospital, Downtown Medical Center\n123 Medical Plaza, City"
                ),
                DoctorProfile(
                    2, "Dr. Michael Chen", "Cardiologist", 4.8f, 95, 12, "3500+", "96%",
                    true, R.drawable.ic_profile,
                    "Dr. Michael Chen specializes in preventive cardiology and cardiac rehabilitation with 12 years of experience.",
                    "Mon - Fri: 10:00 AM - 7:00 PM",
                    "Aster Hospital, Downtown Medical Center"
                ),
                DoctorProfile(
                    3, "Dr. Neha Kapoor", "Dermatologist", 4.7f, 142, 10, "4200+", "95%",
                    true, R.drawable.ic_profile,
                    "Dr. Neha Kapoor is an expert dermatologist specializing in cosmetic and medical dermatology.",
                    "Mon - Sat: 9:00 AM - 5:00 PM",
                    "Aster Hospital, Downtown Medical Center"
                ),
                DoctorProfile(
                    4, "Dr. Anirudh Sinha", "Orthopedic", 4.5f, 156, 18, "6000+", "97%",
                    true, R.drawable.ic_profile,
                    "Dr. Anirudh Sinha is a renowned orthopedic surgeon with expertise in joint replacement and sports injuries.",
                    "Mon - Fri: 8:00 AM - 4:00 PM",
                    "Aster Hospital, Downtown Medical Center"
                ),
                DoctorProfile(
                    5, "Dr. Priya Sharma", "Pediatric", 4.9f, 201, 14, "5500+", "99%",
                    true, R.drawable.ic_profile,
                    "Dr. Priya Sharma is a compassionate pediatrician with extensive experience in child healthcare.",
                    "Mon - Sat: 9:00 AM - 7:00 PM",
                    "Aster Hospital, Downtown Medical Center"
                ),
                DoctorProfile(
                    6, "Dr. Emily Roberts", "Dermatologist", 4.6f, 87, 8, "2800+", "94%",
                    false, R.drawable.ic_profile,
                    "Dr. Emily Roberts focuses on treating skin conditions and cosmetic procedures.",
                    "Tue - Sat: 11:00 AM - 6:00 PM",
                    "City Medical Center"
                )
            )
        )

        val rvTopDoctors = findViewById<RecyclerView>(R.id.rvTopDoctors)
        rvTopDoctors.layoutManager = LinearLayoutManager(this)
        adapter = DoctorSearchAdapter(this, allDoctors.toMutableList()) { doctor ->
            // Open doctor details page
            val intent = Intent(this, DoctorDetailsActivity::class.java)
            intent.putExtra("doctorId", doctor.id)
            intent.putExtra("doctorName", doctor.name)
            intent.putExtra("doctorSpeciality", doctor.speciality)
            intent.putExtra("doctorRating", doctor.rating)
            intent.putExtra("doctorReviews", doctor.reviews)
            intent.putExtra("doctorExperience", doctor.experience)
            intent.putExtra("doctorPatients", doctor.patients)
            intent.putExtra("doctorSuccessRate", doctor.successRate)
            intent.putExtra("doctorAvatar", doctor.avatarRes)
            intent.putExtra("doctorAbout", doctor.about)
            intent.putExtra("doctorWorkingHours", doctor.workingHours)
            intent.putExtra("doctorLocation", doctor.location)
            startActivity(intent)
        }
        rvTopDoctors.adapter = adapter
    }

    private fun setupSearch() {
        val etSearch = findViewById<EditText>(R.id.etSearchDoctor)
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filterDoctors(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun filterDoctors(query: String) {
        val searchQuery = query.lowercase().trim()
        val filtered = allDoctors.filter { doctor ->
            doctor.name.lowercase().contains(searchQuery) ||
                    doctor.speciality.lowercase().contains(searchQuery)
        }
        adapter.updateList(filtered)
        findViewById<TextView>(R.id.tvResultsCount).text = if (query.isEmpty()) {
            "Top Rated Doctors"
        } else {
            "${filtered.size} Doctors Found"
        }
    }

    private fun setupBottomNavigation() {
        val navAppt = findViewById<LinearLayout>(R.id.navAppt)
        val navHome = findViewById<LinearLayout>(R.id.navHome)
        val navTracking = findViewById<LinearLayout>(R.id.navTracking)
        val navOrders = findViewById<LinearLayout>(R.id.navOrders)
        val navAi = findViewById<LinearLayout>(R.id.navAi)

        setNavigationActive(navAppt)

        navHome.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
        }

        navTracking.setOnClickListener {
            startActivity(Intent(this, TrackingActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
        }

        navAppt.setOnClickListener {
            startActivity(Intent(this, AppointmentsActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
        }
        navOrders.setOnClickListener {
            startActivity(Intent(this, OrdersActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
        }

        navAi.setOnClickListener {
            Toast.makeText(this, "AI Assistant - Coming Soon", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setNavigationActive(activeNav: LinearLayout) {
        val navHome = findViewById<LinearLayout>(R.id.navHome)
        val navTracking = findViewById<LinearLayout>(R.id.navTracking)
        val navAppt = findViewById<LinearLayout>(R.id.navAppt)
        val navOrders = findViewById<LinearLayout>(R.id.navOrders)
        val navAi = findViewById<LinearLayout>(R.id.navAi)

        listOf(navHome, navTracking, navAppt, navOrders, navAi).forEach { nav ->
            val iconBg = nav.getChildAt(0) as CardView
            val icon = iconBg.getChildAt(0) as ImageView
            val text = nav.getChildAt(1) as TextView

            if (nav == activeNav) {
                iconBg.setCardBackgroundColor(android.graphics.Color.parseColor("#E8F5F7"))
                iconBg.cardElevation = 2f
                icon.setColorFilter(android.graphics.Color.parseColor("#1BA3C4"))
                text.setTextColor(android.graphics.Color.parseColor("#1BA3C4"))
                text.typeface = android.graphics.Typeface.DEFAULT_BOLD
            } else {
                iconBg.setCardBackgroundColor(android.graphics.Color.parseColor("#F5F8FA"))
                iconBg.cardElevation = 0f
                icon.setColorFilter(android.graphics.Color.parseColor("#6B7280"))
                text.setTextColor(android.graphics.Color.parseColor("#6B7280"))
                text.typeface = android.graphics.Typeface.DEFAULT
            }
        }
    }
}
