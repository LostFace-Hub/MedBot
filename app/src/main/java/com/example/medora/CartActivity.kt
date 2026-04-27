package com.example.medora

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
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
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartItem(
    val medicineId: String,
    val name: String,
    val price: Double,
    val originalPrice: Double = 0.0,
    var quantity: Int = 1,
    val imageRes: Int = R.drawable.ic_medicine
) : Parcelable

class CartActivity : AppCompatActivity() {

    private lateinit var rvCartItems: RecyclerView
    private lateinit var emptyCartLayout: LinearLayout
    private lateinit var savingsBanner: LinearLayout
    private lateinit var tvCartCount: TextView
    private lateinit var tvItemCount: TextView
    private lateinit var tvTotalAmount: TextView
    private lateinit var tvSavingsText: TextView
    private lateinit var btnPlaceOrder: MaterialButton
    private lateinit var progressBar: ProgressBar
    
    private lateinit var adapter: CartItemAdapter
    private val cartItems = mutableListOf<CartItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        initViews()
        setupRecyclerView()
        loadCartItems()
        setupListeners()
        updateUI()
    }

    private fun initViews() {
        rvCartItems = findViewById(R.id.rvCartItems)
        emptyCartLayout = findViewById(R.id.emptyCartLayout)
        savingsBanner = findViewById(R.id.savingsBanner)
        tvCartCount = findViewById(R.id.tvCartCount)
        tvItemCount = findViewById(R.id.tvItemCount)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)
        tvSavingsText = findViewById(R.id.tvSavingsText)
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)
        progressBar = findViewById(R.id.progressBar) // Add to layout
    }

    private fun setupRecyclerView() {
        adapter = CartItemAdapter(cartItems,
            onQuantityChanged = { updateUI() },
            onDeleteItem = { item ->
                cartItems.remove(item)
                adapter.notifyDataSetChanged()
                updateUI()
            }
        )
        rvCartItems.layoutManager = LinearLayoutManager(this)
        rvCartItems.adapter = adapter
    }

    private fun loadCartItems() {
        // Get cart items from OrdersActivity or local storage
        // For now, loading from static data (you'll replace this with actual cart management)
        val passedItems = intent.getParcelableArrayListExtra<CartItem>("cartItems")
        if (passedItems != null && passedItems.isNotEmpty()) {
            cartItems.clear()
            cartItems.addAll(passedItems)
        } else {
            // Sample data for testing
            cartItems.addAll(
                listOf(
                    CartItem("1", "Volfx Spray 100gm", 70.78, 99.00, 2),
                    CartItem("2", "Citrus 50mg Tablet 14's", 40.76, 55.00, 1),
                    CartItem("3", "SupraDyn Multivitamin", 278.76, 350.00, 1),
                    CartItem("4", "Lizol Disinfectant Surface", 349.76, 450.00, 1)
                )
            )
        }
    }

    private fun setupListeners() {
        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        btnPlaceOrder.setOnClickListener {
            if (cartItems.isNotEmpty()) {
                // Check if user has addresses, if not, add address first
                fetchAddressesAndProceed()
            }
        }
    }
    
    private fun fetchAddressesAndProceed() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.getApiService().getAddresses()
                if (response.isSuccessful && response.body()?.status == "success") {
                    val addresses = response.body()?.data
                    if (!addresses.isNullOrEmpty()) {
                        // User has addresses, proceed to address selection
                        val intent = Intent(this@CartActivity, OrderSummaryActivity::class.java)
                        intent.putExtra("cartItems", ArrayList(cartItems))
                        intent.putExtra("totalAmount", calculateTotal())
                        intent.putExtra("savings", calculateSavings())
                        startActivity(intent)
                    } else {
                        // No addresses, ask user to add one
                        Toast.makeText(this@CartActivity, "Please add a delivery address first", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@CartActivity, AddAddressActivity::class.java))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Fallback to order summary
                val intent = Intent(this@CartActivity, OrderSummaryActivity::class.java)
                intent.putExtra("cartItems", ArrayList(cartItems))
                intent.putExtra("totalAmount", calculateTotal())
                startActivity(intent)
            }
        }
    }

    private fun updateUI() {
        if (cartItems.isEmpty()) {
            emptyCartLayout.visibility = View.VISIBLE
            rvCartItems.visibility = View.GONE
            savingsBanner.visibility = View.GONE
            btnPlaceOrder.isEnabled = false
        } else {
            emptyCartLayout.visibility = View.GONE
            rvCartItems.visibility = View.VISIBLE
            btnPlaceOrder.isEnabled = true
            
            val totalItems = cartItems.sumOf { it.quantity }
            val totalAmount = calculateTotal()
            val savings = calculateSavings()
            
            tvCartCount.text = totalItems.toString()
            tvItemCount.text = if (totalItems == 1) "$totalItems Item" else "$totalItems Items"
            tvTotalAmount.text = "₹${String.format("%.2f", totalAmount)}"
            
            if (savings > 0) {
                savingsBanner.visibility = View.VISIBLE
                tvSavingsText.text = "You are saving ₹${String.format("%.2f", savings)} on this order"
            } else {
                savingsBanner.visibility = View.GONE
            }
        }
    }

    private fun calculateTotal(): Double {
        return cartItems.sumOf { it.price * it.quantity }
    }

    private fun calculateSavings(): Double {
        return cartItems.sumOf { (it.originalPrice - it.price) * it.quantity }
    }

    inner class CartItemAdapter(
        private val items: List<CartItem>,
        private val onQuantityChanged: () -> Unit,
        private val onDeleteItem: (CartItem) -> Unit
    ) : RecyclerView.Adapter<CartItemAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvMedicineName: TextView = view.findViewById(R.id.tvMedicineName)
            val tvPrice: TextView = view.findViewById(R.id.tvPrice)
            val tvOriginalPrice: TextView = view.findViewById(R.id.tvOriginalPrice)
            val tvQuantity: TextView = view.findViewById(R.id.tvQuantity)
            val btnDecrease: MaterialButton = view.findViewById(R.id.btnDecrease)
            val btnIncrease: MaterialButton = view.findViewById(R.id.btnIncrease)
            val btnDelete: ImageView = view.findViewById(R.id.btnDelete)
            val ivMedicineImage: ImageView = view.findViewById(R.id.ivMedicineImage)
        }

        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
            val view = android.view.LayoutInflater.from(parent.context)
                .inflate(R.layout.item_cart_medicine, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            
            holder.tvMedicineName.text = item.name
            holder.tvPrice.text = "₹${String.format("%.2f", item.price)}"
            holder.tvQuantity.text = item.quantity.toString()
            
            if (item.originalPrice > item.price) {
                holder.tvOriginalPrice.visibility = View.VISIBLE
                holder.tvOriginalPrice.text = "₹${String.format("%.2f", item.originalPrice)}"
                holder.tvOriginalPrice.paintFlags = 
                    holder.tvOriginalPrice.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                holder.tvOriginalPrice.visibility = View.GONE
            }

            holder.btnDecrease.setOnClickListener {
                if (item.quantity > 1) {
                    item.quantity--
                    holder.tvQuantity.text = item.quantity.toString()
                    onQuantityChanged()
                }
            }

            holder.btnIncrease.setOnClickListener {
                if (item.quantity < 10) {  // Max quantity limit
                    item.quantity++
                    holder.tvQuantity.text = item.quantity.toString()
                    onQuantityChanged()
                }
            }

            holder.btnDelete.setOnClickListener {
                onDeleteItem(item)
            }
        }

        override fun getItemCount() = items.size
    }
}
