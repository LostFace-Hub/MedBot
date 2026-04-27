package com.example.medora

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medora.network.RetrofitClient
import com.example.medora.utils.SessionManager
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class ChatHistory(
    val id: Int,
    val title: String,
    val preview: String,
    val timestamp: String
)

class AIAssistantActivity : AppCompatActivity() {

    private lateinit var tvWelcomeMessage: TextView
    private lateinit var etMessage: EditText
    private lateinit var btnSend: FloatingActionButton
    private lateinit var btnCamera: ImageView
    private lateinit var btnVoiceInput: ImageView
    private lateinit var btnNotifications: ImageView
    private lateinit var ivProfile: de.hdodenhof.circleimageview.CircleImageView
    
    // Bottom Navigation
    private lateinit var navHome: LinearLayout
    private lateinit var navTracking: LinearLayout
    private lateinit var navAppt: LinearLayout
    private lateinit var navOrders: LinearLayout
    private lateinit var navAi: LinearLayout
    
    private lateinit var rvChatHistory: RecyclerView
    private lateinit var layoutEmptyState: LinearLayout
    private lateinit var progressBar: ProgressBar

    // Quick Action Cards
    private lateinit var cardCheckSymptoms: MaterialCardView
    private lateinit var cardWorkoutGuide: MaterialCardView
    private lateinit var cardRecommendations: MaterialCardView
    private lateinit var cardMentalHealth: MaterialCardView
    private lateinit var cardDietPlanner: MaterialCardView
    private lateinit var cardTrackSleep: MaterialCardView

