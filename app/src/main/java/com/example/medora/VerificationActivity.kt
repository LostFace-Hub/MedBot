package com.example.medora

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class VerificationActivity : AppCompatActivity() {

    private lateinit var otp1: EditText
    private lateinit var otp2: EditText
    private lateinit var otp3: EditText
    private lateinit var otp4: EditText

    private lateinit var btnConfirm: Button
    private lateinit var tvResend: TextView
    private lateinit var btnBack: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification)

        initViews()
        setupOTPInputs()
        setupClicks()
    }

    private fun initViews() {
        otp1 = findViewById(R.id.otp1)
        otp2 = findViewById(R.id.otp2)
        otp3 = findViewById(R.id.otp3)
        otp4 = findViewById(R.id.otp4)

        btnConfirm = findViewById(R.id.btnConfirm)
        tvResend = findViewById(R.id.tvResend)
        btnBack = findViewById(R.id.btnBack)
    }

    private fun setupOTPInputs() {

        val inputs = listOf(otp1, otp2, otp3, otp4)

        // Auto Focus Logic
        for (i in inputs.indices) {
            inputs[i].addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1 && i < inputs.size - 1) {
                        inputs[i + 1].requestFocus()
                        inputs[i].background = getDrawable(R.drawable.otp_circle_filled)
                    } else if (s?.isEmpty() == true && i > 0) {
                        inputs[i - 1].requestFocus()
                        inputs[i].background = getDrawable(R.drawable.otp_circle_default)
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }
    }

    private fun setupClicks() {

        btnConfirm.setOnClickListener {
            val code = otp1.text.toString() +
                    otp2.text.toString() +
                    otp3.text.toString() +
                    otp4.text.toString()

            if (code.length < 4) {
                Toast.makeText(this, "Enter full OTP", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(this, "Verifying OTP: $code", Toast.LENGTH_SHORT).show()
        }

        tvResend.setOnClickListener {
            Toast.makeText(this, "OTP Resent", Toast.LENGTH_SHORT).show()
        }

        btnBack.setOnClickListener {
            finish()
        }
    }
}
