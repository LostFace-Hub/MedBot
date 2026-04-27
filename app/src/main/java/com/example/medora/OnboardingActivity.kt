package com.example.medora

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.medora.Adapter.OnboardingAdapter
import com.example.medora.model.OnboardingModel

class OnboardingActivity : AppCompatActivity() {

    private lateinit var pager: ViewPager2
    private lateinit var dotsLayout: LinearLayout
    private lateinit var btnRegister: CardView
    private lateinit var btnSignIn: CardView
    private lateinit var btnSkip: TextView

    private val list = listOf(
        OnboardingModel(
            R.drawable.onboard1,
            "Smart Health Tracking",
            "Monitor Vitals and Activity",
            "Display health insights, vitals, and daily activity patterns with clean visuals."
        ),
        OnboardingModel(
            R.drawable.onboard2,
            "Easy Appointment Booking",
            "Schedule with your doctor",
            "Manage reminders, sync appointments and receive instant alerts."
        ),
        OnboardingModel(
            R.drawable.onboard3,
            "Prescription Management",
            "Store and Track Medications",
            "Track medicine schedules and get dosage reminders automatically."
        ),
        OnboardingModel(
            R.drawable.onboard4,
            "Emergency Services",
            "Quick-Access SOS Button",
            "Auto-detect location and alert emergency responders swiftly."
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        pager = findViewById(R.id.onboardPager)
        dotsLayout = findViewById(R.id.dots)
        btnRegister = findViewById(R.id.btnRegister)
        btnSignIn = findViewById(R.id.btnSignIn)
        btnSkip = findViewById(R.id.btnSkip)

        pager.adapter = OnboardingAdapter(list)
        updateDots(0)

        pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateDots(position)
            }
        })

        btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        btnSignIn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        btnSkip.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }
    }

    private fun updateDots(position: Int) {
        dotsLayout.removeAllViews()

        for (i in list.indices) {
            val dot = View(this)
            val size = 14

            dot.layoutParams = LinearLayout.LayoutParams(size, size).apply {
                setMargins(6, 0, 6, 0)
            }
            dot.background = ContextCompat.getDrawable(
                this,
                if (i == position) R.drawable.dot_active else R.drawable.dot_inactive
            )

            dotsLayout.addView(dot)
        }
    }
}