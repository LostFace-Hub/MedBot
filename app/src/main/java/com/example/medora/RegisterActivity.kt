package com.example.medora

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.example.medora.network.RegisterRequest
import com.example.medora.network.RetrofitClient
import com.example.medora.utils.SessionManager
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import java.util.Calendar

class RegisterActivity : AppCompatActivity() {

    private lateinit var tabSignUp: TextView
    private lateinit var tabLogin: TextView

    private lateinit var inputFirstName: TextInputLayout
    private lateinit var inputLastName: TextInputLayout
    private lateinit var inputEmail: TextInputLayout
    private lateinit var inputDob: TextInputLayout
    private lateinit var inputNumber: TextInputLayout
    private lateinit var inputPassword: TextInputLayout

    private lateinit var btnRegister: CardView
    private lateinit var btnGoogle: CardView
    private lateinit var btnFacebook: CardView
    private lateinit var tvLogin: TextView

    private var isSignUpSelected = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_form)

        initViews()
        setupTabs()
        setupDOBPicker()
        setupClicks()
    }

    private fun initViews() {

        tabSignUp = findViewById(R.id.tabSignUp)
        tabLogin = findViewById(R.id.tabLogin)

        inputFirstName = findViewById(R.id.inputFirstName)
        inputLastName = findViewById(R.id.inputLastName)
        inputEmail = findViewById(R.id.inputEmail)
        inputDob = findViewById(R.id.inputDob)
        inputNumber = findViewById(R.id.inputNumber)
        inputPassword = findViewById(R.id.inputPassword)

        btnRegister = findViewById(R.id.btnRegister)
        btnGoogle = findViewById(R.id.btnGoogle)
        btnFacebook = findViewById(R.id.btnFacebook)
        tvLogin = findViewById(R.id.tvLogin)
    }

    private fun setupTabs() {

        tabSignUp.setOnClickListener {
            selectSignUpTab()
        }

        tabLogin.setOnClickListener {
            selectLoginTab()
        }

        // default selection: SignUp
        selectSignUpTab()
    }

    private fun selectSignUpTab() {
        isSignUpSelected = true

        tabSignUp.setBackgroundResource(R.drawable.toggle_left_active)
        tabLogin.setBackgroundResource(R.drawable.toggle_right_default)

        tabSignUp.setTextColor(resources.getColor(android.R.color.white, null))
        tabLogin.setTextColor(resources.getColor(R.color.toggle_inactive, null))
    }

    private fun selectLoginTab() {
        isSignUpSelected = false

        tabSignUp.setBackgroundResource(R.drawable.toggle_left_default)
        tabLogin.setBackgroundResource(R.drawable.toggle_right_active)

        tabSignUp.setTextColor(resources.getColor(R.color.toggle_inactive, null))
        tabLogin.setTextColor(resources.getColor(android.R.color.white, null))

        startActivity(Intent(this, SignInActivity::class.java))
        finish()
    }

    private fun setupDOBPicker() {

        inputDob.editText?.setOnClickListener {
            showDatePicker()
        }

        inputDob.setEndIconOnClickListener {
            showDatePicker()
        }
    }

    private fun showDatePicker() {

        val calendar = Calendar.getInstance()

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val dialog = DatePickerDialog(
            this,
            { _, y, m, d ->
                val date = "$d/${m + 1}/$y"
                inputDob.editText?.setText(date)
            },
            year, month, day
        )

        dialog.show()
    }

    private fun setupClicks() {

        btnRegister.setOnClickListener {
            performRegistration()
        }

        btnGoogle.setOnClickListener {
            Toast.makeText(this, "Google Sign Up Coming Soon", Toast.LENGTH_SHORT).show()
        }

        btnFacebook.setOnClickListener {
            Toast.makeText(this, "Facebook Sign Up Coming Soon", Toast.LENGTH_SHORT).show()
        }

        tvLogin.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }
    }
    
    private fun performRegistration() {
        val firstName = inputFirstName.editText?.text.toString().trim()
        val lastName = inputLastName.editText?.text.toString().trim()
        val email = inputEmail.editText?.text.toString().trim()
        val dob = inputDob.editText?.text.toString().trim()
        val phoneNumber = inputNumber.editText?.text.toString().trim()
        val password = inputPassword.editText?.text.toString().trim()

        if (firstName.isEmpty() ||
            lastName.isEmpty() ||
            email.isEmpty() ||
            dob.isEmpty() ||
            phoneNumber.isEmpty() ||
            password.isEmpty()
        ) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Validate email
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Validate phone number (10 digits)
        if (phoneNumber.length != 10) {
            Toast.makeText(this, "Please enter a valid 10-digit phone number", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Validate password (min 6 characters)
        if (password.length < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            return
        }
        
        val fullName = "$firstName $lastName"
        val registerRequest = RegisterRequest(
            fullName = fullName,
            email = email,
            phoneNumber = phoneNumber,
            password = password,
            role = "patient"
        )
        
        // Show loading
        btnRegister.isEnabled = false
        
        // Make API call
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.getApiService().register(registerRequest)
                
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    
                    if (apiResponse.status == "success" && apiResponse.data != null) {
                        val authData = apiResponse.data
                        val user = authData.user
                        val token = authData.token
                        
                        // Save session
                        SessionManager.saveAuthToken(this@RegisterActivity, token)
                        SessionManager.saveUserData(
                            this@RegisterActivity,
                            userId = user.userId,
                            name = user.fullName,
                            email = user.email,
                            phone = user.phoneNumber,
                            role = user.role ?: "patient"
                        )
                        
                        btnRegister.isEnabled = true
                        Toast.makeText(this@RegisterActivity, "Registration Successful!", Toast.LENGTH_SHORT).show()
                        
                        // Navigate to Home
                        startActivity(Intent(this@RegisterActivity, HomeActivity::class.java))
                        finish()
                    } else {
                        btnRegister.isEnabled = true
                        Toast.makeText(this@RegisterActivity, apiResponse.message ?: "Registration failed", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    btnRegister.isEnabled = true
                    val errorBody = response.errorBody()?.string()
                    Toast.makeText(this@RegisterActivity, "Registration failed: $errorBody", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                btnRegister.isEnabled = true
                Toast.makeText(this@RegisterActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
    }
}
