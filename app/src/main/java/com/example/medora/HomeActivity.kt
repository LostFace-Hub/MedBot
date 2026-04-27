package com.example.medora

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.lifecycleScope
import com.example.medora.network.RetrofitClient
import com.example.medora.utils.SessionManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HomeActivity : AppCompatActivity() {

    // Header
    private lateinit var tvGreeting: TextView
    private lateinit var tvUserName: TextView
    private lateinit var ivNotification: ImageView
    private lateinit var redDot: View
    private lateinit var ivProfile: ImageView

    // Vital Cards
    private lateinit var cardHeart: View
    private lateinit var cardBP: View
    private lateinit var cardSteps: View
    private lateinit var cardCalories: View
    
    // AI Insights
    private lateinit var aiInsightsContainer: LinearLayout
    private lateinit var progressInsights: ProgressBar

    // Bottom Options
    private lateinit var optLogVitals: View
    private lateinit var optConnectDevice: View
    private lateinit var optReports: View
    private lateinit var optConsultDoctor: View
    private lateinit var optMedications: View
    private lateinit var optEmergency: View

    // Bottom Navigation
    private lateinit var navHome: LinearLayout
    private lateinit var navTracking: LinearLayout
    private lateinit var navAppt: LinearLayout
    private lateinit var navOrders: LinearLayout
    private lateinit var navAi: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        initViews()
        setupHeader()
        setupVitals()
        setupBottomOptions()
        setupBottomNavigation()
        
        // Fetch data from backend
        fetchUserProfile()
        fetchHealthData()
        fetchAIHealthInsights()
    }

    // ============================================================
    // INITIALIZE VIEWS
    // ============================================================
    private fun initViews() {
        tvGreeting = findViewById(R.id.tvGreeting)
        tvUserName = findViewById(R.id.tvUserName)
        ivNotification = findViewById(R.id.ivNotification)
        redDot = findViewById(R.id.redDot)
        ivProfile = findViewById(R.id.ivProfile)

        cardHeart = findViewById(R.id.cardHeart)
        cardBP = findViewById(R.id.cardBP)
        cardSteps = findViewById(R.id.cardSteps)
        cardCalories = findViewById(R.id.cardCalories)
        
        aiInsightsContainer = findViewById(R.id.aiBox)
        progressInsights = findViewById(R.id.progressInsights)

        optLogVitals = findViewById(R.id.optLogVitals)
        optConnectDevice = findViewById(R.id.optConnectDevice)
        optReports = findViewById(R.id.optReports)
        optConsultDoctor = findViewById(R.id.optConsultDoctor)
        optMedications = findViewById(R.id.optMedications)
        optEmergency = findViewById(R.id.optEmergency)

        navHome = findViewById(R.id.navHome)
        navTracking = findViewById(R.id.navTracking)
        navAppt = findViewById(R.id.navAppt)
        navOrders = findViewById(R.id.navOrders)
        navAi = findViewById(R.id.navAi)
    }

    // ============================================================
    // HEADER ACTIONS
    // ============================================================
    private fun setupHeader() {
        ivNotification.setOnClickListener {
            Toast.makeText(this, "Opening Notifications...", Toast.LENGTH_SHORT).show()
            redDot.visibility = View.GONE
        }

        ivProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    // ============================================================
    // VITAL CARDS SETUP
    // ============================================================
    private fun setupVitals() {
        setVitalCard(cardHeart, "Heart Rate", "72 bpm", R.drawable.ic_heart, "#FFE8E8", "#FF4B4B") {
            Toast.makeText(this, "Heart Rate clicked", Toast.LENGTH_SHORT).show()
        }

        setVitalCard(cardBP, "Blood Pressure", "120/80", R.drawable.ic_vitals, "#E8F5F7", "#1BA3C4") {
            Toast.makeText(this, "Blood Pressure clicked", Toast.LENGTH_SHORT).show()
        }

        setVitalCard(cardSteps, "Steps Today", "5,342", R.drawable.ic_steps, "#EDE7F6", "#9C27B0") {
            Toast.makeText(this, "Steps clicked", Toast.LENGTH_SHORT).show()
        }

        setVitalCard(cardCalories, "Calories Burned", "1,750", R.drawable.ic_calories, "#FFF3E0", "#FF9800") {
            Toast.makeText(this, "Calories clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setVitalCard(
        view: View,
        title: String,
        value: String,
        iconRes: Int,
        bgColor: String,
        iconColor: String,
        clickAction: () -> Unit
    ) {
        val iconContainer = view.findViewById<androidx.cardview.widget.CardView>(R.id.iconContainer)
        val icon = view.findViewById<ImageView>(R.id.iconVital)
        val tvTitle = view.findViewById<TextView>(R.id.titleVital)
        val tvValue = view.findViewById<TextView>(R.id.valueVital)

        iconContainer.setCardBackgroundColor(android.graphics.Color.parseColor(bgColor))
        icon.setImageResource(iconRes)
        icon.setColorFilter(android.graphics.Color.parseColor(iconColor))
        tvTitle.text = title
        tvValue.text = value

        view.setOnClickListener { clickAction() }
    }

    // ============================================================
    // BOTTOM OPTIONS SETUP
    // ============================================================
    private fun setupBottomOptions() {

        setOption(optLogVitals, "Log Vitals", R.drawable.ic_vitals, "#E8F5F7", "#1BA3C4") {
            LogVitalsBottomSheet().show(supportFragmentManager, "Vitals")
        }

        setOption(optConnectDevice, "Connect Device", R.drawable.ic_connect, "#EDE7F6", "#9C27B0") {
            ConnectWearablesBottomSheet().show(supportFragmentManager, "Connect")
        }

        setOption(optReports, "Health Reports", R.drawable.ic_reports, "#FFF3E0", "#FF9800") {
            HealthReportsBottomSheet().show(supportFragmentManager, "Reports")
        }

        setOption(optConsultDoctor, "Consult Doctor", R.drawable.ic_doctor, "#FFE8E8", "#FF4B4B") {
            ConsultationBottomSheet().show(supportFragmentManager, "Consult")
        }

        setOption(optMedications, "Medications", R.drawable.ic_medications, "#E8F8F3", "#00C177") {
            MedicationTrackerBottomSheet().show(supportFragmentManager, "Medication")
        }

        setOption(optEmergency, "Emergency", R.drawable.ic_emergency, "#FFEBEE", "#D32F2F") {
            EmergencyBottomSheet().show(supportFragmentManager, "Emergency")
        }
    }

    private fun setOption(
        view: View,
        title: String,
        iconRes: Int,
        bgColor: String,
        iconColor: String,
        clickAction: () -> Unit
    ) {
        val iconBg = view.findViewById<androidx.cardview.widget.CardView>(R.id.iconBg)
        val iconView = view.findViewById<ImageView>(R.id.iconOption)
        val titleView = view.findViewById<TextView>(R.id.titleOption)

        iconBg.setCardBackgroundColor(android.graphics.Color.parseColor(bgColor))
        iconView.setImageResource(iconRes)
        iconView.setColorFilter(android.graphics.Color.parseColor(iconColor))
        titleView.text = title

        view.setOnClickListener { clickAction() }
    }

    // ============================================================
    // BOTTOM NAVIGATION SETUP
    // ============================================================
    private fun setupBottomNavigation() {
        // Set Home as active
        setNavigationActive(navHome)

        navHome.setOnClickListener {
            // Already on Home
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
            startActivity(Intent(this, AIAssistantActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
        }
    }

    private fun setNavigationActive(activeNav: LinearLayout) {
        // Reset all navigation items
        resetNavigationItem(navHome)
        resetNavigationItem(navTracking)
        resetNavigationItem(navAppt)
        resetNavigationItem(navOrders)
        resetNavigationItem(navAi)

        // Set active navigation item
        val iconBg = activeNav.getChildAt(0) as CardView
        val icon = iconBg.getChildAt(0) as ImageView
        val text = activeNav.getChildAt(1) as TextView

        iconBg.setCardBackgroundColor(android.graphics.Color.parseColor("#E8F5F7"))
        icon.setColorFilter(android.graphics.Color.parseColor("#1BA3C4"))
        text.setTextColor(android.graphics.Color.parseColor("#1BA3C4"))
        text.setTypeface(null, android.graphics.Typeface.BOLD)
    }

    private fun resetNavigationItem(nav: LinearLayout) {
        val iconBg = nav.getChildAt(0) as CardView
        val icon = iconBg.getChildAt(0) as ImageView
        val text = nav.getChildAt(1) as TextView

        iconBg.setCardBackgroundColor(android.graphics.Color.parseColor("#00000000"))
        icon.setColorFilter(android.graphics.Color.parseColor("#6B7280"))
        text.setTextColor(android.graphics.Color.parseColor("#6B7280"))
        text.setTypeface(null, android.graphics.Typeface.NORMAL)
    }
    
    // ============================================================
    // BACKEND INTEGRATION
    // ============================================================
    private fun fetchUserProfile() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.getApiService().getProfile()
                Log.d("HomeActivity", "Profile API Response: ${response.code()}")
                Log.d("HomeActivity", "Response body: ${response.body()}")
                
                if (response.isSuccessful && response.body()?.status == "success") {
                    val user = response.body()?.data
                    user?.let {
                        Log.d("HomeActivity", "User fullName from API: ${it.fullName}")
                        
                        // Update greeting with user name
                        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                        val greeting = when (hour) {
                            in 0..11 -> "Good Morning"
                            in 12..16 -> "Good Afternoon"
                            else -> "Good Evening"
                        }
                        tvGreeting.text = greeting
                        tvUserName.text = it.fullName ?: "User"
                        
                        // Also save to SessionManager for consistency
                        SessionManager.saveUserProfile(this@HomeActivity, it.fullName ?: "User")
                    }
                } else {
                    Log.e("HomeActivity", "Profile API failed: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("HomeActivity", "Error fetching profile: ${e.message}", e)
                e.printStackTrace()
            }
        }
    }
    
    private fun fetchHealthData() {
        lifecycleScope.launch {
            try {
                val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                val response = RetrofitClient.getApiService().getHealthData(startDate = today, endDate = today)
                
                if (response.isSuccessful && response.body()?.status == "success") {
                    val healthDataList = response.body()?.data
                    if (!healthDataList.isNullOrEmpty()) {
                        val todayData = healthDataList.first()
                        
                        // Update heart rate
                        todayData.heartRate?.let { heartRateList ->
                            if (heartRateList.isNotEmpty()) {
                                val avgHeartRate = heartRateList.map { it.bpm }.average().toInt()
                                updateVitalCard(cardHeart, "Heart Rate", "$avgHeartRate bpm")
                            }
                        }
                        
                        // Update steps
                        todayData.steps?.let {
                            val stepsFormatted = String.format("%,d", it.count)
                            updateVitalCard(cardSteps, "Steps Today", stepsFormatted)
                        }
                        
                        // Update calories
                        todayData.calories?.let {
                            val caloriesFormatted = String.format("%,d", it.consumed)
                            updateVitalCard(cardCalories, "Calories Burned", caloriesFormatted)
                        }
                        
                        // BP data would typically come from heartRate measurements or separate API
                        // For now keeping static or can be enhanced based on backend structure
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    private fun updateVitalCard(view: View, title: String, value: String) {
        val tvTitle = view.findViewById<TextView>(R.id.titleVital)
        val tvValue = view.findViewById<TextView>(R.id.valueVital)
        tvTitle.text = title
        tvValue.text = value
    }
    
    private fun fetchAIHealthInsights() {
        lifecycleScope.launch {
            try {
                progressInsights.visibility = View.VISIBLE
                
                val response = RetrofitClient.getApiService().getHealthInsights()
                
                if (response.isSuccessful && response.body()?.status == "success") {
                    val insightsData = response.body()?.data
                    insightsData?.insights?.let { insights ->
                        displayAIInsights(insights)
                    }
                } else {
                    // Show default message if no insights
                    showDefaultInsight()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                showDefaultInsight()
            } finally {
                progressInsights.visibility = View.GONE
            }
        }
    }
    
    private fun displayAIInsights(insights: List<com.example.medora.network.HealthInsight>) {
        aiInsightsContainer.removeAllViews()
        
        insights.take(3).forEach { insight ->
            val insightCard = layoutInflater.inflate(
                R.layout.item_ai_insight,
                aiInsightsContainer,
                false
            )
            
            val borderView = insightCard.findViewById<View>(R.id.insightBorder)
            val messageView = insightCard.findViewById<TextView>(R.id.tvInsightMessage)
            
            messageView.text = insight.message
            
            // Set color based on type
            val backgroundColor: Int
            val borderColor: Int
            when (insight.type) {
                "warning" -> {
                    backgroundColor = android.graphics.Color.parseColor("#FFF9E6")
                    borderColor = android.graphics.Color.parseColor("#FFB800")
                }
                "suggestion" -> {
                    backgroundColor = android.graphics.Color.parseColor("#E8F5F7")
                    borderColor = android.graphics.Color.parseColor("#1BA3C4")
                }
                else -> {
                    backgroundColor = android.graphics.Color.parseColor("#E8F8F3")
                    borderColor = android.graphics.Color.parseColor("#00C177")
                }
            }
            
            (insightCard as? CardView)?.setCardBackgroundColor(backgroundColor)
            borderView.setBackgroundColor(borderColor)
            
            aiInsightsContainer.addView(insightCard)
        }
    }
    
    private fun showDefaultInsight() {
        aiInsightsContainer.removeAllViews()
        
        val insightCard = layoutInflater.inflate(
            R.layout.item_ai_insight,
            aiInsightsContainer,
            false
        )
        
        val messageView = insightCard.findViewById<TextView>(R.id.tvInsightMessage)
        messageView.text = "Start tracking your health data to get personalized AI insights! Connect your smartwatch or log vitals manually."
        
        (insightCard as? CardView)?.setCardBackgroundColor(
            android.graphics.Color.parseColor("#E8F5F7")
        )
        
        aiInsightsContainer.addView(insightCard)
    }
}