    private val chatHistoryList = mutableListOf<ChatHistory>()
    private var currentSessionId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ai_assistant)

        initViews()
        setupBottomNavigation()
        setupQuickActions()
        setupChatHistory()
        setupListeners()
        setupWelcomeMessage()
        
        // Fetch chat history from backend
        fetchChatHistory()
    }

    private fun initViews() {
        tvWelcomeMessage = findViewById(R.id.tvWelcomeMessage)
        etMessage = findViewById(R.id.etMessage)
        btnSend = findViewById(R.id.btnSend)
        btnCamera = findViewById(R.id.btnCamera)
        btnVoiceInput = findViewById(R.id.btnVoiceInput)
        btnNotifications = findViewById(R.id.btnNotifications)
        ivProfile = findViewById(R.id.ivProfile)
        rvChatHistory = findViewById(R.id.rvChatHistory)
        layoutEmptyState = findViewById(R.id.layoutEmptyState)
        progressBar = findViewById(R.id.progressBar) // Add to layout
        
        // Bottom Navigation
        navHome = findViewById(R.id.navHome)
        navTracking = findViewById(R.id.navTracking)
        navAppt = findViewById(R.id.navAppt)
        navOrders = findViewById(R.id.navOrders)
        navAi = findViewById(R.id.navAi)

        // Quick Action Cards
        cardCheckSymptoms = findViewById(R.id.cardCheckSymptoms)
        cardWorkoutGuide = findViewById(R.id.cardWorkoutGuide)
        cardRecommendations = findViewById(R.id.cardRecommendations)
        cardMentalHealth = findViewById(R.id.cardMentalHealth)
        cardDietPlanner = findViewById(R.id.cardDietPlanner)
        cardTrackSleep = findViewById(R.id.cardTrackSleep)
    }

    private fun setupBottomNavigation() {
        // Set AI as active
        setNavigationActive(navAi)

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
            // Already on AI page
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
        val iconBg = when (activeNav.id) {
            R.id.navHome -> findViewById<CardView>(R.id.navHomeIconBg)
            R.id.navTracking -> findViewById<CardView>(R.id.navTrackingIconBg)
            R.id.navAppt -> findViewById<CardView>(R.id.navApptIconBg)
            R.id.navOrders -> findViewById<CardView>(R.id.navOrdersIconBg)
            R.id.navAi -> findViewById<CardView>(R.id.navAiIconBg)
            else -> null
        }

        val icon = when (activeNav.id) {
            R.id.navHome -> findViewById<ImageView>(R.id.navHomeIcon)
            R.id.navTracking -> findViewById<ImageView>(R.id.navTrackingIcon)
            R.id.navAppt -> findViewById<ImageView>(R.id.navApptIcon)
            R.id.navOrders -> findViewById<ImageView>(R.id.navOrdersIcon)
            R.id.navAi -> findViewById<ImageView>(R.id.navAiIcon)
            else -> null
        }

        val text = when (activeNav.id) {
            R.id.navHome -> findViewById<TextView>(R.id.navHomeText)
            R.id.navTracking -> findViewById<TextView>(R.id.navTrackingText)
            R.id.navAppt -> findViewById<TextView>(R.id.navApptText)
            R.id.navOrders -> findViewById<TextView>(R.id.navOrdersText)
            R.id.navAi -> findViewById<TextView>(R.id.navAiText)
            else -> null
        }

        iconBg?.apply {
            setCardBackgroundColor(android.graphics.Color.parseColor("#E8F5F7"))
            cardElevation = 2f
        }
        icon?.setColorFilter(android.graphics.Color.parseColor("#1BA3C4"))
        text?.apply {
            setTextColor(android.graphics.Color.parseColor("#1BA3C4"))
            setTypeface(null, android.graphics.Typeface.BOLD)
        }
    }

    private fun resetNavigationItem(navItem: LinearLayout) {
        val iconBg = when (navItem.id) {
            R.id.navHome -> findViewById<CardView>(R.id.navHomeIconBg)
            R.id.navTracking -> findViewById<CardView>(R.id.navTrackingIconBg)
            R.id.navAppt -> findViewById<CardView>(R.id.navApptIconBg)
            R.id.navOrders -> findViewById<CardView>(R.id.navOrdersIconBg)
            R.id.navAi -> findViewById<CardView>(R.id.navAiIconBg)
            else -> null
        }

        val icon = when (navItem.id) {
            R.id.navHome -> findViewById<ImageView>(R.id.navHomeIcon)
            R.id.navTracking -> findViewById<ImageView>(R.id.navTrackingIcon)
            R.id.navAppt -> findViewById<ImageView>(R.id.navApptIcon)
            R.id.navOrders -> findViewById<ImageView>(R.id.navOrdersIcon)
            R.id.navAi -> findViewById<ImageView>(R.id.navAiIcon)
            else -> null
        }

        val text = when (navItem.id) {
            R.id.navHome -> findViewById<TextView>(R.id.navHomeText)
            R.id.navTracking -> findViewById<TextView>(R.id.navTrackingText)
            R.id.navAppt -> findViewById<TextView>(R.id.navApptText)
            R.id.navOrders -> findViewById<TextView>(R.id.navOrdersText)
            R.id.navAi -> findViewById<TextView>(R.id.navAiText)
            else -> null
        }

        iconBg?.apply {
            setCardBackgroundColor(android.graphics.Color.parseColor("#F5F8FA"))
            cardElevation = 0f
        }
        icon?.setColorFilter(android.graphics.Color.parseColor("#6B7280"))
        text?.apply {
            setTextColor(android.graphics.Color.parseColor("#6B7280"))
            setTypeface(null, android.graphics.Typeface.NORMAL)
        }
    }

    private fun setupQuickActions() {
        cardCheckSymptoms.setOnClickListener {
            sendQuickAction("check-symptoms", "I want to check my symptoms")
        }

        cardWorkoutGuide.setOnClickListener {
            sendQuickAction("workout-guide", "I need an AI workout guide")
        }

        cardRecommendations.setOnClickListener {
            sendQuickAction("health-recommendations", "Give me personalized health recommendations")
        }

        cardMentalHealth.setOnClickListener {
            sendQuickAction("mental-health", "I need mental health support")
        }

        cardDietPlanner.setOnClickListener {
            sendQuickAction("diet-plan", "Create a diet plan for me")
        }

        cardTrackSleep.setOnClickListener {
            sendQuickAction("sleep-tracking", "Help me track and improve my sleep")
        }
    }

    private fun sendQuickAction(action: String, prompt: String) {
        // Send quick action to backend and open chat
        // TODO: Implement SessionManager and API methods
        /*
        lifecycleScope.launch {
            try {
                val token = SessionManager(this@AIAssistantActivity).getAuthToken()
                val response = RetrofitClient.getApiService().sendQuickAction(
                    authorization = "Bearer $token",
                    request = mapOf(
                        "action" to action,
                        "prompt" to prompt
                    )
                )
                
                if (response.isSuccessful && response.body()?.status == "success") {
                    val sessionId = response.body()?.data?.get("sessionId") as? String
                    openChatWithPrompt(prompt, sessionId)
                } else {
                    // Fallback: open chat without session
                    openChatWithPrompt(prompt, null)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Fallback: open chat without session
                openChatWithPrompt(prompt, null)
            }
        }
        */
        // For now, directly open chat
        openChatWithPrompt(prompt, null)
    }

    private fun setupChatHistory() {
        // Sample chat history
        chatHistoryList.addAll(
            listOf(
                ChatHistory(
                    1,
                    "Symptom Check",
                    "What are the symptoms of flu and how can I prevent it?",
                    "2h ago"
                ),
                ChatHistory(
                    2,
                    "Workout Routine",
                    "Can you suggest a 30-minute workout routine for beginners?",
                    "1d ago"
                ),
                ChatHistory(
                    3,
                    "Diet Plan",
                    "I need a vegetarian diet plan for weight loss",
                    "3d ago"
                )
            )
        )

        if (chatHistoryList.isEmpty()) {
            layoutEmptyState.visibility = View.VISIBLE
            rvChatHistory.visibility = View.GONE
        } else {
            layoutEmptyState.visibility = View.GONE
            rvChatHistory.visibility = View.VISIBLE
            
            val adapter = ChatHistoryAdapter(chatHistoryList) { chat ->
                openChatDetail(chat)
            }
            rvChatHistory.layoutManager = LinearLayoutManager(this)
            rvChatHistory.adapter = adapter
        }
    }
    
    private fun fetchChatHistory() {
        // TODO: Implement SessionManager and API methods
        /*
        lifecycleScope.launch {
            try {
                progressBar.visibility = View.VISIBLE
                val token = SessionManager(this@AIAssistantActivity).getAuthToken()
                val response = RetrofitClient.getApiService().getAIChatHistory(
                    authorization = "Bearer $token"
                )
                
                progressBar.visibility = View.GONE
                
                if (response.isSuccessful && response.body()?.status == "success") {
                    val history = response.body()?.data?.get("history") as? List<Map<String, Any>>
                    if (!history.isNullOrEmpty()) {
                        chatHistoryList.clear()
                        history.forEach { item ->
                            chatHistoryList.add(
                                ChatHistory(
                                    id = (item["id"] as? Double)?.toInt() ?: 0,
                                    title = item["title"] as? String ?: "Chat",
                                    preview = item["preview"] as? String ?: "",
                                    timestamp = item["timestamp"] as? String ?: ""
                                )
                            )
                        }
                        layoutEmptyState.visibility = View.GONE
                        rvChatHistory.visibility = View.VISIBLE
                        rvChatHistory.adapter?.notifyDataSetChanged()
                    }
                }
            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                e.printStackTrace()
            }
        }
        */
        // For now, use sample data already loaded in setupChatHistory()
    }

    private fun setupListeners() {
        btnSend.setOnClickListener {
            sendMessage()
        }

        btnCamera.setOnClickListener {
            Toast.makeText(this, "Camera feature - Upload medical images", Toast.LENGTH_SHORT).show()
        }

        btnVoiceInput.setOnClickListener {
            Toast.makeText(this, "Voice input - Speak your query", Toast.LENGTH_SHORT).show()
        }

        btnNotifications.setOnClickListener {
            Toast.makeText(this, "Notifications", Toast.LENGTH_SHORT).show()
        }

        ivProfile.setOnClickListener {
            Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupWelcomeMessage() {
        val userName = "Aryan" // Get from preferences/database
        tvWelcomeMessage.text = "Hello $userName, How can I assist you today!"
    }

    private fun sendMessage() {
        val message = etMessage.text.toString().trim()
        
        if (message.isEmpty()) {
            Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show()
            return
        }

        // Send message to AI backend
        openChatWithPrompt(message, null)
        etMessage.setText("")
    }

    private fun openChatWithPrompt(prompt: String, sessionId: String?) {
        val intent = Intent(this, AIChatActivity::class.java)
        intent.putExtra("prompt", prompt)
        sessionId?.let { intent.putExtra("sessionId", it) }
        startActivity(intent)
    }

    private fun openChatDetail(chat: ChatHistory) {
        val intent = Intent(this, AIChatActivity::class.java)
        intent.putExtra("chatId", chat.id)
        startActivity(intent)
    }

    // Chat History Adapter
    inner class ChatHistoryAdapter(
        private val chats: List<ChatHistory>,
        private val onChatClick: (ChatHistory) -> Unit
    ) : RecyclerView.Adapter<ChatHistoryAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val ivChatIcon: ImageView = view.findViewById(R.id.ivChatIcon)
            val tvChatTitle: TextView = view.findViewById(R.id.tvChatTitle)
            val tvChatPreview: TextView = view.findViewById(R.id.tvChatPreview)
            val tvChatTime: TextView = view.findViewById(R.id.tvChatTime)
        }

        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
            val view = android.view.LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chat_history, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val chat = chats[position]
            
            holder.tvChatTitle.text = chat.title
            holder.tvChatPreview.text = chat.preview
            holder.tvChatTime.text = chat.timestamp

            holder.itemView.setOnClickListener {
                onChatClick(chat)
            }
        }

        override fun getItemCount() = chats.size
    }
}
