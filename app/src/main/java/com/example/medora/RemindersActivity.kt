package com.example.medora

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RemindersActivity : AppCompatActivity() {

    private val todayReminders = mutableListOf<Reminder>()
    private val upcomingReminders = mutableListOf<Reminder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminders)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        findViewById<ImageView>(R.id.btnAddReminder).setOnClickListener {
            Toast.makeText(this, "Add new reminder", Toast.LENGTH_SHORT).show()
        }

        setupReminders()
        setupBottomNavigation()
    }

    private fun setupReminders() {
        // Today's reminders
        todayReminders.addAll(
            listOf(
                Reminder(1, "Take Medication", "9:00 AM", "Blood pressure medication", true),
                Reminder(2, "Doctor Appointment", "4:30 PM", "Dr. Sarah Johnson - Cardiology", true),
                Reminder(3, "Evening Medication", "8:00 PM", "After dinner", true)
            )
        )

        // Upcoming reminders
        upcomingReminders.addAll(
            listOf(
                Reminder(4, "Lab Test", "Tomorrow, 10:00 AM", "Fasting blood test", true),
                Reminder(5, "Physiotherapy", "Nov 21, 3:00 PM", "Weekly session", true),
                Reminder(6, "Medication Refill", "Nov 25", "Visit pharmacy", true)
            )
        )

        // Setup Today's RecyclerView
        val rvToday = findViewById<RecyclerView>(R.id.rvTodayReminders)
        rvToday.layoutManager = LinearLayoutManager(this)
        rvToday.adapter = RemindersAdapter(this, todayReminders) { reminder, isEnabled ->
            Toast.makeText(
                this,
                "${reminder.title} ${if (isEnabled) "enabled" else "disabled"}",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Setup Upcoming RecyclerView
        val rvUpcoming = findViewById<RecyclerView>(R.id.rvUpcomingReminders)
        rvUpcoming.layoutManager = LinearLayoutManager(this)
        rvUpcoming.adapter = RemindersAdapter(this, upcomingReminders) { reminder, isEnabled ->
            Toast.makeText(
                this,
                "${reminder.title} ${if (isEnabled) "enabled" else "disabled"}",
                Toast.LENGTH_SHORT
            ).show()
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
