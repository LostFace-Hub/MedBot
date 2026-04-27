package com.example.medora

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.medora.network.RetrofitClient
import com.example.medora.utils.SessionManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {

    private lateinit var ivBack: ImageView
    private lateinit var ivProfilePic: CircleImageView
    private lateinit var fabEditProfile: FloatingActionButton
    private lateinit var tvUserName: TextView
    private lateinit var tvUserEmail: TextView
    private lateinit var tvUserPhone: TextView
    
    // Health Stats
    private lateinit var tvBloodGroup: TextView
    private lateinit var tvHeight: TextView
    private lateinit var tvWeight: TextView
    
    // Quick Actions
    private lateinit var btnMyAppointments: LinearLayout
    private lateinit var btnMedicalRecords: LinearLayout
    private lateinit var btnMyOrders: LinearLayout
    private lateinit var btnAddresses: LinearLayout
    private lateinit var btnNotifications: LinearLayout
    private lateinit var btnPrivacy: LinearLayout
    private lateinit var btnPayment: LinearLayout
    private lateinit var btnLanguage: LinearLayout
    
    // Support Cards
    private lateinit var cardHelpSupport: MaterialCardView
    private lateinit var cardTermsConditions: MaterialCardView
    private lateinit var cardPrivacyPolicy: MaterialCardView
    private lateinit var cardAboutUs: MaterialCardView
    
    // More Cards
    private lateinit var cardShareApp: MaterialCardView
    private lateinit var cardRateApp: MaterialCardView
    
    // Actions
    private lateinit var btnLogout: MaterialButton
    private lateinit var loadingOverlay: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        initViews()
        loadUserData()
        setupClickListeners()
    }

    private fun initViews() {
        ivBack = findViewById(R.id.ivBack)
        ivProfilePic = findViewById(R.id.ivProfilePic)
        fabEditProfile = findViewById(R.id.fabEditProfile)
        tvUserName = findViewById(R.id.tvUserName)
        tvUserEmail = findViewById(R.id.tvUserEmail)
        tvUserPhone = findViewById(R.id.tvUserPhone)
        
        // Health Stats
        tvBloodGroup = findViewById(R.id.tvBloodGroup)
        tvHeight = findViewById(R.id.tvHeight)
        tvWeight = findViewById(R.id.tvWeight)
        
        // Quick Actions
        btnMyAppointments = findViewById(R.id.btnMyAppointments)
        btnMedicalRecords = findViewById(R.id.btnMedicalRecords)
        btnMyOrders = findViewById(R.id.btnMyOrders)
        btnAddresses = findViewById(R.id.btnAddresses)
        btnNotifications = findViewById(R.id.btnNotifications)
        btnPrivacy = findViewById(R.id.btnPrivacy)
        btnPayment = findViewById(R.id.btnPayment)
        btnLanguage = findViewById(R.id.btnLanguage)
        
        // Support Cards
        cardHelpSupport = findViewById(R.id.cardHelpSupport)
        cardTermsConditions = findViewById(R.id.cardTermsConditions)
        cardPrivacyPolicy = findViewById(R.id.cardPrivacyPolicy)
        cardAboutUs = findViewById(R.id.cardAboutUs)
        
        // More Cards
        cardShareApp = findViewById(R.id.cardShareApp)
        cardRateApp = findViewById(R.id.cardRateApp)
        
        // Actions
        btnLogout = findViewById(R.id.btnLogout)
        loadingOverlay = findViewById(R.id.loadingOverlay)
    }

    private fun loadUserData() {
        // Load from session
        val userName = SessionManager.getUserName(this)
        val userEmail = SessionManager.getUserEmail(this)
        val userPhone = SessionManager.getUserPhone(this)
        
        tvUserName.text = userName ?: "Guest User"
        tvUserEmail.text = userEmail ?: "email@example.com"
        tvUserPhone.text = userPhone ?: "+91 0000000000"
        
        // Fetch updated profile from backend
        fetchProfileFromBackend()
    }
    
    private fun fetchProfileFromBackend() {
        loadingOverlay.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.getApiService().getProfile()
                loadingOverlay.visibility = View.GONE
                
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    if (apiResponse.status == "success" && apiResponse.data != null) {
                        val user = apiResponse.data
                        
                        tvUserName.text = user.fullName
                        tvUserEmail.text = user.email
                        tvUserPhone.text = user.phoneNumber
                        
                        // Display Health Stats
                        tvBloodGroup.text = user.bloodGroup ?: "N/A"
                        tvHeight.text = if (user.height != null) {
                            "${user.height.toInt()} cm"
                        } else {
                            "N/A"
                        }
                        tvWeight.text = if (user.weight != null) {
                            "${user.weight.toInt()} kg"
                        } else {
                            "N/A"
                        }
                        
                        // Update session
                        SessionManager.saveUserData(
                            this@ProfileActivity,
                            userId = user.userId,
                            name = user.fullName,
                            email = user.email,
                            phone = user.phoneNumber,
                            role = user.role ?: "patient"
                        )
                    }
                }
            } catch (e: Exception) {
                loadingOverlay.visibility = View.GONE
                e.printStackTrace()
                Toast.makeText(this@ProfileActivity, "Failed to load profile", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupClickListeners() {
        ivBack.setOnClickListener { finish() }

        fabEditProfile.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }

        btnMyAppointments.setOnClickListener {
            startActivity(Intent(this, AppointmentsActivity::class.java))
        }

        btnMedicalRecords.setOnClickListener {
            startActivity(Intent(this, MedicalNotesActivity::class.java))
        }

        btnMyOrders.setOnClickListener {
            startActivity(Intent(this, OrdersActivity::class.java))
        }

        btnAddresses.setOnClickListener {
            startActivity(Intent(this, AddressManagementActivity::class.java))
        }

        btnPayment.setOnClickListener {
            Toast.makeText(this, "Payment Methods - Coming Soon", Toast.LENGTH_SHORT).show()
        }

        btnNotifications.setOnClickListener {
            startActivity(Intent(this, NotificationSettingsActivity::class.java))
        }

        btnPrivacy.setOnClickListener {
            startActivity(Intent(this, PrivacySettingsActivity::class.java))
        }

        btnLanguage.setOnClickListener {
            showLanguageDialog()
        }

        cardHelpSupport.setOnClickListener {
            startActivity(Intent(this, HelpSupportActivity::class.java))
        }

        cardTermsConditions.setOnClickListener {
            startActivity(Intent(this, TermsConditionsActivity::class.java))
        }

        cardPrivacyPolicy.setOnClickListener {
            startActivity(Intent(this, PrivacyPolicyActivity::class.java))
        }

        cardAboutUs.setOnClickListener {
            startActivity(Intent(this, AboutUsActivity::class.java))
        }

        cardShareApp.setOnClickListener {
            shareApp()
        }

        cardRateApp.setOnClickListener {
            rateApp()
        }

        btnLogout.setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun showLanguageDialog() {
        val languages = arrayOf("English", "Hindi", "Spanish", "French", "German")
        
        AlertDialog.Builder(this)
            .setTitle("Select Language")
            .setItems(languages) { _, which ->
                Toast.makeText(this, "Language changed to ${languages[which]}", Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    private fun shareApp() {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Medora - Healthcare App")
            putExtra(Intent.EXTRA_TEXT, "Check out Medora app for all your healthcare needs! Download now: [App Link]")
        }
        startActivity(Intent.createChooser(shareIntent, "Share Medora via"))
    }

    private fun rateApp() {
        Toast.makeText(this, "Redirecting to Play Store...", Toast.LENGTH_SHORT).show()
        // In production, open Play Store link
        // val uri = Uri.parse("market://details?id=$packageName")
        // startActivity(Intent(Intent.ACTION_VIEW, uri))
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { _, _ ->
                logout()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun logout() {
        SessionManager.clearSession(this)
        val intent = Intent(this, SignInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
