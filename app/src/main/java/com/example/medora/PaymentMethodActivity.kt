package com.example.medora

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class PaymentMethodActivity : AppCompatActivity() {

    private lateinit var rgUPI: RadioGroup
    private lateinit var rgCards: RadioGroup
    private lateinit var rgWallets: RadioGroup
    private lateinit var rgCOD: RadioGroup
    private lateinit var tvBottomTotalAmount: TextView
    private lateinit var btnPlaceOrder: MaterialButton

    private var cartItems: ArrayList<CartItem>? = null
    private var totalAmount: Double = 0.0
    private var savings: Double = 0.0
    private var deliveryAddress: String = ""
    private var recipientName: String = ""
    private var selectedPaymentMethod: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_method)

        cartItems = intent.getParcelableArrayListExtra("cartItems")
        totalAmount = intent.getDoubleExtra("totalAmount", 0.0)
        savings = intent.getDoubleExtra("savings", 0.0)
        deliveryAddress = intent.getStringExtra("selectedAddress") ?: ""
        recipientName = intent.getStringExtra("recipientName") ?: ""

        initViews()
        displayTotalAmount()
        setupRadioGroups()
        setupListeners()
    }

    private fun initViews() {
        rgUPI = findViewById(R.id.rgUPI)
        rgCards = findViewById(R.id.rgCards)
        rgWallets = findViewById(R.id.rgWallets)
        rgCOD = findViewById(R.id.rgCOD)
        tvBottomTotalAmount = findViewById(R.id.tvBottomTotalAmount)
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)
    }

    private fun displayTotalAmount() {
        tvBottomTotalAmount.text = "₹${String.format("%.2f", totalAmount)}"
    }

    private fun setupRadioGroups() {
        val radioGroups = listOf(rgUPI, rgCards, rgWallets, rgCOD)

        radioGroups.forEach { group ->
            group.setOnCheckedChangeListener { _, checkedId ->
                if (checkedId != -1) {
                    // Clear other radio groups
                    radioGroups.forEach { otherGroup ->
                        if (otherGroup.id != group.id) {
                            otherGroup.clearCheck()
                        }
                    }

                    // Get selected payment method
                    val radioButton = findViewById<RadioButton>(checkedId)
                    selectedPaymentMethod = radioButton.text.toString()
                    btnPlaceOrder.isEnabled = true
                }
            }
        }
    }

    private fun setupListeners() {
        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        btnPlaceOrder.setOnClickListener {
            placeOrder()
        }
    }

    private fun placeOrder() {
        // Show order confirmation dialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Order Placed Successfully!")
        builder.setMessage("Your order has been placed successfully.\n\n" +
                "Order Details:\n" +
                "Items: ${cartItems?.size ?: 0}\n" +
                "Total: ₹${String.format("%.2f", totalAmount)}\n" +
                "Payment: $selectedPaymentMethod\n" +
                "Delivery Address: $deliveryAddress\n\n" +
                "You will receive a confirmation shortly.")
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
            // Navigate back to home or orders
            finish()
        }
        builder.setCancelable(false)
        builder.show()
    }
}
