package com.example.medora

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class HelpSupportActivity : AppCompatActivity() {

    private lateinit var ivBack: ImageView
    private lateinit var cardFAQ: CardView
    private lateinit var cardLiveChat: CardView
    private lateinit var cardCallUs: CardView
    private lateinit var cardEmailUs: CardView
    private lateinit var cardWhatsApp: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_support)

        initViews()
        setupClickListeners()
    }

    private fun initViews() {
        ivBack = findViewById(R.id.ivBack)
        cardFAQ = findViewById(R.id.cardFAQ)
        cardLiveChat = findViewById(R.id.cardLiveChat)
        cardCallUs = findViewById(R.id.cardCallUs)
        cardEmailUs = findViewById(R.id.cardEmailUs)
        cardWhatsApp = findViewById(R.id.cardWhatsApp)
    }

    private fun setupClickListeners() {
        ivBack.setOnClickListener { finish() }

        cardFAQ.setOnClickListener {
            Toast.makeText(this, "Opening FAQ...", Toast.LENGTH_SHORT).show()
        }

        cardLiveChat.setOnClickListener {
            Toast.makeText(this, "Connecting to support...", Toast.LENGTH_SHORT).show()
        }

        cardCallUs.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:+911800XXXXXX")
            }
            startActivity(intent)
        }

        cardEmailUs.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:support@medora.com")
                putExtra(Intent.EXTRA_SUBJECT, "Support Request")
            }
            startActivity(Intent.createChooser(intent, "Send Email"))
        }

        cardWhatsApp.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://wa.me/911800XXXXXX")
                }
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "WhatsApp not installed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
