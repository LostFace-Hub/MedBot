package com.example.medora

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medora.Adapter.AppointmentAdapter
import com.example.medora.network.RetrofitClient
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class Appointment(val id: Int, val name: String, val dateTime: String, val place: String, val avatarRes: Int, val isUpcoming: Boolean)
data class Doctor(val id: Int, val name: String, val speciality: String, val rating: Float, val avatarRes: Int, val available: Boolean)

class AppointmentsActivity : AppCompatActivity() {

    private lateinit var upcomingCard: View
    private lateinit var tvUpcomingName: TextView
    private lateinit var tvUpcomingTime: TextView
    private lateinit var tvUpcomingPlace: TextView
    private lateinit var btnJoinNow: Button
    private lateinit var tvNoUpcoming: TextView
    private lateinit var rvPast: RecyclerView
    private var pastAppointmentsList = mutableListOf<Appointment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointments)

        initViews()
        setupActions()
        setupBottomNavigation()
        
        // Fetch appointments from backend
        fetchAppointments()

        // View More click handler
        findViewById<TextView>(R.id.tvViewMore).setOnClickListener {
            startActivity(android.content.Intent(this, PastAppointmentsActivity::class.java))
        }
    }
    
    private fun initViews() {
        upcomingCard = findViewById(R.id.upcomingCard)
        tvUpcomingName = findViewById(R.id.upcomingName)
        tvUpcomingTime = findViewById(R.id.upcomingTime)
        tvUpcomingPlace = findViewById(R.id.upcomingPlace)
        btnJoinNow = findViewById(R.id.btnJoinNow)
        rvPast = findViewById(R.id.rvPast)
        
        rvPast.layoutManager = LinearLayoutManager(this)
        rvPast.adapter = AppointmentAdapter(this, pastAppointmentsList)
    }
    
    private fun setupActions() {
        btnJoinNow.setOnClickListener {
            Toast.makeText(this, "Joining appointment", Toast.LENGTH_SHORT).show()
        }

        // Setup action cards click listeners
        findViewById<androidx.cardview.widget.CardView>(R.id.actionBook).setOnClickListener {
            startActivity(android.content.Intent(this, BookAppointmentActivity::class.java))
        }

        findViewById<androidx.cardview.widget.CardView>(R.id.actionReschedule).setOnClickListener {
            startActivity(android.content.Intent(this, RescheduleActivity::class.java))
        }

        findViewById<androidx.cardview.widget.CardView>(R.id.actionProfile).setOnClickListener {
            startActivity(android.content.Intent(this, DoctorProfileActivity::class.java))
        }

        findViewById<androidx.cardview.widget.CardView>(R.id.actionNotes).setOnClickListener {
            startActivity(android.content.Intent(this, MedicalNotesActivity::class.java))
        }

        findViewById<androidx.cardview.widget.CardView>(R.id.actionReminders).setOnClickListener {
            startActivity(android.content.Intent(this, RemindersActivity::class.java))
        }

        findViewById<androidx.cardview.widget.CardView>(R.id.actionSOS).setOnClickListener {
            startActivity(android.content.Intent(this, EmergencySOSActivity::class.java))
        }

        // Setup bottom navigation
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        val navHome = findViewById<LinearLayout>(R.id.navHome)
        val navTracking = findViewById<LinearLayout>(R.id.navTracking)
        val navAppt = findViewById<LinearLayout>(R.id.navAppt)
        val navOrders = findViewById<LinearLayout>(R.id.navOrders)
        val navAi = findViewById<LinearLayout>(R.id.navAi)

        // Set Appointments as active
        setNavigationActive(navAppt)

        navHome.setOnClickListener {
            startActivity(android.content.Intent(this, HomeActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
        }

        navTracking.setOnClickListener {
            startActivity(android.content.Intent(this, TrackingActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
        }

        navAppt.setOnClickListener {
            // Already on Appointments
        }

        navOrders.setOnClickListener {
            startActivity(Intent(this, OrdersActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
        }

        navAi.setOnClickListener {
            startActivity(Intent(this, AIAssistantActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
        }
    }

    private fun setNavigationActive(activeNav: LinearLayout) {
        val navHome = findViewById<LinearLayout>(R.id.navHome)
        val navTracking = findViewById<LinearLayout>(R.id.navTracking)
        val navAppt = findViewById<LinearLayout>(R.id.navAppt)
        val navOrders = findViewById<LinearLayout>(R.id.navOrders)
        val navAi = findViewById<LinearLayout>(R.id.navAi)

        // Reset all navigation items
        resetNavigationItem(navHome)
        resetNavigationItem(navTracking)
        resetNavigationItem(navAppt)
        resetNavigationItem(navOrders)
        resetNavigationItem(navAi)

        // Set active item
        setNavigationItemActive(activeNav)
    }

    private fun resetNavigationItem(nav: LinearLayout) {
        val iconBg = nav.getChildAt(0) as androidx.cardview.widget.CardView
        val icon = iconBg.getChildAt(0) as android.widget.ImageView
        val text = nav.getChildAt(1) as TextView

        iconBg.setCardBackgroundColor(android.graphics.Color.parseColor("#F5F8FA"))
        iconBg.cardElevation = 0f
        icon.setColorFilter(android.graphics.Color.parseColor("#6B7280"))
        text.setTextColor(android.graphics.Color.parseColor("#6B7280"))
        text.typeface = android.graphics.Typeface.DEFAULT
    }

    private fun setNavigationItemActive(nav: LinearLayout) {
        val iconBg = nav.getChildAt(0) as androidx.cardview.widget.CardView
        val icon = iconBg.getChildAt(0) as android.widget.ImageView
        val text = nav.getChildAt(1) as TextView

        iconBg.setCardBackgroundColor(android.graphics.Color.parseColor("#E8F5F7"))
        iconBg.cardElevation = 2f
        icon.setColorFilter(android.graphics.Color.parseColor("#1BA3C4"))
        text.setTextColor(android.graphics.Color.parseColor("#1BA3C4"))
        text.typeface = android.graphics.Typeface.DEFAULT_BOLD
    }
    
    // ============================================================
    // BACKEND INTEGRATION
    // ============================================================
    private fun fetchAppointments() {
        lifecycleScope.launch {
            try {
                // Fetch upcoming appointments (Scheduled, Confirmed, Rescheduled)
                val upcomingResponse = RetrofitClient.getApiService().getAppointments(status = "Scheduled")
                if (upcomingResponse.isSuccessful && upcomingResponse.body()?.status == "success") {
                    val appointments = upcomingResponse.body()?.data?.appointments
                    if (!appointments.isNullOrEmpty()) {
                        // Get the most recent upcoming appointment
                        val upcoming = appointments.firstOrNull()
                        if (upcoming != null) {
                            displayUpcomingAppointment(upcoming)
                        } else {
                            hideUpcomingCard()
                        }
                    } else {
                        hideUpcomingCard()
                    }
                } else {
                    hideUpcomingCard()
                }
                
                // Fetch past appointments (Completed, Cancelled)
                val pastResponse = RetrofitClient.getApiService().getAppointments(status = "Completed")
                if (pastResponse.isSuccessful && pastResponse.body()?.status == "success") {
                    val appointments = pastResponse.body()?.data?.appointments
                    if (!appointments.isNullOrEmpty()) {
                        displayPastAppointments(appointments)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@AppointmentsActivity, "Error loading appointments: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun displayUpcomingAppointment(appointment: com.example.medora.network.Appointment) {
        upcomingCard.visibility = View.VISIBLE
        
        // Display doctor name and specialization
        val doctorName = appointment.doctor?.fullName ?: "Doctor"
        val specialization = appointment.doctor?.specialization ?: ""
        tvUpcomingName.text = if (specialization.isNotEmpty()) {
            "$doctorName - $specialization"
        } else {
            doctorName
        }
        
        // Format date and time
        try {
            // Parse the date (format: yyyy-MM-dd or ISO date)
            val dateStr = appointment.appointmentDate.split("T")[0] // Handle ISO format
            val inputDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputDateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
            val date = inputDateFormat.parse(dateStr)
            
            // Format the time display
            val formattedDate = outputDateFormat.format(date ?: Date())
            tvUpcomingTime.text = "$formattedDate at ${appointment.appointmentTime}"
        } catch (e: Exception) {
            e.printStackTrace()
            tvUpcomingTime.text = "${appointment.appointmentDate.split("T")[0]} at ${appointment.appointmentTime}"
        }
        
        // Display consultation type
        tvUpcomingPlace.text = appointment.consultationType ?: "In-Person"
    }
    
    private fun hideUpcomingCard() {
        upcomingCard.visibility = View.GONE
    }
    
    private fun displayPastAppointments(appointments: List<com.example.medora.network.Appointment>) {
        pastAppointmentsList.clear()
        appointments.forEachIndexed { index, appt ->
            try {
                // Parse the date
                val dateStr = appt.appointmentDate.split("T")[0] // Handle ISO format
                val inputDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val outputDateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
                val date = inputDateFormat.parse(dateStr)
                val formattedDate = outputDateFormat.format(date ?: Date())
                
                // Format the complete date-time string
                val formattedDateTime = "$formattedDate at ${appt.appointmentTime}"
                
                // Get doctor name and specialization
                val doctorName = appt.doctor?.fullName ?: "Doctor"
                val specialization = appt.doctor?.specialization ?: ""
                val displayName = if (specialization.isNotEmpty()) {
                    "$doctorName ($specialization)"
                } else {
                    doctorName
                }
                
                pastAppointmentsList.add(
                    Appointment(
                        id = index,
                        name = displayName,
                        dateTime = formattedDateTime,
                        place = appt.consultationType ?: "In-Person",
                        avatarRes = R.drawable.ic_profile,
                        isUpcoming = false
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
                // Fallback if date parsing fails
                pastAppointmentsList.add(
                    Appointment(
                        id = index,
                        name = appt.doctor?.fullName ?: "Doctor",
                        dateTime = "${appt.appointmentDate} at ${appt.appointmentTime}",
                        place = appt.consultationType ?: "In-Person",
                        avatarRes = R.drawable.ic_profile,
                        isUpcoming = false
                    )
                )
            }
        }
        rvPast.adapter?.notifyDataSetChanged()
    }
}
