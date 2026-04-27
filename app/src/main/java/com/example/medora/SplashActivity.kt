package com.example.medora

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.medora.utils.SessionManager

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            checkAuthenticationAndNavigate()
        }, 2000) // 2 second delay
    }

    private fun checkAuthenticationAndNavigate() {
        // Check if user is already logged in with valid token
        val isLoggedIn = SessionManager.isLoggedIn(this)
        val authToken = SessionManager.getAuthToken(this)

        if (isLoggedIn && !authToken.isNullOrEmpty()) {
            // User has valid session, redirect to HomeActivity
            startActivity(Intent(this, HomeActivity::class.java))
        } else {
            // No valid session, redirect to GetStarted screen
            startActivity(Intent(this, GetScreenActivity::class.java))
        }
        finish()
    }
}