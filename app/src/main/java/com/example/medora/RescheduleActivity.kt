package com.example.medora

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

data class BookedAppointment(
    val id: Int,
    val doctorName: String,
    val dateTime: String,
    val place: String,
    val avatarRes: Int
)

class BookedAppointmentAdapter(
    private val context: Context,
    private val appointments: List<BookedAppointment>,
    private val onAppointmentClick: (BookedAppointment) -> Unit
) : RecyclerView.Adapter<BookedAppointmentAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val avatar: ImageView = view.findViewById(R.id.imgBookedDoctorAvatar)
        val name: TextView = view.findViewById(R.id.tvBookedDoctorName)
        val dateTime: TextView = view.findViewById(R.id.tvBookedDateTime)
        val place: TextView = view.findViewById(R.id.tvBookedPlace)

        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onAppointmentClick(appointments[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_booked_appointment, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appointment = appointments[position]
        holder.avatar.setImageResource(appointment.avatarRes)
        holder.name.text = appointment.doctorName
        holder.dateTime.text = appointment.dateTime
        holder.place.text = appointment.place
    }

    override fun getItemCount() = appointments.size
}

class RescheduleActivity : AppCompatActivity() {

    private var selectedDate: Calendar? = null
    private var selectedSlot: String? = null
    private var selectedAppointment: BookedAppointment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reschedule)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        setupBookedAppointments()
        setupBottomNavigation()
    }

    private fun setupBookedAppointments() {
        // Sample booked appointments
        val bookedAppointments = listOf(
            BookedAppointment(1, "Dr. Sarah Johnson", "Nov 20, 2025 at 4:30 PM", "Aster Hospital", R.drawable.ic_profile),
            BookedAppointment(2, "Dr. Michael Chen", "Nov 22, 2025 at 10:00 AM", "City Medical Center", R.drawable.ic_profile),
            BookedAppointment(3, "Dr. Neha Kapoor", "Nov 25, 2025 at 2:00 PM", "Aster Hospital", R.drawable.ic_profile)
        )

        val rvBookedAppointments = findViewById<RecyclerView>(R.id.rvBookedAppointments)
        rvBookedAppointments.layoutManager = LinearLayoutManager(this)
        rvBookedAppointments.adapter = BookedAppointmentAdapter(this, bookedAppointments) { appointment ->
            showRescheduleOptions(appointment)
        }
    }

    private fun showRescheduleOptions(appointment: BookedAppointment) {
        selectedAppointment = appointment

        // Show selected appointment card
        val layoutSelected = findViewById<LinearLayout>(R.id.layoutSelectedAppointment)
        layoutSelected.visibility = View.VISIBLE

        findViewById<ImageView>(R.id.currentDoctorAvatar).setImageResource(appointment.avatarRes)
        findViewById<TextView>(R.id.currentDoctorName).text = appointment.doctorName
        findViewById<TextView>(R.id.currentDateTime).text = appointment.dateTime
        findViewById<TextView>(R.id.currentPlace).text = appointment.place

        // Setup date picker
        setupDatePicker()

        // Setup confirm button
        findViewById<Button>(R.id.btnConfirmReschedule).setOnClickListener {
            if (selectedDate == null) {
                Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (selectedSlot == null) {
                Toast.makeText(this, "Please select a time slot", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val reason = findViewById<EditText>(R.id.etReason).text.toString()
            Toast.makeText(
                this,
                "Appointment with ${appointment.doctorName} rescheduled to $selectedSlot on ${SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(selectedDate!!.time)}",
                Toast.LENGTH_LONG
            ).show()
            finish()
        }
    }

    private fun setupDatePicker() {
        val cardSelectDate = findViewById<CardView>(R.id.cardSelectDate)
        val tvSelectedDate = findViewById<TextView>(R.id.tvSelectedDate)

        cardSelectDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, year, month, day ->
                    selectedDate = Calendar.getInstance().apply {
                        set(year, month, day)
                    }
                    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                    tvSelectedDate.text = sdf.format(selectedDate!!.time)
                    tvSelectedDate.setTextColor(android.graphics.Color.parseColor("#1F2937"))
                    setupTimeSlots()
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).apply {
                datePicker.minDate = System.currentTimeMillis()
                show()
            }
        }
    }

    private fun setupTimeSlots() {
        val gridTimeSlots = findViewById<GridLayout>(R.id.gridTimeSlots)
        gridTimeSlots.removeAllViews()

        val timeSlots = arrayOf(
            "9:00 AM", "10:00 AM", "11:00 AM",
            "2:00 PM", "3:00 PM", "4:00 PM",
            "5:00 PM", "6:00 PM", "7:00 PM"
        )

        timeSlots.forEach { slot ->
            val card = layoutInflater.inflate(R.layout.item_time_slot, gridTimeSlots, false) as CardView
            val textView = card.findViewById<TextView>(R.id.slotTime)
            textView.text = slot

            card.setOnClickListener {
                selectedSlot = slot
                updateSlotSelection(gridTimeSlots, card)
            }

            gridTimeSlots.addView(card)
        }
    }

    private fun updateSlotSelection(grid: GridLayout, selectedCard: CardView) {
        for (i in 0 until grid.childCount) {
            val card = grid.getChildAt(i) as? CardView ?: continue
            if (card == selectedCard) {
                card.setCardBackgroundColor(android.graphics.Color.parseColor("#1BA3C4"))
                card.findViewById<TextView>(R.id.slotTime)?.setTextColor(android.graphics.Color.WHITE)
            } else {
                card.setCardBackgroundColor(android.graphics.Color.parseColor("#E0F7FA"))
                card.findViewById<TextView>(R.id.slotTime)?.setTextColor(android.graphics.Color.parseColor("#1BA3C4"))
            }
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
            startActivity(android.content.Intent(this, AppointmentsActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
        }

        navOrders.setOnClickListener {
            startActivity(android.content.Intent(this, OrdersActivity::class.java))
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
