package com.example.medora

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medora.network.ChatRequest
import com.example.medora.network.RetrofitClient
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

data class ChatMessage(
    val message: String,
    val isAi: Boolean,
    val isTyping: Boolean = false
)

class AIChatActivity : AppCompatActivity() {

    private lateinit var rvChat: RecyclerView
    private lateinit var etChatMessage: EditText
    private lateinit var btnSendChat: FloatingActionButton
    private lateinit var btnCameraChat: ImageView
    private lateinit var btnVoiceChat: ImageView
    private lateinit var btnBack: ImageView
    private lateinit var progressBar: ProgressBar

    private lateinit var chatAdapter: ChatAdapter
    private val chatMessages = mutableListOf<ChatMessage>()
    private val handler = Handler(Looper.getMainLooper())
    private var sessionId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ai_chat)

        initViews()
        setupRecyclerView()
        setupListeners()
        
        // Get session ID if provided
        sessionId = intent.getStringExtra("sessionId")
        
        // Load initial prompt if provided
        val prompt = intent.getStringExtra("prompt")
        if (!prompt.isNullOrEmpty()) {
            etChatMessage.setText(prompt)
            sendMessage()
        }
    }

    private fun initViews() {
        rvChat = findViewById(R.id.rvChat)
        etChatMessage = findViewById(R.id.etChatMessage)
        btnSendChat = findViewById(R.id.btnSendChat)
        btnCameraChat = findViewById(R.id.btnCameraChat)
        btnVoiceChat = findViewById(R.id.btnVoiceChat)
        btnBack = findViewById(R.id.btnBack)
        progressBar = findViewById(R.id.progressBar) // Add to layout
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter(chatMessages)
        rvChat.layoutManager = LinearLayoutManager(this)
        rvChat.adapter = chatAdapter
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        btnSendChat.setOnClickListener {
            sendMessage()
        }

        btnCameraChat.setOnClickListener {
            Toast.makeText(this, "Camera - Upload medical images", Toast.LENGTH_SHORT).show()
        }

        btnVoiceChat.setOnClickListener {
            Toast.makeText(this, "Voice input", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendMessage() {
        val message = etChatMessage.text.toString().trim()
        
        if (message.isEmpty()) {
            Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show()
            return
        }

        // Add user message
        chatMessages.add(ChatMessage(message, false))
        chatAdapter.notifyItemInserted(chatMessages.size - 1)
        rvChat.scrollToPosition(chatMessages.size - 1)
        
        // Clear input
        etChatMessage.setText("")

        // Show typing indicator
        showTypingIndicator()

        // Send message to AI backend
        sendMessageToAI(message)
    }
    
    private fun sendMessageToAI(message: String) {
        lifecycleScope.launch {
            try {
                btnSendChat.isEnabled = false
                val chatRequest = ChatRequest(
                    message = message,
                    sessionId = sessionId
                )
                
                val response = RetrofitClient.getApiService().chatWithAI(chatRequest)
                
                if (response.isSuccessful && response.body()?.status == "success") {
                    val chatResponse = response.body()?.data
                    sessionId = chatResponse?.sessionId // Save session ID for next messages
                    
                    hideTypingIndicator()
                    
                    // Add AI response
                    val aiMessage = chatResponse?.message ?: "Sorry, I couldn't process that."
                    chatMessages.add(ChatMessage(aiMessage, true))
                    chatAdapter.notifyItemInserted(chatMessages.size - 1)
                    rvChat.scrollToPosition(chatMessages.size - 1)
                } else {
                    hideTypingIndicator()
                    Toast.makeText(this@AIChatActivity, "Error getting AI response", Toast.LENGTH_SHORT).show()
                    // Fallback to local response
                    generateAIResponse(message)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                hideTypingIndicator()
                Toast.makeText(this@AIChatActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
                // Fallback to local response
                generateAIResponse(message)
            } finally {
                btnSendChat.isEnabled = true
            }
        }
    }

    private fun showTypingIndicator() {
        chatMessages.add(ChatMessage("", true, true))
        chatAdapter.notifyItemInserted(chatMessages.size - 1)
        rvChat.scrollToPosition(chatMessages.size - 1)
    }

    private fun hideTypingIndicator() {
        val typingIndex = chatMessages.indexOfLast { it.isTyping }
        if (typingIndex != -1) {
            chatMessages.removeAt(typingIndex)
            chatAdapter.notifyItemRemoved(typingIndex)
        }
    }

    private fun generateAIResponse(userMessage: String) {
        // Generate AI response based on user message
        val response = when {
            userMessage.contains("symptom", ignoreCase = true) || 
            userMessage.contains("fever", ignoreCase = true) ||
            userMessage.contains("cold", ignoreCase = true) ||
            userMessage.contains("cough", ignoreCase = true) ||
            userMessage.contains("throat", ignoreCase = true) -> {
                """Based on your symptoms, here are a few possibilities:

• Common Cold 🤧
• Seasonal Flu 🦠
• Viral Infection 🔬
• It's advisable to get evaluated 📋
• Get plenty of rest 😴
• Consider a warm salt water gargle for throat relief 🧂
• Monitor your temperature regularly 🌡️

⚠️ If symptoms worsen, please consult a doctor immediately."""
            }
            userMessage.contains("workout", ignoreCase = true) ||
            userMessage.contains("exercise", ignoreCase = true) ||
            userMessage.contains("fitness", ignoreCase = true) -> {
                """Here's a personalized workout plan for you:

🏃 Cardio (20 minutes)
• Jogging or brisk walking
• Jump rope or cycling

💪 Strength Training (15 minutes)
• Push-ups: 3 sets of 10
• Squats: 3 sets of 15
• Planks: 3 sets of 30 seconds

🧘 Cool Down (5 minutes)
• Stretching exercises
• Deep breathing

Remember to stay hydrated and listen to your body!"""
            }
            userMessage.contains("diet", ignoreCase = true) ||
            userMessage.contains("food", ignoreCase = true) ||
            userMessage.contains("meal", ignoreCase = true) -> {
                """Here's a healthy diet plan:

🌅 Breakfast
• Oatmeal with fruits and nuts
• Green tea

🌞 Lunch
• Grilled chicken or tofu
• Brown rice
• Mixed vegetables

🌙 Dinner
• Fish or lean protein
• Quinoa
• Salad

🍎 Snacks
• Fresh fruits
• Nuts and seeds
• Greek yogurt

Drink 8-10 glasses of water daily!"""
            }
            userMessage.contains("mental", ignoreCase = true) ||
            userMessage.contains("stress", ignoreCase = true) ||
            userMessage.contains("anxiety", ignoreCase = true) -> {
                """Mental Health Support:

🧘 Relaxation Techniques
• Deep breathing exercises
• Progressive muscle relaxation
• Meditation (10-15 minutes daily)

💆 Self-Care Tips
• Get adequate sleep (7-9 hours)
• Practice mindfulness
• Connect with loved ones
• Limit social media

📱 Professional Help
If you're feeling overwhelmed, consider speaking with a mental health professional.

You're not alone in this journey! 💙"""
            }
            userMessage.contains("sleep", ignoreCase = true) -> {
                """Sleep Improvement Tips:

😴 Better Sleep Habits
• Maintain consistent sleep schedule
• Create a relaxing bedtime routine
• Keep bedroom cool and dark
• Avoid screens 1 hour before bed
• Limit caffeine after 2 PM

🌙 Sleep Quality
• Aim for 7-9 hours
• Use comfortable bedding
• Try meditation apps
• Avoid heavy meals before bed

Sweet dreams! 🌟"""
            }
            else -> {
                """I'm here to help you with:

🩺 Health & Symptoms
💪 Fitness & Workouts
🥗 Diet & Nutrition
🧘 Mental Health
😴 Sleep Tracking
💊 Medication Reminders

How can I assist you today?"""
            }
        }

        chatMessages.add(ChatMessage(response, true))
        chatAdapter.notifyItemInserted(chatMessages.size - 1)
        rvChat.scrollToPosition(chatMessages.size - 1)
    }

    // Chat Adapter
    class ChatAdapter(private val messages: List<ChatMessage>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        companion object {
            const val VIEW_TYPE_HUMAN = 0
            const val VIEW_TYPE_AI = 1
            const val VIEW_TYPE_TYPING = 2
        }

        override fun getItemViewType(position: Int): Int {
            val message = messages[position]
            return when {
                message.isTyping -> VIEW_TYPE_TYPING
                message.isAi -> VIEW_TYPE_AI
                else -> VIEW_TYPE_HUMAN
            }
        }

        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return when (viewType) {
                VIEW_TYPE_AI -> {
                    val view = android.view.LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_ai_chat, parent, false)
                    AIViewHolder(view)
                }
                VIEW_TYPE_TYPING -> {
                    val view = android.view.LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_ai_typing, parent, false)
                    TypingViewHolder(view)
                }
                else -> {
                    val view = android.view.LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_human_chat, parent, false)
                    HumanViewHolder(view)
                }
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val message = messages[position]
            when (holder) {
                is AIViewHolder -> holder.bind(message)
                is HumanViewHolder -> holder.bind(message)
                is TypingViewHolder -> holder.startAnimation()
            }
        }

        override fun getItemCount() = messages.size

        class AIViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            private val tvAiMessage = view.findViewById<android.widget.TextView>(R.id.tvAiMessage)

            fun bind(message: ChatMessage) {
                tvAiMessage.text = message.message
            }
        }

        class HumanViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            private val tvHumanMessage = view.findViewById<android.widget.TextView>(R.id.tvHumanMessage)

            fun bind(message: ChatMessage) {
                tvHumanMessage.text = message.message
            }
        }

        class TypingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            private val dot1 = view.findViewById<View>(R.id.dot1)
            private val dot2 = view.findViewById<View>(R.id.dot2)
            private val dot3 = view.findViewById<View>(R.id.dot3)

            fun startAnimation() {
                val anim1 = android.view.animation.AnimationUtils.loadAnimation(
                    itemView.context, R.anim.typing_dot_1
                )
                val anim2 = android.view.animation.AnimationUtils.loadAnimation(
                    itemView.context, R.anim.typing_dot_2
                )
                val anim3 = android.view.animation.AnimationUtils.loadAnimation(
                    itemView.context, R.anim.typing_dot_3
                )
                
                dot1.startAnimation(anim1)
                dot2.startAnimation(anim2)
                dot3.startAnimation(anim3)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
