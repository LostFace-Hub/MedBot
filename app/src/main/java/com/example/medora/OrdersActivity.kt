package com.example.medora

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medora.network.RetrofitClient
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.launch

data class MedicineProduct(
    val id: String,
    val name: String,
    val price: Double,
    val category: String,
    val imageRes: Int = R.drawable.ic_medicine,
    val stock: Int = 0
)

class OrdersActivity : AppCompatActivity() {

    private lateinit var allMedicines: MutableList<MedicineProduct>
    private lateinit var adapter: MedicineAdapter
    private lateinit var etSearch: EditText
    private lateinit var tvCartBadge: TextView
    private var progressBar: ProgressBar? = null
    private lateinit var rvMedicines: RecyclerView
    private var cartItemsList = ArrayList<CartItem>()
    private var selectedCategory: String = "All"

    // Category chips
    private lateinit var chipPainRelief: MaterialCardView
    private lateinit var chipHealthCare: MaterialCardView
    private lateinit var chipFitness: MaterialCardView
    private lateinit var chipDiabetes: MaterialCardView
    private lateinit var chipColdFlu: MaterialCardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)

        etSearch = findViewById(R.id.etSearch)
        tvCartBadge = findViewById(R.id.tvCartBadge)
        progressBar = findViewById(R.id.progressBar) // Optional - may not exist in layout
        rvMedicines = findViewById(R.id.rvMedicines)
        
        // Category chips
        chipPainRelief = findViewById(R.id.chipPainRelief)
        chipHealthCare = findViewById(R.id.chipHealthCare)
        chipFitness = findViewById(R.id.chipFitness)
        chipDiabetes = findViewById(R.id.chipDiabetes)
        chipColdFlu = findViewById(R.id.chipColdFlu)

        // My Orders button
        findViewById<TextView>(R.id.btnMyOrders).setOnClickListener {
            startActivity(Intent(this, MyOrdersActivity::class.java))
        }

        // Initialize medicines data
        allMedicines = mutableListOf()

        // Setup RecyclerView with Grid Layout
        setupRecyclerView()

        // Setup search
        setupSearch()

        // Setup category chips
        setupCategoryChips()

        // Setup cart button
        setupCartButton()

        // Setup bottom navigation
        setupBottomNavigation()

        // Update cart badge
        updateCartBadge()
        
        // Fetch medicines from backend
        fetchMedicines()
    }

    private fun createSampleMedicines(): MutableList<MedicineProduct> {
        return mutableListOf(
            MedicineProduct("1", "Volfx Spray 100gm", 70.78, "Pain Relief"),
            MedicineProduct("2", "Omingel Gel (topical) 30gm", 230.10, "Pain Relief"),
            MedicineProduct("3", "SupraDyn Multivitamin Tablet with Minerals (5's)", 80.00, "Health Care"),
            MedicineProduct("4", "Natural Vibes Ayurvedic Vitamin C 20 ml", 189.00, "Health Care"),
            MedicineProduct("5", "Dolo 650 Tablet", 30.50, "Pain Relief"),
            MedicineProduct("6", "Crocin Advance Tablet", 25.99, "Pain Relief"),
            MedicineProduct("7", "Calpol 500mg Tablet", 15.25, "Pain Relief"),
            MedicineProduct("8", "Combiflam Tablet", 28.00, "Pain Relief"),
            MedicineProduct("9", "Allegra 120mg Tablet", 150.00, "Cold & Flu"),
            MedicineProduct("10", "Cetrizine 10mg Tablet", 12.50, "Cold & Flu"),
            MedicineProduct("11", "Sinarest Tablet", 45.00, "Cold & Flu"),
            MedicineProduct("12", "Vicks Vaporub 50ml", 105.00, "Cold & Flu"),
            MedicineProduct("13", "Glucon-D Powder 500gm", 180.00, "Fitness"),
            MedicineProduct("14", "Protinex Powder 250gm", 420.00, "Fitness"),
            MedicineProduct("15", "Electral Powder Sachet", 21.50, "Fitness"),
            MedicineProduct("16", "Glucometer Strips", 850.00, "Diabetes"),
            MedicineProduct("17", "Insulin Syringe Pack", 120.00, "Diabetes"),
            MedicineProduct("18", "Sugar Free Gold 500 Pellets", 285.00, "Diabetes"),
            MedicineProduct("19", "Vitamin D3 Capsules", 195.00, "Health Care"),
            MedicineProduct("20", "Omega 3 Fish Oil Capsules", 650.00, "Health Care")
        )
    }

    private fun setupRecyclerView() {
        val rvMedicines = findViewById<RecyclerView>(R.id.rvMedicines)
        adapter = MedicineAdapter(allMedicines, 
            onAddToCart = { medicine ->
                addToCart(medicine)
            },
            onItemClick = { medicine ->
                openProductDetails(medicine)
            }
        )
        rvMedicines.layoutManager = GridLayoutManager(this, 2)
        rvMedicines.adapter = adapter
    }

    private fun setupSearch() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filterMedicines(s.toString())
            }
        })
    }

    private fun setupCategoryChips() {
        // Add All category chip
        val chipAll = findViewById<MaterialCardView>(R.id.chipAll)
        chipAll?.setOnClickListener { 
            selectCategory("All", chipAll)
            fetchMedicines() // Fetch all medicines
        }
        
        chipPainRelief.setOnClickListener { 
            selectCategory("Pain Relief", chipPainRelief)
            fetchMedicines(category = "Pain Relief")
        }
        chipHealthCare.setOnClickListener { 
            selectCategory("Health Care", chipHealthCare)
            fetchMedicines(category = "Health Care")
        }
        chipFitness.setOnClickListener { 
            selectCategory("Fitness", chipFitness)
            fetchMedicines(category = "Fitness")
        }
        chipDiabetes.setOnClickListener { 
            selectCategory("Diabetes", chipDiabetes)
            fetchMedicines(category = "Diabetes")
        }
        chipColdFlu.setOnClickListener { 
            selectCategory("Cold & Flu", chipColdFlu)
            fetchMedicines(category = "Cold & Flu")
        }
    }

    private fun selectCategory(category: String, selectedChip: MaterialCardView) {
        // Reset all chips
        val chipAll = findViewById<MaterialCardView>(R.id.chipAll)
        chipAll?.let { resetChip(it) }
        resetChip(chipPainRelief)
        resetChip(chipHealthCare)
        resetChip(chipFitness)
        resetChip(chipDiabetes)
        resetChip(chipColdFlu)

        // Highlight selected chip
        selectedChip.setCardBackgroundColor(android.graphics.Color.parseColor("#1BA3C4"))
        (selectedChip.getChildAt(0) as TextView).setTextColor(android.graphics.Color.WHITE)

        selectedCategory = category
        // Don't filter locally, let the backend handle it through fetchMedicines
    }

    private fun resetChip(chip: MaterialCardView) {
        chip.setCardBackgroundColor(android.graphics.Color.parseColor("#DBEEF3"))
        (chip.getChildAt(0) as TextView).setTextColor(android.graphics.Color.parseColor("#1BA3C4"))
    }

    private fun filterMedicines(query: String) {
        val filteredList = allMedicines.filter { medicine ->
            val matchesSearch = medicine.name.contains(query, ignoreCase = true) ||
                    medicine.category.contains(query, ignoreCase = true)
            
            val matchesCategory = if (selectedCategory == "All") true
            else medicine.category == selectedCategory

            matchesSearch && matchesCategory
        }
        adapter.updateList(filteredList)
    }

    private fun addToCart(medicine: MedicineProduct) {
        // Check if item already exists in cart
        val existingItem = cartItemsList.find { it.medicineId == medicine.id }
        
        if (existingItem != null) {
            // Increase quantity if already in cart
            existingItem.quantity++
        } else {
            // Add new item to cart
            val cartItem = CartItem(
                medicineId = medicine.id,
                name = medicine.name,
                price = medicine.price,
                originalPrice = medicine.price * 1.5, // 33% discount
                quantity = 1,
                imageRes = medicine.imageRes
            )
            cartItemsList.add(cartItem)
        }
        
        updateCartBadge()
        Toast.makeText(this, "${medicine.name} added to cart", Toast.LENGTH_SHORT).show()
    }

    private fun openProductDetails(medicine: MedicineProduct) {
        val intent = Intent(this, OrderItemDetailsActivity::class.java)
        intent.putExtra("productId", medicine.id)
        intent.putExtra("productName", medicine.name)
        intent.putExtra("category", medicine.category)
        intent.putExtra("price", medicine.price)
        intent.putExtra("originalPrice", medicine.price * 1.5)
        intent.putExtra("imageRes", medicine.imageRes)
        startActivity(intent)
    }

    private fun updateCartBadge() {
        val totalItems = cartItemsList.sumOf { it.quantity }
        tvCartBadge.text = totalItems.toString()
        tvCartBadge.visibility = if (totalItems > 0) View.VISIBLE else View.GONE
    }

    private fun setupCartButton() {
        findViewById<FrameLayout>(R.id.btnCart).setOnClickListener {
            if (cartItemsList.isEmpty()) {
                Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, CartActivity::class.java)
                intent.putExtra("cartItems", cartItemsList)
                startActivity(intent)
            }
        }
    }

    private fun setupBottomNavigation() {
        val navHome = findViewById<LinearLayout>(R.id.navHome)
        val navTracking = findViewById<LinearLayout>(R.id.navTracking)
        val navAppt = findViewById<LinearLayout>(R.id.navAppt)
        val navOrders = findViewById<LinearLayout>(R.id.navOrders)
        val navAi = findViewById<LinearLayout>(R.id.navAi)

        // Set Orders as active
        setNavigationActive(navOrders)

        navHome.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        navTracking.setOnClickListener {
            startActivity(Intent(this, TrackingActivity::class.java))
            finish()
        }

        navAppt.setOnClickListener {
            startActivity(Intent(this, AppointmentsActivity::class.java))
            finish()
        }

        navOrders.setOnClickListener {
            // Already on Orders
        }

        navAi.setOnClickListener {
            startActivity(Intent(this, AIAssistantActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
        }
    }

    private fun setNavigationActive(activeNav: LinearLayout) {
        val navHome = findViewById<LinearLayout>(R.id.navHome)
        val navTracking = findViewById<LinearLayout>(R.id.navTracking)
        val navAppt = findViewById<LinearLayout>(R.id.navAppt)
        val navOrders = findViewById<LinearLayout>(R.id.navOrders)
        val navAi = findViewById<LinearLayout>(R.id.navAi)

        // Reset all navigation items
        resetNavigationItem(navHome)
        resetNavigationItem(navTracking)
        resetNavigationItem(navAppt)
        resetNavigationItem(navOrders)
        resetNavigationItem(navAi)

        // Set active navigation item
        val iconBg = activeNav.getChildAt(0) as androidx.cardview.widget.CardView
        val icon = iconBg.getChildAt(0) as ImageView
        val text = activeNav.getChildAt(1) as TextView

        iconBg.setCardBackgroundColor(Color.parseColor("#E8F5F7"))
        iconBg.cardElevation = 2f
        icon.setColorFilter(Color.parseColor("#1BA3C4"))
        text.setTextColor(Color.parseColor("#1BA3C4"))
        text.setTypeface(null, android.graphics.Typeface.BOLD)
    }

    private fun resetNavigationItem(nav: LinearLayout) {
        val iconBg = nav.getChildAt(0) as androidx.cardview.widget.CardView
        val icon = iconBg.getChildAt(0) as ImageView
        val text = nav.getChildAt(1) as TextView

        iconBg.setCardBackgroundColor(Color.parseColor("#F5F8FA"))
        iconBg.cardElevation = 0f
        icon.setColorFilter(Color.parseColor("#6B7280"))
        text.setTextColor(Color.parseColor("#6B7280"))
        text.setTypeface(null, android.graphics.Typeface.NORMAL)
    }

    inner class MedicineAdapter(
        private var medicines: List<MedicineProduct>,
        private val onAddToCart: (MedicineProduct) -> Unit,
        private val onItemClick: (MedicineProduct) -> Unit
    ) : RecyclerView.Adapter<MedicineAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvMedicineName: TextView = view.findViewById(R.id.tvMedicineName)
            val tvPrice: TextView = view.findViewById(R.id.tvPrice)
            val btnAddToCart: com.google.android.material.button.MaterialButton = 
                view.findViewById(R.id.btnAddToCart)
            val layoutQuantityControls: LinearLayout = view.findViewById(R.id.layoutQuantityControls)
            val btnDecrease: com.google.android.material.button.MaterialButton = 
                view.findViewById(R.id.btnDecrease)
            val tvQuantity: TextView = view.findViewById(R.id.tvQuantity)
            val btnIncrease: com.google.android.material.button.MaterialButton = 
                view.findViewById(R.id.btnIncrease)
        }

        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
            val view = android.view.LayoutInflater.from(parent.context)
                .inflate(R.layout.item_medicine_card, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val medicine = medicines[position]
            holder.tvMedicineName.text = medicine.name
            holder.tvPrice.text = "₹${String.format("%.2f", medicine.price)}"
            
            // Check if item is in cart
            val cartItem = cartItemsList.find { it.medicineId == medicine.id }
            
            if (cartItem != null) {
                // Item is in cart, show quantity controls
                holder.btnAddToCart.visibility = View.GONE
                holder.layoutQuantityControls.visibility = View.VISIBLE
                holder.tvQuantity.text = cartItem.quantity.toString()
            } else {
                // Item not in cart, show add to cart button
                holder.btnAddToCart.visibility = View.VISIBLE
                holder.layoutQuantityControls.visibility = View.GONE
            }
            
            // Click on card to view details
            holder.itemView.setOnClickListener {
                onItemClick(medicine)
            }
            
            // Click on add to cart button
            holder.btnAddToCart.setOnClickListener {
                onAddToCart(medicine)
                // Update UI to show quantity controls
                holder.btnAddToCart.visibility = View.GONE
                holder.layoutQuantityControls.visibility = View.VISIBLE
                holder.tvQuantity.text = "1"
            }
            
            // Decrease quantity
            holder.btnDecrease.setOnClickListener {
                val currentItem = cartItemsList.find { it.medicineId == medicine.id }
                if (currentItem != null) {
                    if (currentItem.quantity > 1) {
                        currentItem.quantity--
                        holder.tvQuantity.text = currentItem.quantity.toString()
                        updateCartBadge()
                    } else {
                        // Remove from cart
                        cartItemsList.remove(currentItem)
                        holder.btnAddToCart.visibility = View.VISIBLE
                        holder.layoutQuantityControls.visibility = View.GONE
                        updateCartBadge()
                        Toast.makeText(holder.itemView.context, "${medicine.name} removed from cart", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            
            // Increase quantity
            holder.btnIncrease.setOnClickListener {
                val currentItem = cartItemsList.find { it.medicineId == medicine.id }
                if (currentItem != null) {
                    if (currentItem.quantity < 10) {
                        currentItem.quantity++
                        holder.tvQuantity.text = currentItem.quantity.toString()
                        updateCartBadge()
                    } else {
                        Toast.makeText(holder.itemView.context, "Maximum quantity is 10", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        override fun getItemCount() = medicines.size

        fun updateList(newList: List<MedicineProduct>) {
            medicines = newList
            notifyDataSetChanged()
        }
    }
    
    // ============================================================
    // BACKEND INTEGRATION
    // ============================================================
    private fun fetchMedicines(category: String? = null, search: String? = null) {
        lifecycleScope.launch {
            try {
                progressBar?.visibility = View.VISIBLE
                
                // Map UI category names to backend category names
                val backendCategory = when(category) {
                    "Pain Relief" -> "Tablet"
                    "Cold & Flu" -> "Syrup"
                    "Health Care" -> "Capsule"
                    "Fitness" -> "Powder"
                    "Diabetes" -> "Tablet"
                    else -> null
                }
                
                val response = RetrofitClient.getApiService().getMedicines(
                    page = 1,
                    limit = 100,
                    category = backendCategory,
                    search = search
                )
                
                if (response.isSuccessful && response.body()?.status == "success") {
                    val medicines = response.body()?.data?.medicines
                    if (!medicines.isNullOrEmpty()) {
                        allMedicines.clear()
                        medicines.forEach { medicine ->
                            // Map backend category to UI category for better organization
                            val uiCategory = mapBackendCategoryToUI(medicine.category ?: "Other")
                            
                            allMedicines.add(
                                MedicineProduct(
                                    id = medicine.id,
                                    name = medicine.name,
                                    price = medicine.price,
                                    category = uiCategory,
                                    imageRes = R.drawable.ic_medicine,
                                    stock = medicine.stock ?: 0
                                )
                            )
                        }
                        adapter.updateList(allMedicines)
                        Toast.makeText(this@OrdersActivity, "${allMedicines.size} medicines loaded from backend", Toast.LENGTH_SHORT).show()
                    } else {
                        // No medicines found in backend
                        allMedicines.clear()
                        adapter.updateList(allMedicines)
                        Toast.makeText(this@OrdersActivity, "No medicines found. Please add medicines to the database.", Toast.LENGTH_LONG).show()
                    }
                } else {
                    val errorMsg = response.body()?.message ?: "Failed to load medicines"
                    Toast.makeText(this@OrdersActivity, "Error: $errorMsg", Toast.LENGTH_SHORT).show()
                    // Load sample data as fallback
                    loadSampleDataAsFallback()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@OrdersActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                // Load sample data as fallback
                loadSampleDataAsFallback()
            } finally {
                progressBar?.visibility = View.GONE
            }
        }
    }
    
    private fun mapBackendCategoryToUI(backendCategory: String): String {
        return when(backendCategory.lowercase()) {
            "tablet" -> "Pain Relief"
            "capsule" -> "Health Care"
            "syrup" -> "Cold & Flu"
            "powder" -> "Fitness"
            "injection" -> "Diabetes"
            "cream", "ointment" -> "Health Care"
            "drops" -> "Cold & Flu"
            "inhaler" -> "Cold & Flu"
            else -> "Health Care"
        }
    }
    
    private fun loadSampleDataAsFallback() {
        if (allMedicines.isEmpty()) {
            allMedicines = createSampleMedicines()
            adapter.updateList(allMedicines)
            Toast.makeText(this, "Showing demo data (Backend connection failed)", Toast.LENGTH_LONG).show()
        }
    }
}
