package com.example.medora

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.example.medora.network.LoginRequest
import com.example.medora.network.RetrofitClient
import com.example.medora.utils.SessionManager
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

class SignInActivity : AppCompatActivity() {

    private lateinit var tabPhone: TextView
    private lateinit var tabEmail: TextView

    private lateinit var inputPhone: TextInputLayout
    private lateinit var inputEmail: TextInputLayout
    private lateinit var inputPassword: TextInputLayout

    private lateinit var btnLogin: CardView
    private lateinit var btnGoogle: CardView
    private lateinit var btnFacebook: CardView

    private lateinit var rememberMe: CheckBox
    private lateinit var tvSignup: TextView
    private lateinit var forgotPassword: TextView
    
    private lateinit var progressBar: ProgressBar

    private var isPhoneSelected = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Check if already logged in
        if (SessionManager.isLoggedIn(this)) {
            navigateToHome()
            return
        }
        
        setContentView(R.layout.activity_signin_form)    // your login xml file

        initViews()
        setupTabSwitch()
        setupClicks()
    }

    private fun initViews() {

        tabPhone = findViewById(R.id.tabPhone)
        tabEmail = findViewById(R.id.tabEmail)

        inputPhone = findViewById(R.id.inputPhone)
        inputEmail = findViewById(R.id.inputEmail)
        inputPassword = findViewById(R.id.inputPassword)

        btnLogin = findViewById(R.id.btnLogin)
        btnGoogle = findViewById(R.id.btnGoogle)
        btnFacebook = findViewById(R.id.btnFacebook)

        rememberMe = findViewById(R.id.checkRemember)
        tvSignup = findViewById(R.id.tvSignup)
        forgotPassword = findViewById(R.id.forgotPassword)
        
        // Add progress bar (you may need to add this to your XML layout)
        progressBar = ProgressBar(this).apply {
            visibility = View.GONE
        }
    }

    private fun setupTabSwitch() {

        tabPhone.setOnClickListener {
            selectPhoneTab()
        }

        tabEmail.setOnClickListener {
            selectEmailTab()
        }

        // default selection = Phone
        selectPhoneTab()
    }

    private fun selectPhoneTab() {
        isPhoneSelected = true

        tabPhone.setBackgroundResource(R.drawable.toggle_left_active)
        tabEmail.setBackgroundResource(R.drawable.toggle_right_default)

        tabPhone.setTextColor(resources.getColor(android.R.color.white, null))
        tabEmail.setTextColor(resources.getColor(R.color.toggle_inactive, null))

        inputPhone.visibility = android.view.View.VISIBLE
        inputEmail.visibility = android.view.View.GONE
    }

    private fun selectEmailTab() {
        isPhoneSelected = false

        tabPhone.setBackgroundResource(R.drawable.toggle_left_default)
        tabEmail.setBackgroundResource(R.drawable.toggle_right_active)

        tabPhone.setTextColor(resources.getColor(R.color.toggle_inactive, null))
        tabEmail.setTextColor(resources.getColor(android.R.color.white, null))

        inputPhone.visibility = android.view.View.GONE
        inputEmail.visibility = android.view.View.VISIBLE
    }

    private fun setupClicks() {

        btnLogin.setOnClickListener {
            performLogin()
        }

        btnGoogle.setOnClickListener {
            Toast.makeText(this, "Google Login Coming Soon", Toast.LENGTH_SHORT).show()
        }

        btnFacebook.setOnClickListener {
            Toast.makeText(this, "Facebook Login Coming Soon", Toast.LENGTH_SHORT).show()
        }

        tvSignup.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        forgotPassword.setOnClickListener {
            Toast.makeText(this, "Forgot Password Clicked", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun performLogin() {
        val password = inputPassword.editText?.text.toString().trim()
        
        val loginRequest = if (isPhoneSelected) {
            val phone = inputPhone.editText?.text.toString().trim()
            
            if (phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Enter phone and password", Toast.LENGTH_SHORT).show()
                return
            }
            
            LoginRequest(email = null, phoneNumber = phone, password = password)
        } else {
            val email = inputEmail.editText?.text.toString().trim()
            
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show()
                return
            }
            
            LoginRequest(email = email, phoneNumber = null, password = password)
        }
        
        // Show loading
        showLoading(true)
        
        // Make API call
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.getApiService().login(loginRequest)
                
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    
                    if (apiResponse.status == "success" && apiResponse.data != null) {
                        val authData = apiResponse.data
                        val user = authData.user
                        val token = authData.token
                        
                        // Save session
                        SessionManager.saveAuthToken(this@SignInActivity, token)
                        SessionManager.saveUserData(
                            this@SignInActivity,
                            userId = user.userId,
                            name = user.fullName,
                            email = user.email,
                            phone = user.phoneNumber,
                            role = user.role ?: "patient"
                        )
                        
                        showLoading(false)
                        Toast.makeText(this@SignInActivity, "Login Successful!", Toast.LENGTH_SHORT).show()
                        navigateToHome()
                    } else {
                        showLoading(false)
                        Toast.makeText(this@SignInActivity, apiResponse.message ?: "Login failed", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    showLoading(false)
                    Toast.makeText(this@SignInActivity, "Login failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                showLoading(false)
                Toast.makeText(this@SignInActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
    }
    
    private fun showLoading(show: Boolean) {
        btnLogin.isEnabled = !show
        // You can add a progress indicator in your UI
    }
    
    private fun navigateToHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}
