package com.example.medora

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class PrivacySettingsActivity : AppCompatActivity() {

    private lateinit var ivBack: ImageView
    private lateinit var switchProfileVisibility: Switch
    private lateinit var switchDataSharing: Switch
    private lateinit var switchLocationAccess: Switch
    private lateinit var switchActivityTracking: Switch
    private lateinit var btnSave: Button
    private lateinit var btnDeleteAccount: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_settings)

        initViews()
        loadSettings()
        setupClickListeners()
    }

    private fun initViews() {
        ivBack = findViewById(R.id.ivBack)
        switchProfileVisibility = findViewById(R.id.switchProfileVisibility)
        switchDataSharing = findViewById(R.id.switchDataSharing)
        switchLocationAccess = findViewById(R.id.switchLocationAccess)
        switchActivityTracking = findViewById(R.id.switchActivityTracking)
        btnSave = findViewById(R.id.btnSave)
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount)
    }

    private fun loadSettings() {
        val prefs = getSharedPreferences("privacy_prefs", MODE_PRIVATE)
        switchProfileVisibility.isChecked = prefs.getBoolean("profile_visibility", true)
        switchDataSharing.isChecked = prefs.getBoolean("data_sharing", false)
        switchLocationAccess.isChecked = prefs.getBoolean("location_access", true)
        switchActivityTracking.isChecked = prefs.getBoolean("activity_tracking", true)
    }

    private fun setupClickListeners() {
        ivBack.setOnClickListener { finish() }

        btnSave.setOnClickListener {
            saveSettings()
        }

        btnDeleteAccount.setOnClickListener {
            // Show confirmation dialog
            Toast.makeText(this, "Account deletion requested", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveSettings() {
        val prefs = getSharedPreferences("privacy_prefs", MODE_PRIVATE)
        prefs.edit().apply {
            putBoolean("profile_visibility", switchProfileVisibility.isChecked)
            putBoolean("data_sharing", switchDataSharing.isChecked)
            putBoolean("location_access", switchLocationAccess.isChecked)
            putBoolean("activity_tracking", switchActivityTracking.isChecked)
            apply()
        }
        
        Toast.makeText(this, "Privacy settings saved", Toast.LENGTH_SHORT).show()
        finish()
    }
}
