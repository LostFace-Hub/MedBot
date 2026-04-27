package com.example.medora

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.medora.network.RetrofitClient
import com.example.medora.utils.SessionManager
import com.google.android.material.textfield.TextInputLayout
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.launch

class EditProfileActivity : AppCompatActivity() {

    private lateinit var ivBack: ImageView
    private lateinit var ivProfilePic: CircleImageView
    private lateinit var inputFullName: TextInputLayout
    private lateinit var inputEmail: TextInputLayout
    private lateinit var inputPhone: TextInputLayout
    private lateinit var inputGender: TextInputLayout
    private lateinit var inputDOB: TextInputLayout
    private lateinit var inputBloodGroup: TextInputLayout
    private lateinit var inputHeight: TextInputLayout
    private lateinit var inputWeight: TextInputLayout
    private lateinit var btnSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        initViews()
        loadUserData()
        setupClickListeners()
    }

    private fun initViews() {
        ivBack = findViewById(R.id.ivBack)
        ivProfilePic = findViewById(R.id.ivProfilePic)
        inputFullName = findViewById(R.id.inputFullName)
        inputEmail = findViewById(R.id.inputEmail)
        inputPhone = findViewById(R.id.inputPhone)
        inputGender = findViewById(R.id.inputGender)
        inputDOB = findViewById(R.id.inputDOB)
        inputBloodGroup = findViewById(R.id.inputBloodGroup)
        inputHeight = findViewById(R.id.inputHeight)
        inputWeight = findViewById(R.id.inputWeight)
        btnSave = findViewById(R.id.btnSave)
    }

    private fun loadUserData() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.getApiService().getProfile()
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    if (apiResponse.status == "success" && apiResponse.data != null) {
                        val user = apiResponse.data
                        
                        inputFullName.editText?.setText(user.fullName)
                        inputEmail.editText?.setText(user.email)
                        inputPhone.editText?.setText(user.phoneNumber)
                        inputGender.editText?.setText(user.gender ?: "")
                        inputDOB.editText?.setText(user.dateOfBirth ?: "")
                        inputBloodGroup.editText?.setText(user.bloodGroup ?: "")
                        inputHeight.editText?.setText(user.height?.toString() ?: "")
                        inputWeight.editText?.setText(user.weight?.toString() ?: "")
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@EditProfileActivity, "Error loading profile", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupClickListeners() {
        ivBack.setOnClickListener { finish() }

        btnSave.setOnClickListener {
            saveProfile()
        }
    }

    private fun saveProfile() {
        val updateData = mutableMapOf<String, Any>()
        
        inputFullName.editText?.text.toString().takeIf { it.isNotEmpty() }?.let { updateData["fullName"] = it }
        inputGender.editText?.text.toString().takeIf { it.isNotEmpty() }?.let { updateData["gender"] = it }
        inputDOB.editText?.text.toString().takeIf { it.isNotEmpty() }?.let { updateData["dateOfBirth"] = it }
        inputBloodGroup.editText?.text.toString().takeIf { it.isNotEmpty() }?.let { updateData["bloodGroup"] = it }
        inputHeight.editText?.text.toString().takeIf { it.isNotEmpty() }?.let { 
            updateData["height"] = it.toDoubleOrNull() ?: 0.0 
        }
        inputWeight.editText?.text.toString().takeIf { it.isNotEmpty() }?.let { 
            updateData["weight"] = it.toDoubleOrNull() ?: 0.0 
        }

        lifecycleScope.launch {
            try {
                btnSave.isEnabled = false
                val response = RetrofitClient.getApiService().updateProfile(updateData)
                
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    if (apiResponse.status == "success") {
                        Toast.makeText(this@EditProfileActivity, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@EditProfileActivity, apiResponse.message ?: "Update failed", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@EditProfileActivity, "Update failed", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@EditProfileActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                btnSave.isEnabled = true
            }
        }
    }
}
