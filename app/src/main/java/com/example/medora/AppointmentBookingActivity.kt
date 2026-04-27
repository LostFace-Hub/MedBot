package com.example.medora

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.example.medora.network.BookAppointmentRequest
import com.example.medora.network.RetrofitClient
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AppointmentBookingActivity : AppCompatActivity() {

    private var selectedDate: Calendar? = null
    private var selectedTimeSlot: String? = null
    private var doctorId: String? = null
    private var doctorName: String = "Doctor"
    private lateinit var progressBar: ProgressBar
    
    private val timeSlots = listOf(
        "09:00 AM", "10:00 AM", "11:00 AM",
        "02:00 PM", "03:00 PM", "04:00 PM",
        "05:00 PM", "06:00 PM", "07:00 PM"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointment_booking)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // Get doctor data from intent
        doctorId = intent.getStringExtra("doctorId")
        doctorName = intent.getStringExtra("doctorName") ?: "Dr. Unknown"
        val doctorSpeciality = intent.getStringExtra("doctorSpeciality") ?: ""
        val doctorAvatar = intent.getIntExtra("doctorAvatar", R.drawable.ic_profile)

        // Set doctor information
        findViewById<ImageView>(R.id.imgDoctorAvatar).setImageResource(doctorAvatar)
        findViewById<TextView>(R.id.tvDoctorName).text = doctorName
        findViewById<TextView>(R.id.tvDoctorSpeciality).text = doctorSpeciality

        // Initialize progress bar
        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.GONE

        // Date Picker
        findViewById<CardView>(R.id.cardDatePicker).setOnClickListener {
            showDatePicker()
        }

        // Setup time slots
        setupTimeSlots()

        // Confirm Booking Button
        findViewById<Button>(R.id.btnConfirmBooking).setOnClickListener {
            bookAppointment()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                selectedDate = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                }
                val dateFormat = SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.getDefault())
                findViewById<TextView>(R.id.tvSelectedDate).text = dateFormat.format(selectedDate!!.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        
        datePickerDialog.datePicker.minDate = calendar.timeInMillis
        datePickerDialog.show()
    }

    private fun setupTimeSlots() {
        val gridLayout = findViewById<GridLayout>(R.id.gridTimeSlots)
        gridLayout.removeAllViews()

        timeSlots.forEach { time ->
            val slotCard = layoutInflater.inflate(R.layout.item_time_slot, null) as CardView
            val slotText = slotCard.findViewById<TextView>(R.id.slotTime)
            slotText.text = time

            val params = GridLayout.LayoutParams().apply {
                width = 0
                height = ViewGroup.LayoutParams.WRAP_CONTENT
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setMargins(8, 8, 8, 8)
            }
            slotCard.layoutParams = params

            slotCard.setOnClickListener {
                selectedTimeSlot = time
                updateSlotSelection(gridLayout, slotCard)
            }

            gridLayout.addView(slotCard)
        }
    }

    private fun updateSlotSelection(gridLayout: GridLayout, selectedCard: CardView) {
        for (i in 0 until gridLayout.childCount) {
            val card = gridLayout.getChildAt(i) as CardView
            val text = card.findViewById<TextView>(R.id.slotTime)

            if (card == selectedCard) {
                card.setCardBackgroundColor(android.graphics.Color.parseColor("#1BA3C4"))
                text.setTextColor(android.graphics.Color.WHITE)
            } else {
                card.setCardBackgroundColor(android.graphics.Color.parseColor("#E0F7FA"))
                text.setTextColor(android.graphics.Color.parseColor("#1BA3C4"))
            }
        }
    }

    private fun bookAppointment() {
        // Validation
        if (selectedDate == null) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedTimeSlot == null) {
            Toast.makeText(this, "Please select a time slot", Toast.LENGTH_SHORT).show()
            return
        }

        if (doctorId == null) {
            Toast.makeText(this, "Doctor information not available", Toast.LENGTH_SHORT).show()
            return
        }

        val purpose = findViewById<EditText>(R.id.etPurpose).text.toString()
        
        if (purpose.isEmpty()) {
            Toast.makeText(this, "Please enter the purpose of visit", Toast.LENGTH_SHORT).show()
            return
        }

        // Format date for API (yyyy-MM-dd)
        val apiDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val appointmentDate = apiDateFormat.format(selectedDate!!.time)
        
        // Convert time slot to 24-hour format (HH:mm)
        val appointmentTime = convertTo24HourFormat(selectedTimeSlot!!)

        // Create booking request
        val bookingRequest = BookAppointmentRequest(
            doctorId = doctorId!!,
            appointmentDate = appointmentDate,
            appointmentTime = appointmentTime,
            appointmentType = "In-Person", // Can be made dynamic
            symptoms = null,
            notes = null,
            reason = purpose
        )

        // Make API call
        lifecycleScope.launch {
            try {
                progressBar.visibility = View.VISIBLE
                findViewById<Button>(R.id.btnConfirmBooking).isEnabled = false
                
                val response = RetrofitClient.getApiService().bookAppointment(bookingRequest)
                
                if (response.isSuccessful && response.body()?.status == "success") {
                    val appointment = response.body()?.data
                    showSuccessDialog(appointmentDate, selectedTimeSlot!!)
                } else {
                    val errorMessage = response.body()?.message ?: "Failed to book appointment"
                    Toast.makeText(this@AppointmentBookingActivity, errorMessage, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@AppointmentBookingActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                progressBar.visibility = View.GONE
                findViewById<Button>(R.id.btnConfirmBooking).isEnabled = true
            }
        }
    }
    
    private fun convertTo24HourFormat(time12h: String): String {
        return try {
            val format12 = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val format24 = SimpleDateFormat("HH:mm", Locale.getDefault())
            val date = format12.parse(time12h)
            format24.format(date ?: return time12h)
        } catch (e: Exception) {
            time12h
        }
    }
    
    private fun showSuccessDialog(date: String, time: String) {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(selectedDate!!.time)
        
        AlertDialog.Builder(this)
            .setTitle("✅ Appointment Confirmed")
            .setMessage("Your appointment with $doctorName has been scheduled for $formattedDate at $time.\n\nYou will receive a confirmation message shortly.")
            .setPositiveButton("View Appointments") { _, _ ->
                val intent = Intent(this, AppointmentsActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
            }
            .setNegativeButton("OK") { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }
}
