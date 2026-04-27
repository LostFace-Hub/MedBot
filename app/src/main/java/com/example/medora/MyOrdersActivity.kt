package com.example.medora

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medora.network.Order
import com.example.medora.network.RetrofitClient
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MyOrdersActivity : AppCompatActivity() {

    private lateinit var rvOrders: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyStateLayout: LinearLayout
    private lateinit var chipAll: Chip
    private lateinit var chipPending: Chip
    private lateinit var chipProcessing: Chip
    private lateinit var chipShipped: Chip
    private lateinit var chipDelivered: Chip
    private lateinit var chipCancelled: Chip
    private lateinit var tvOrderCount: TextView

    private val ordersList = mutableListOf<Order>()
    private lateinit var ordersAdapter: OrdersAdapter
    private var selectedStatus: String = "all"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_orders)

        initViews()
        setupRecyclerView()
        setupFilterChips()
        setupListeners()
        
        // Fetch orders from backend
        fetchOrders()
    }

    private fun initViews() {
        rvOrders = findViewById(R.id.rvOrders)
        progressBar = findViewById(R.id.progressBar)
        emptyStateLayout = findViewById(R.id.emptyStateLayout)
        chipAll = findViewById(R.id.chipAll)
        chipPending = findViewById(R.id.chipPending)
        chipProcessing = findViewById(R.id.chipProcessing)
        chipShipped = findViewById(R.id.chipShipped)
        chipDelivered = findViewById(R.id.chipDelivered)
        chipCancelled = findViewById(R.id.chipCancelled)
        tvOrderCount = findViewById(R.id.tvOrderCount)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        ordersAdapter = OrdersAdapter(ordersList) { order ->
            openOrderDetails(order)
        }
        rvOrders.layoutManager = LinearLayoutManager(this)
        rvOrders.adapter = ordersAdapter
    }

    private fun setupFilterChips() {
        chipAll.setOnClickListener { filterOrders("all") }
        chipPending.setOnClickListener { filterOrders("pending") }
        chipProcessing.setOnClickListener { filterOrders("processing") }
        chipShipped.setOnClickListener { filterOrders("shipped") }
        chipDelivered.setOnClickListener { filterOrders("delivered") }
        chipCancelled.setOnClickListener { filterOrders("cancelled") }
        
        // Set initial selection
        chipAll.isChecked = true
    }

    private fun setupListeners() {
        findViewById<TextView>(R.id.tvViewAll).setOnClickListener {
            filterOrders("all")
        }
    }

    private fun filterOrders(status: String) {
        selectedStatus = status
        
        // Update chip selection
        chipAll.isChecked = status == "all"
        chipPending.isChecked = status == "pending"
        chipProcessing.isChecked = status == "processing"
        chipShipped.isChecked = status == "shipped"
        chipDelivered.isChecked = status == "delivered"
        chipCancelled.isChecked = status == "cancelled"
        
        // Fetch filtered orders
        fetchOrders(status)
    }

    private fun fetchOrders(status: String? = null) {
        lifecycleScope.launch {
            try {
                progressBar.visibility = View.VISIBLE
                emptyStateLayout.visibility = View.GONE
                
                val response = RetrofitClient.getApiService().getOrders(
                    page = 1,
                    limit = 50,
                    status = if (status == "all") null else status
                )

                if (response.isSuccessful && response.body()?.status == "success") {
                    val orders = response.body()?.data?.orders
                    
                    if (!orders.isNullOrEmpty()) {
                        ordersList.clear()
                        ordersList.addAll(orders)
                        ordersAdapter.notifyDataSetChanged()
                        
                        rvOrders.visibility = View.VISIBLE
                        emptyStateLayout.visibility = View.GONE
                        tvOrderCount.text = "${orders.size} Orders"
                    } else {
                        rvOrders.visibility = View.GONE
                        emptyStateLayout.visibility = View.VISIBLE
                        tvOrderCount.text = "0 Orders"
                    }
                } else {
                    Toast.makeText(this@MyOrdersActivity, "Error loading orders", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@MyOrdersActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun openOrderDetails(order: Order) {
        val intent = Intent(this, OrderDetailsActivity::class.java)
        intent.putExtra("orderId", order.id)
        startActivity(intent)
    }

    // Adapter for Orders
    inner class OrdersAdapter(
        private val orders: List<Order>,
        private val onOrderClick: (Order) -> Unit
    ) : RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {

        inner class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvOrderId: TextView = view.findViewById(R.id.tvOrderId)
            val tvOrderDate: TextView = view.findViewById(R.id.tvOrderDate)
            val tvOrderStatus: TextView = view.findViewById(R.id.tvOrderStatus)
            val tvOrderAmount: TextView = view.findViewById(R.id.tvOrderAmount)
            val tvItemCount: TextView = view.findViewById(R.id.tvItemCount)
            val tvPaymentMethod: TextView = view.findViewById(R.id.tvPaymentMethod)
            val btnTrackOrder: TextView = view.findViewById(R.id.btnTrackOrder)
            val statusIndicator: View = view.findViewById(R.id.statusIndicator)
        }

        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): OrderViewHolder {
            val view = android.view.LayoutInflater.from(parent.context)
                .inflate(R.layout.item_order, parent, false)
            return OrderViewHolder(view)
        }

        override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
            val order = orders[position]

            holder.tvOrderId.text = "#${order.orderId}"
            
            // Format date
            try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                inputFormat.timeZone = TimeZone.getTimeZone("UTC")
                val date = inputFormat.parse(order.createdAt)
                val outputFormat = SimpleDateFormat("MMM dd, yyyy 'at' h:mm a", Locale.getDefault())
                holder.tvOrderDate.text = outputFormat.format(date ?: Date())
            } catch (e: Exception) {
                holder.tvOrderDate.text = order.createdAt
            }

            // Status with color coding
            val statusText = order.orderStatus.capitalize()
            holder.tvOrderStatus.text = statusText
            
            when (order.orderStatus.lowercase()) {
                "pending" -> {
                    holder.tvOrderStatus.setTextColor(android.graphics.Color.parseColor("#FF9800"))
                    holder.statusIndicator.setBackgroundColor(android.graphics.Color.parseColor("#FF9800"))
                }
                "processing" -> {
                    holder.tvOrderStatus.setTextColor(android.graphics.Color.parseColor("#2196F3"))
                    holder.statusIndicator.setBackgroundColor(android.graphics.Color.parseColor("#2196F3"))
                }
                "shipped" -> {
                    holder.tvOrderStatus.setTextColor(android.graphics.Color.parseColor("#9C27B0"))
                    holder.statusIndicator.setBackgroundColor(android.graphics.Color.parseColor("#9C27B0"))
                }
                "delivered" -> {
                    holder.tvOrderStatus.setTextColor(android.graphics.Color.parseColor("#4CAF50"))
                    holder.statusIndicator.setBackgroundColor(android.graphics.Color.parseColor("#4CAF50"))
                }
                "cancelled" -> {
                    holder.tvOrderStatus.setTextColor(android.graphics.Color.parseColor("#F44336"))
                    holder.statusIndicator.setBackgroundColor(android.graphics.Color.parseColor("#F44336"))
                }
                else -> {
                    holder.tvOrderStatus.setTextColor(android.graphics.Color.parseColor("#666666"))
                    holder.statusIndicator.setBackgroundColor(android.graphics.Color.parseColor("#666666"))
                }
            }

            holder.tvOrderAmount.text = "₹${String.format("%.2f", order.pricing.totalAmount)}"
            holder.tvItemCount.text = "${order.items.size} ${if (order.items.size == 1) "item" else "items"}"
            holder.tvPaymentMethod.text = order.payment.method.uppercase()

            // Track order button visibility
            if (order.orderStatus.lowercase() in listOf("pending", "processing", "shipped")) {
                holder.btnTrackOrder.visibility = View.VISIBLE
                holder.btnTrackOrder.setOnClickListener {
                    trackOrder(order)
                }
            } else {
                holder.btnTrackOrder.visibility = View.GONE
            }

            holder.itemView.setOnClickListener {
                onOrderClick(order)
            }
        }

        override fun getItemCount() = orders.size

        private fun trackOrder(order: Order) {
            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.getApiService().trackOrder(order.id)
                    if (response.isSuccessful && response.body()?.status == "success") {
                        val trackedOrder = response.body()?.data
                        Toast.makeText(
                            this@MyOrdersActivity,
                            "Order Status: ${trackedOrder?.orderStatus}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this@MyOrdersActivity, "Error tracking order", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
