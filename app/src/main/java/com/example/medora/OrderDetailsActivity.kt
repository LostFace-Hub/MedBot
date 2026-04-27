package com.example.medora

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medora.network.Order
import com.example.medora.network.OrderItem
import com.example.medora.network.OrderItemDetail
import com.example.medora.network.RetrofitClient
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class OrderDetailsActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var tvOrderId: TextView
    private lateinit var tvOrderDate: TextView
    private lateinit var tvOrderStatus: TextView
    private lateinit var statusIndicator: View
    private lateinit var rvOrderItems: RecyclerView
    private lateinit var tvSubtotal: TextView
    private lateinit var tvDeliveryFee: TextView
    private lateinit var tvDiscount: TextView
    private lateinit var tvTotal: TextView
    private lateinit var tvDeliveryAddress: TextView
    private lateinit var tvPaymentMethod: TextView
    private lateinit var btnCancelOrder: TextView
    private lateinit var btnReorder: TextView
    private lateinit var btnTrackOrder: TextView

    private var orderId: String = ""
    private var currentOrder: Order? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_details)

        orderId = intent.getStringExtra("orderId") ?: ""

        initViews()
        setupListeners()
        
        if (orderId.isNotEmpty()) {
            fetchOrderDetails()
        }
    }

    private fun initViews() {
        progressBar = findViewById(R.id.progressBar)
        tvOrderId = findViewById(R.id.tvOrderId)
        tvOrderDate = findViewById(R.id.tvOrderDate)
        tvOrderStatus = findViewById(R.id.tvOrderStatus)
        statusIndicator = findViewById(R.id.statusIndicator)
        rvOrderItems = findViewById(R.id.rvOrderItems)
        tvSubtotal = findViewById(R.id.tvSubtotal)
        tvDeliveryFee = findViewById(R.id.tvDeliveryFee)
        tvDiscount = findViewById(R.id.tvDiscount)
        tvTotal = findViewById(R.id.tvTotal)
        tvDeliveryAddress = findViewById(R.id.tvDeliveryAddress)
        tvPaymentMethod = findViewById(R.id.tvPaymentMethod)
        btnCancelOrder = findViewById(R.id.btnCancelOrder)
        btnReorder = findViewById(R.id.btnReorder)
        btnTrackOrder = findViewById(R.id.btnTrackOrder)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }

    private fun setupListeners() {
        btnCancelOrder.setOnClickListener {
            showCancelConfirmation()
        }

        btnReorder.setOnClickListener {
            reorderItems()
        }

        btnTrackOrder.setOnClickListener {
            trackOrder()
        }
    }

    private fun fetchOrderDetails() {
        lifecycleScope.launch {
            try {
                progressBar.visibility = View.VISIBLE

                val response = RetrofitClient.getApiService().getOrderById(orderId)

                if (response.isSuccessful && response.body()?.status == "success") {
                    currentOrder = response.body()?.data
                    currentOrder?.let { displayOrderDetails(it) }
                } else {
                    Toast.makeText(this@OrderDetailsActivity, "Error loading order", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@OrderDetailsActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun displayOrderDetails(order: Order) {
        // Order header
        tvOrderId.text = "#${order.orderId}"
        
        // Format date
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = inputFormat.parse(order.createdAt)
            val outputFormat = SimpleDateFormat("MMM dd, yyyy 'at' h:mm a", Locale.getDefault())
            tvOrderDate.text = outputFormat.format(date ?: Date())
        } catch (e: Exception) {
            tvOrderDate.text = order.createdAt
        }

        // Status
        val statusText = order.orderStatus.capitalize()
        tvOrderStatus.text = statusText
        
        when (order.orderStatus.lowercase()) {
            "pending" -> {
                tvOrderStatus.setTextColor(android.graphics.Color.parseColor("#FF9800"))
                statusIndicator.setBackgroundColor(android.graphics.Color.parseColor("#FF9800"))
            }
            "processing" -> {
                tvOrderStatus.setTextColor(android.graphics.Color.parseColor("#2196F3"))
                statusIndicator.setBackgroundColor(android.graphics.Color.parseColor("#2196F3"))
            }
            "shipped" -> {
                tvOrderStatus.setTextColor(android.graphics.Color.parseColor("#9C27B0"))
                statusIndicator.setBackgroundColor(android.graphics.Color.parseColor("#9C27B0"))
            }
            "delivered" -> {
                tvOrderStatus.setTextColor(android.graphics.Color.parseColor("#4CAF50"))
                statusIndicator.setBackgroundColor(android.graphics.Color.parseColor("#4CAF50"))
            }
            "cancelled" -> {
                tvOrderStatus.setTextColor(android.graphics.Color.parseColor("#F44336"))
                statusIndicator.setBackgroundColor(android.graphics.Color.parseColor("#F44336"))
            }
        }

        // Order items
        val adapter = OrderItemsAdapter(order.items)
        rvOrderItems.layoutManager = LinearLayoutManager(this)
        rvOrderItems.adapter = adapter

        // Pricing
        tvSubtotal.text = "₹${String.format("%.2f", order.pricing.subtotal)}"
        tvDeliveryFee.text = "₹${String.format("%.2f", order.pricing.deliveryFee)}"
        tvDiscount.text = "- ₹${String.format("%.2f", order.pricing.discount)}"
        tvTotal.text = "₹${String.format("%.2f", order.pricing.totalAmount)}"

        // Delivery address
        val address = order.deliveryAddress
        tvDeliveryAddress.text = "${address.addressLine1}, ${address.city}, ${address.state} - ${address.pincode}"

        // Payment method
        tvPaymentMethod.text = order.payment.method.uppercase()

        // Button visibility
        if (order.orderStatus.lowercase() in listOf("pending", "processing")) {
            btnCancelOrder.visibility = View.VISIBLE
        } else {
            btnCancelOrder.visibility = View.GONE
        }

        if (order.orderStatus.lowercase() in listOf("pending", "processing", "shipped")) {
            btnTrackOrder.visibility = View.VISIBLE
        } else {
            btnTrackOrder.visibility = View.GONE
        }
    }

    private fun showCancelConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Cancel Order")
            .setMessage("Are you sure you want to cancel this order?")
            .setPositiveButton("Yes") { _, _ ->
                cancelOrder()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun cancelOrder() {
        lifecycleScope.launch {
            try {
                progressBar.visibility = View.VISIBLE

                val response = RetrofitClient.getApiService().cancelOrder(orderId)

                if (response.isSuccessful && response.body()?.status == "success") {
                    Toast.makeText(this@OrderDetailsActivity, "Order cancelled successfully", Toast.LENGTH_SHORT).show()
                    fetchOrderDetails() // Refresh order details
                } else {
                    Toast.makeText(this@OrderDetailsActivity, "Failed to cancel order", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@OrderDetailsActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun trackOrder() {
        lifecycleScope.launch {
            try {
                progressBar.visibility = View.VISIBLE

                val response = RetrofitClient.getApiService().trackOrder(orderId)

                if (response.isSuccessful && response.body()?.status == "success") {
                    val order = response.body()?.data
                    Toast.makeText(
                        this@OrderDetailsActivity,
                        "Order Status: ${order?.orderStatus}",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(this@OrderDetailsActivity, "Failed to track order", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@OrderDetailsActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun reorderItems() {
        currentOrder?.let { order ->
            // Navigate to cart with pre-filled items
            Toast.makeText(this, "Adding ${order.items.size} items to cart", Toast.LENGTH_SHORT).show()
            // TODO: Implement cart navigation with items
        }
    }

    // Adapter for Order Items
    inner class OrderItemsAdapter(
        private val items: List<OrderItemDetail>
    ) : RecyclerView.Adapter<OrderItemsAdapter.ItemViewHolder>() {

        inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvMedicineName: TextView = view.findViewById(R.id.tvMedicineName)
            val tvQuantity: TextView = view.findViewById(R.id.tvQuantity)
            val tvPrice: TextView = view.findViewById(R.id.tvPrice)
            val tvTotal: TextView = view.findViewById(R.id.tvTotal)
        }

        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ItemViewHolder {
            val view = android.view.LayoutInflater.from(parent.context)
                .inflate(R.layout.item_order_medicine, parent, false)
            return ItemViewHolder(view)
        }

        override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            val item = items[position]

            holder.tvMedicineName.text = item.medicineName
            holder.tvQuantity.text = "Qty: ${item.quantity}"
            holder.tvPrice.text = "₹${String.format("%.2f", item.price)}"
            holder.tvTotal.text = "₹${String.format("%.2f", item.price * item.quantity)}"
        }

        override fun getItemCount() = items.size
    }
}
