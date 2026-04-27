package com.example.medora

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class AddAddressActivity : AppCompatActivity() {

    private lateinit var tilFullName: TextInputLayout
    private lateinit var tilPhone: TextInputLayout
    private lateinit var tilHouseNo: TextInputLayout
    private lateinit var tilStreet: TextInputLayout
    private lateinit var tilCity: TextInputLayout
    private lateinit var tilPincode: TextInputLayout
    private lateinit var tilState: TextInputLayout

    private lateinit var etFullName: TextInputEditText
    private lateinit var etPhone: TextInputEditText
    private lateinit var etHouseNo: TextInputEditText
    private lateinit var etStreet: TextInputEditText
    private lateinit var etCity: TextInputEditText
    private lateinit var etPincode: TextInputEditText
    private lateinit var etState: TextInputEditText

    private lateinit var chipHome: MaterialCardView
    private lateinit var chipWork: MaterialCardView
    private lateinit var chipOther: MaterialCardView
    private lateinit var tvHome: TextView
    private lateinit var tvWork: TextView
    private lateinit var tvOther: TextView

    private lateinit var cbDefaultAddress: MaterialCheckBox
    private lateinit var btnSaveAddress: MaterialButton

    private var selectedAddressType = "Home"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_address)

        initViews()
        setupListeners()
        setupAddressTypeChips()
    }

    private fun initViews() {
        // TextInputLayouts
        tilFullName = findViewById(R.id.tilFullName)
        tilPhone = findViewById(R.id.tilPhone)
        tilHouseNo = findViewById(R.id.tilHouseNo)
        tilStreet = findViewById(R.id.tilStreet)
        tilCity = findViewById(R.id.tilCity)
        tilPincode = findViewById(R.id.tilPincode)
        tilState = findViewById(R.id.tilState)

        // EditTexts
        etFullName = findViewById(R.id.etFullName)
        etPhone = findViewById(R.id.etPhone)
        etHouseNo = findViewById(R.id.etHouseNo)
        etStreet = findViewById(R.id.etStreet)
        etCity = findViewById(R.id.etCity)
        etPincode = findViewById(R.id.etPincode)
        etState = findViewById(R.id.etState)

        // Address Type Chips
        chipHome = findViewById(R.id.chipHome)
        chipWork = findViewById(R.id.chipWork)
        chipOther = findViewById(R.id.chipOther)
        tvHome = findViewById(R.id.tvHome)
        tvWork = findViewById(R.id.tvWork)
        tvOther = findViewById(R.id.tvOther)

        // Other views
        cbDefaultAddress = findViewById(R.id.cbDefaultAddress)
        btnSaveAddress = findViewById(R.id.btnSaveAddress)
    }

    private fun setupListeners() {
        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        btnSaveAddress.setOnClickListener {
            saveAddress()
        }
    }

    private fun setupAddressTypeChips() {
        chipHome.setOnClickListener {
            selectAddressType("Home", chipHome, tvHome)
        }

        chipWork.setOnClickListener {
            selectAddressType("Work", chipWork, tvWork)
        }

        chipOther.setOnClickListener {
            selectAddressType("Other", chipOther, tvOther)
        }

        // Set Home as default selected
        selectAddressType("Home", chipHome, tvHome)
    }

    private fun selectAddressType(type: String, selectedChip: MaterialCardView, selectedText: TextView) {
        selectedAddressType = type

        // Reset all chips
        resetChip(chipHome, tvHome)
        resetChip(chipWork, tvWork)
        resetChip(chipOther, tvOther)

        // Highlight selected chip
        selectedChip.setCardBackgroundColor(getColor(android.R.color.transparent))
        selectedChip.setCardBackgroundColor(android.graphics.Color.parseColor("#E0F7FA"))
        selectedChip.strokeColor = android.graphics.Color.parseColor("#1BA3C4")
        selectedChip.strokeWidth = 4
        selectedText.setTextColor(android.graphics.Color.parseColor("#1BA3C4"))
        selectedText.setTypeface(null, android.graphics.Typeface.BOLD)
    }

    private fun resetChip(chip: MaterialCardView, textView: TextView) {
        chip.setCardBackgroundColor(android.graphics.Color.parseColor("#F3F4F6"))
        chip.strokeColor = android.graphics.Color.parseColor("#E5E7EB")
        chip.strokeWidth = 2
        textView.setTextColor(android.graphics.Color.parseColor("#6B7280"))
        textView.setTypeface(null, android.graphics.Typeface.NORMAL)
    }

    private fun saveAddress() {
        // Clear previous errors
        tilFullName.error = null
        tilPhone.error = null
        tilHouseNo.error = null
        tilStreet.error = null
        tilCity.error = null
        tilPincode.error = null
        tilState.error = null

        // Get input values
        val fullName = etFullName.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val houseNo = etHouseNo.text.toString().trim()
        val street = etStreet.text.toString().trim()
        val city = etCity.text.toString().trim()
        val pincode = etPincode.text.toString().trim()
        val state = etState.text.toString().trim()
        val isDefault = cbDefaultAddress.isChecked

        // Validation
        var isValid = true

        if (fullName.isEmpty()) {
            tilFullName.error = "Please enter full name"
            isValid = false
        }

        if (phone.isEmpty()) {
            tilPhone.error = "Please enter phone number"
            isValid = false
        } else if (phone.length != 10) {
            tilPhone.error = "Phone number must be 10 digits"
            isValid = false
        }

        if (houseNo.isEmpty()) {
            tilHouseNo.error = "Please enter house/flat number"
            isValid = false
        }

        if (street.isEmpty()) {
            tilStreet.error = "Please enter street/area"
            isValid = false
        }

        if (city.isEmpty()) {
            tilCity.error = "Please enter city"
            isValid = false
        }

        if (pincode.isEmpty()) {
            tilPincode.error = "Please enter pincode"
            isValid = false
        } else if (pincode.length != 6) {
            tilPincode.error = "Pincode must be 6 digits"
            isValid = false
        }

        if (state.isEmpty()) {
            tilState.error = "Please enter state"
            isValid = false
        }

        if (!isValid) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Create full address string
        val fullAddress = "$houseNo, $street, $city, $state - $pincode"

        // Save address (you can save to SharedPreferences, Room DB, or send to server)
        // For now, we'll just show a success message and return the data
        
        // Return address data to previous activity
        val resultIntent = android.content.Intent()
        resultIntent.putExtra("fullName", fullName)
        resultIntent.putExtra("phone", "+91 $phone")
        resultIntent.putExtra("address", fullAddress)
        resultIntent.putExtra("addressType", selectedAddressType)
        resultIntent.putExtra("isDefault", isDefault)
        setResult(RESULT_OK, resultIntent)

        Toast.makeText(this, "Address saved successfully!", Toast.LENGTH_SHORT).show()
        finish()
    }

    // Optional: Load existing address for editing
    private fun loadAddress() {
        // If editing existing address, load data here
        val addressId = intent.getIntExtra("addressId", -1)
        if (addressId != -1) {
            // Load address from database/preferences
            // etFullName.setText(address.fullName)
            // etc...
        }
    }
}
