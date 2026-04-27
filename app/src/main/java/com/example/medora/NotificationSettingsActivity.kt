package com.example.medora

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class NotificationSettingsActivity : AppCompatActivity() {

    private lateinit var ivBack: ImageView
    private lateinit var switchAppointments: Switch
    private lateinit var switchOrders: Switch
    private lateinit var switchHealthTips: Switch
    private lateinit var switchPromotions: Switch
    private lateinit var switchEmailNotif: Switch
    private lateinit var switchSMSNotif: Switch
    private lateinit var btnSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_settings)

        initViews()
        loadSettings()
        setupClickListeners()
    }

    private fun initViews() {
        ivBack = findViewById(R.id.ivBack)
        switchAppointments = findViewById(R.id.switchAppointments)
        switchOrders = findViewById(R.id.switchOrders)
        switchHealthTips = findViewById(R.id.switchHealthTips)
        switchPromotions = findViewById(R.id.switchPromotions)
        switchEmailNotif = findViewById(R.id.switchEmailNotif)
        switchSMSNotif = findViewById(R.id.switchSMSNotif)
        btnSave = findViewById(R.id.btnSave)
    }

    private fun loadSettings() {
        // Load notification preferences from SharedPreferences
        val prefs = getSharedPreferences("notification_prefs", MODE_PRIVATE)
        switchAppointments.isChecked = prefs.getBoolean("appointments", true)
        switchOrders.isChecked = prefs.getBoolean("orders", true)
        switchHealthTips.isChecked = prefs.getBoolean("health_tips", true)
        switchPromotions.isChecked = prefs.getBoolean("promotions", false)
        switchEmailNotif.isChecked = prefs.getBoolean("email", true)
        switchSMSNotif.isChecked = prefs.getBoolean("sms", true)
    }

    private fun setupClickListeners() {
        ivBack.setOnClickListener { finish() }

        btnSave.setOnClickListener {
            saveSettings()
        }
    }

    private fun saveSettings() {
        val prefs = getSharedPreferences("notification_prefs", MODE_PRIVATE)
        prefs.edit().apply {
            putBoolean("appointments", switchAppointments.isChecked)
            putBoolean("orders", switchOrders.isChecked)
            putBoolean("health_tips", switchHealthTips.isChecked)
            putBoolean("promotions", switchPromotions.isChecked)
            putBoolean("email", switchEmailNotif.isChecked)
            putBoolean("sms", switchSMSNotif.isChecked)
            apply()
        }
        
        Toast.makeText(this, "Settings saved successfully", Toast.LENGTH_SHORT).show()
        finish()
    }
}
