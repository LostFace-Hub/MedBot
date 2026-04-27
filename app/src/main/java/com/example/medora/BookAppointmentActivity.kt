package com.example.medora

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Toast
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medora.network.RetrofitClient
import kotlinx.coroutines.launch

class BookAppointmentActivity : AppCompatActivity() {

    private lateinit var adapter: DoctorSearchAdapter
    private val allDoctors = mutableListOf<DoctorProfile>()
    private var selectedFilter = "All"
    private lateinit var progressBar: ProgressBar
    private lateinit var rvDoctors: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_appointment)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        progressBar = findViewById(R.id.progressBar) // Add progressBar to your layout
        rvDoctors = findViewById(R.id.rvDoctors)

        setupDoctorsList()
        setupSearch()
        setupFilters()
        setupBottomNavigation()
        
        // Fetch doctors from backend
        fetchDoctors()
    }

    private fun setupDoctorsList() {
        // Initialize with empty list, data will be loaded from backend
        val rvDoctors = findViewById<RecyclerView>(R.id.rvDoctors)
        rvDoctors.layoutManager = LinearLayoutManager(this)
        adapter = DoctorSearchAdapter(this, allDoctors.toMutableList()) { doctor ->
            val intent = Intent(this, DoctorDetailsActivity::class.java)
            intent.putExtra("doctorId", doctor.id.toString())
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
        rvDoctors.adapter = adapter

        updateResultsCount(allDoctors.size)
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

    private fun setupFilters() {
        val chipAll = findViewById<CardView>(R.id.chipAll)
        val chipCardiology = findViewById<CardView>(R.id.chipCardiology)
        val chipDermatology = findViewById<CardView>(R.id.chipDermatology)
        val chipOrthopedic = findViewById<CardView>(R.id.chipOrthopedic)
        val chipPediatric = findViewById<CardView>(R.id.chipPediatric)

        chipAll.setOnClickListener {
            selectedFilter = "All"
            updateChipStyles(chipAll, listOf(chipCardiology, chipDermatology, chipOrthopedic, chipPediatric))
            fetchDoctors(specialization = null)
        }

        chipCardiology.setOnClickListener {
            selectedFilter = "Cardiologist"
            updateChipStyles(chipCardiology, listOf(chipAll, chipDermatology, chipOrthopedic, chipPediatric))
            fetchDoctors(specialization = "Cardiology")
        }

        chipDermatology.setOnClickListener {
            selectedFilter = "Dermatologist"
            updateChipStyles(chipDermatology, listOf(chipAll, chipCardiology, chipOrthopedic, chipPediatric))
            fetchDoctors(specialization = "Dermatology")
        }

        chipOrthopedic.setOnClickListener {
            selectedFilter = "Orthopedic"
            updateChipStyles(chipOrthopedic, listOf(chipAll, chipCardiology, chipDermatology, chipPediatric))
            fetchDoctors(specialization = "Orthopedics")
        }

        chipPediatric.setOnClickListener {
            selectedFilter = "Pediatric"
            updateChipStyles(chipPediatric, listOf(chipAll, chipCardiology, chipDermatology, chipOrthopedic))
            fetchDoctors(specialization = "Pediatrics")
        }
    }

    private fun updateChipStyles(selected: CardView, others: List<CardView>) {
        selected.setCardBackgroundColor(android.graphics.Color.parseColor("#1BA3C4"))
        (selected.getChildAt(0) as TextView).setTextColor(android.graphics.Color.WHITE)

        others.forEach { chip ->
            chip.setCardBackgroundColor(android.graphics.Color.parseColor("#E0F7FA"))
            (chip.getChildAt(0) as TextView).setTextColor(android.graphics.Color.parseColor("#1BA3C4"))
        }
    }

    private fun filterDoctors(query: String) {
        val searchQuery = query.lowercase().trim()
        val filtered = allDoctors.filter { doctor ->
            val matchesSearch = doctor.name.lowercase().contains(searchQuery) ||
                    doctor.speciality.lowercase().contains(searchQuery)
            val matchesFilter = selectedFilter == "All" || doctor.speciality == selectedFilter
            matchesSearch && matchesFilter
        }

        adapter.updateList(filtered)
        updateResultsCount(filtered.size)
    }

    private fun updateResultsCount(count: Int) {
        findViewById<TextView>(R.id.tvResultsCount).text = "$count Doctors Available"
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
            finish()
        }

        navTracking.setOnClickListener {
            startActivity(Intent(this, TrackingActivity::class.java))
            finish()
        }

        navAppt.setOnClickListener {
            startActivity(Intent(this, AppointmentsActivity::class.java))
            finish()
        }
        navOrders.setOnClickListener {
            startActivity(Intent(this, OrdersActivity::class.java))
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
    
    // ============================================================
    // BACKEND INTEGRATION
    // ============================================================
    private fun fetchDoctors(specialization: String? = null, search: String? = null) {
        lifecycleScope.launch {
            try {
                progressBar.visibility = View.VISIBLE
                val response = RetrofitClient.getApiService().getDoctors(
                    page = 1,
                    limit = 100,
                    specialization = specialization,
                    search = search
                )
                
                if (response.isSuccessful && response.body()?.status == "success") {
                    val doctors = response.body()?.data?.doctors
                    if (!doctors.isNullOrEmpty()) {
                        allDoctors.clear()
                        doctors.forEach { doctor ->
                            val displaySpeciality = when (doctor.specialization) {
                                "Cardiology" -> "Cardiologist"
                                "Dermatology" -> "Dermatologist"
                                "Orthopedics" -> "Orthopedic"
                                "Pediatrics" -> "Pediatric"
                                else -> doctor.specialization
                            }
                            
                            allDoctors.add(
                                DoctorProfile(
                                    id = doctor.id.hashCode(),
                                    name = doctor.fullName,
                                    speciality = displaySpeciality,
                                    rating = doctor.rating?.toFloat() ?: 4.5f,
                                    reviews = (doctor.experience * 10), // Estimate based on experience
                                    experience = doctor.experience,
                                    patients = "${doctor.experience * 300}+",
                                    successRate = "95%",
                                    available = true,
                                    avatarRes = R.drawable.ic_profile,
                                    about = "${doctor.qualification ?: "Medical Professional"} with ${doctor.experience} years of experience in ${doctor.specialization}.",
                                    workingHours = "Mon - Fri: 9:00 AM - 6:00 PM",
                                    location = "Aster Hospital, Downtown Medical Center"
                                )
                            )
                        }
                        adapter.updateList(allDoctors)
                        updateResultsCount(allDoctors.size)
                    } else {
                        allDoctors.clear()
                        adapter.updateList(allDoctors)
                        updateResultsCount(0)
                        Toast.makeText(this@BookAppointmentActivity, "No doctors found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@BookAppointmentActivity, "Error loading doctors", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@BookAppointmentActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }
}
