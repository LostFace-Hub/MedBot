package com.example.medora

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView

class GetScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_screen)

        val themeSwitch = findViewById<ImageView>(R.id.theme_switch)
        val themeSwitchCard = findViewById<CardView>(R.id.theme_switch_card)
        val getStartedButton = findViewById<CardView>(R.id.get_started_button)

        // Set the initial icon based on the current theme
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            themeSwitch.setImageResource(R.drawable.lightmode)
        } else {
            themeSwitch.setImageResource(R.drawable.darkmode)
        }

        themeSwitchCard.setOnClickListener {
            if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                themeSwitch.setImageResource(R.drawable.darkmode)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                themeSwitch.setImageResource(R.drawable.lightmode)
            }
        }

        getStartedButton.setOnClickListener {
            startActivity(Intent(this, OnboardingActivity::class.java))
        }
    }
}