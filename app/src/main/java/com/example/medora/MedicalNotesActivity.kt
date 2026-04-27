package com.example.medora

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MedicalNotesActivity : AppCompatActivity() {

    private lateinit var notesAdapter: MedicalNotesAdapter
    private val allNotes = mutableListOf<MedicalNote>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medical_notes)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        findViewById<ImageView>(R.id.btnAddNote).setOnClickListener {
            Toast.makeText(this, "Add new note", Toast.LENGTH_SHORT).show()
        }

        setupNotes()
        setupSearch()
        setupBottomNavigation()
    }

    private fun setupNotes() {
        // Sample data
        allNotes.addAll(
            listOf(
                MedicalNote(
                    1,
                    "Consultation Notes",
                    "Dr. Sarah Johnson",
                    "Nov 15, 2025",
                    "Patient shows improvement. Blood pressure is stable. Continue current medication. Follow-up in 2 weeks.",
                    "Cardiology"
                ),
                MedicalNote(
                    2,
                    "Lab Results",
                    "Dr. Neha Kapoor",
                    "Nov 10, 2025",
                    "Blood test results are within normal range. Cholesterol levels have improved significantly.",
                    "General"
                ),
                MedicalNote(
                    3,
                    "Prescription Update",
                    "Dr. Sarah Johnson",
                    "Nov 5, 2025",
                    "Updated medication dosage. New prescription for blood pressure management.",
                    "Cardiology"
                ),
                MedicalNote(
                    4,
                    "Physical Examination",
                    "Dr. Anirudh Sinha",
                    "Oct 28, 2025",
                    "Regular checkup completed. No major concerns. Advised to maintain healthy lifestyle.",
                    "Orthopedics"
                )
            )
        )

        val rvNotes = findViewById<RecyclerView>(R.id.rvNotes)
        rvNotes.layoutManager = LinearLayoutManager(this)
        notesAdapter = MedicalNotesAdapter(this, allNotes) { note ->
            Toast.makeText(this, "Viewing: ${note.title}", Toast.LENGTH_SHORT).show()
        }
        rvNotes.adapter = notesAdapter
    }

    private fun setupSearch() {
        val etSearch = findViewById<EditText>(R.id.etSearch)
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().toLowerCase()
                val filtered = allNotes.filter {
                    it.title.toLowerCase().contains(query) ||
                            it.doctor.toLowerCase().contains(query) ||
                            it.content.toLowerCase().contains(query)
                }
                notesAdapter = MedicalNotesAdapter(this@MedicalNotesActivity, filtered) { note ->
                    Toast.makeText(this@MedicalNotesActivity, "Viewing: ${note.title}", Toast.LENGTH_SHORT).show()
                }
                findViewById<RecyclerView>(R.id.rvNotes).adapter = notesAdapter
            }
        })
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
