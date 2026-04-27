package com.example.medora

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class EmergencySOSActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emergency_sos)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // Emergency Call Button
        findViewById<Button>(R.id.btnEmergencyCall).setOnClickListener {
            showEmergencyDialog()
        }

        // Ambulance Card
        findViewById<CardView>(R.id.cardAmbulance).setOnClickListener {
            makePhoneCall("102") // India Ambulance Emergency Number
        }

        // Hospital Card
        findViewById<CardView>(R.id.cardHospital).setOnClickListener {
            Toast.makeText(this, "Opening directions to Aster Hospital...", Toast.LENGTH_SHORT).show()
        }

        // My Doctor Card
        findViewById<CardView>(R.id.cardMyDoctor).setOnClickListener {
            makePhoneCall("+1234567890")
        }

        setupBottomNavigation()
    }

    private fun showEmergencyDialog() {
        AlertDialog.Builder(this)
            .setTitle("🚨 Emergency Call")
            .setMessage("Are you sure you want to call emergency services (112)?")
            .setPositiveButton("Call Now") { _, _ ->
                makePhoneCall("112") // India National Emergency Number
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun makePhoneCall(number: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$number")
            }
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Unable to make call", Toast.LENGTH_SHORT).show()
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
