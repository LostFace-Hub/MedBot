package com.example.medora

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medora.network.*
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class OrderSummaryActivity : AppCompatActivity() {

    private lateinit var rvOrderItems: RecyclerView
    private lateinit var tvRecipientName: TextView
    private lateinit var tvDeliveryAddress: TextView
    private lateinit var tvDeliveryPhone: TextView
    private lateinit var tvItemCountLabel: TextView
    private lateinit var tvMrpAmount: TextView
    private lateinit var tvDiscountAmount: TextView
    private lateinit var tvDeliveryFee: TextView
    private lateinit var tvTotalAmount: TextView
    private lateinit var tvBottomTotalAmount: TextView
    private lateinit var tvSavingsBanner: TextView
    private lateinit var btnContinue: MaterialButton
    private lateinit var tvChangeAddress: TextView
    private lateinit var progressBar: ProgressBar
    
    private var cartItems: ArrayList<CartItem>? = null
    private var totalAmount: Double = 0.0
    private var savings: Double = 0.0
    private var deliveryAddress: String = ""
    private var recipientName: String = ""
    private var addressId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_summary)

        cartItems = intent.getParcelableArrayListExtra("cartItems")
        totalAmount = intent.getDoubleExtra("totalAmount", 0.0)
        savings = intent.getDoubleExtra("savings", 0.0)
        deliveryAddress = intent.getStringExtra("selectedAddress") ?: ""
        recipientName = intent.getStringExtra("recipientName") ?: ""

        initViews()
        setupRecyclerView()
        displayOrderSummary()
        setupListeners()
    }

    private fun initViews() {
        rvOrderItems = findViewById(R.id.rvOrderItems)
        tvRecipientName = findViewById(R.id.tvRecipientName)
        tvDeliveryAddress = findViewById(R.id.tvDeliveryAddress)
        tvDeliveryPhone = findViewById(R.id.tvDeliveryPhone)
        tvItemCountLabel = findViewById(R.id.tvItemCountLabel)
        tvMrpAmount = findViewById(R.id.tvMrpAmount)
        tvDiscountAmount = findViewById(R.id.tvDiscountAmount)
        tvDeliveryFee = findViewById(R.id.tvDeliveryFee)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)
        tvBottomTotalAmount = findViewById(R.id.tvBottomTotalAmount)
        tvSavingsBanner = findViewById(R.id.tvSavingsBanner)
        btnContinue = findViewById(R.id.btnContinue)
        tvChangeAddress = findViewById(R.id.tvChangeAddress)
    }

    private fun setupRecyclerView() {
        val adapter = OrderSummaryAdapter(cartItems ?: emptyList())
        rvOrderItems.layoutManager = LinearLayoutManager(this)
        rvOrderItems.adapter = adapter
    }

    private fun displayOrderSummary() {
        // Display address
        tvRecipientName.text = recipientName
        tvDeliveryAddress.text = deliveryAddress
        tvDeliveryPhone.text = "+91 7017055446" // Should come from selected address

        // Calculate totals
        val items = cartItems ?: emptyList()
        val itemCount = items.sumOf { it.quantity }
        val mrpTotal = items.sumOf { (it.originalPrice.takeIf { p -> p > 0.0 } ?: it.price) * it.quantity }
        val deliveryFee = 0.0 // Free delivery

        tvItemCountLabel.text = "MRP ($itemCount item${if (itemCount != 1) "s" else ""})"
        tvMrpAmount.text = "₹${String.format("%.2f", mrpTotal)}"
        tvDiscountAmount.text = "-₹${String.format("%.2f", savings)}"
        tvDeliveryFee.text = if (deliveryFee == 0.0) "₹0" else "₹${String.format("%.2f", deliveryFee)}"
        tvTotalAmount.text = "₹${String.format("%.2f", totalAmount)}"
        tvBottomTotalAmount.text = "₹${String.format("%.2f", totalAmount)}"
        
        if (savings > 0) {
            tvSavingsBanner.visibility = View.VISIBLE
            tvSavingsBanner.text = "You are saving ₹${String.format("%.2f", savings)} on this order"
        } else {
            tvSavingsBanner.visibility = View.GONE
        }
    }

    private fun setupListeners() {
        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        tvChangeAddress.setOnClickListener {
            finish() // Go back to address selection
        }

        btnContinue.setOnClickListener {
            createOrder()
        }
    }

    private fun createOrder() {
        if (addressId.isEmpty()) {
            Toast.makeText(this, "Please select a delivery address", Toast.LENGTH_SHORT).show()
            return
        }

        if (cartItems.isNullOrEmpty()) {
            Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                progressBar.visibility = View.VISIBLE
                btnContinue.isEnabled = false

                // Convert CartItems to OrderItems
                val orderItems = cartItems!!.map { cartItem ->
                    OrderItem(
                        medicineId = cartItem.medicineId,
                        quantity = cartItem.quantity,
                        price = cartItem.price
                    )
                }

                // Create order request
                // For now using placeholder address - you should fetch actual address from API
                val shippingAddress = com.example.medora.network.Address(
                    type = "home",
                    fullName = recipientName.ifEmpty { "Customer" },
                    phoneNumber = "1234567890",
                    addressLine1 = deliveryAddress.ifEmpty { "Default Address" },
                    addressLine2 = null,
                    city = "Bangalore",
                    state = "Karnataka",
                    pincode = "560001",
                    isDefault = false
                )
                
                val orderRequest = CreateOrderRequest(
                    items = orderItems,
                    shippingAddress = shippingAddress,
                    totalAmount = totalAmount,
                    paymentMethod = "card" // Default to card payment
                )

                val response = RetrofitClient.getApiService().createOrder(orderRequest)

                if (response.isSuccessful && response.body()?.status == "success") {
                    val order = response.body()?.data
                    
                    Toast.makeText(
                        this@OrderSummaryActivity,
                        "Order placed successfully! Order ID: ${order?.orderId}",
                        Toast.LENGTH_LONG
                    ).show()

                    // Navigate to My Orders page
                    val intent = Intent(this@OrderSummaryActivity, MyOrdersActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        this@OrderSummaryActivity,
                        "Failed to create order",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    this@OrderSummaryActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                progressBar.visibility = View.GONE
                btnContinue.isEnabled = true
            }
        }
    }

    inner class OrderSummaryAdapter(
        private val items: List<CartItem>
    ) : RecyclerView.Adapter<OrderSummaryAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvMedicineName: TextView = view.findViewById(R.id.tvMedicineName)
            val tvPrice: TextView = view.findViewById(R.id.tvPrice)
            val tvQuantity: TextView = view.findViewById(R.id.tvQuantity)
            val ivMedicineImage: ImageView = view.findViewById(R.id.ivMedicineImage)
        }

        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
            val view = android.view.LayoutInflater.from(parent.context)
                .inflate(R.layout.item_order_summary, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            
            holder.tvMedicineName.text = item.name
            holder.tvPrice.text = "₹${String.format("%.2f", item.price)}"
            holder.tvQuantity.text = "Qty: ${item.quantity}"
        }

        override fun getItemCount() = items.size
    }
}
