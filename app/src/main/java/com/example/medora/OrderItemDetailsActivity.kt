package com.example.medora

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class OrderItemDetailsActivity : AppCompatActivity() {

    private lateinit var ivProductImage: ImageView
    private lateinit var tvProductName: TextView
    private lateinit var tvCategory: TextView
    private lateinit var tvRating: TextView
    private lateinit var tvReviewCount: TextView
    private lateinit var tvCurrentPrice: TextView
    private lateinit var tvOriginalPrice: TextView
    private lateinit var tvSaveAmount: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvBrand: TextView
    private lateinit var tvSize: TextView
    private lateinit var tvManufacturer: TextView
    private lateinit var tvCountry: TextView
    private lateinit var discountBadge: MaterialCardView
    private lateinit var tvDiscount: TextView
    private lateinit var tvStockStatus: TextView
    private lateinit var stockIndicator: View
    private lateinit var btnAddToCart: MaterialButton
    private lateinit var btnBuyNow: MaterialButton
    private lateinit var btnFavorite: ImageView
    private lateinit var btnShare: ImageView

    private var isFavorite = false
    private var productId: Int = 0
    private var productName: String = ""
    private var productPrice: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_item_details)

        initViews()
        loadProductData()
        setupListeners()
    }

    private fun initViews() {
        ivProductImage = findViewById(R.id.ivProductImage)
        tvProductName = findViewById(R.id.tvProductName)
        tvCategory = findViewById(R.id.tvCategory)
        tvRating = findViewById(R.id.tvRating)
        tvReviewCount = findViewById(R.id.tvReviewCount)
        tvCurrentPrice = findViewById(R.id.tvCurrentPrice)
        tvOriginalPrice = findViewById(R.id.tvOriginalPrice)
        tvSaveAmount = findViewById(R.id.tvSaveAmount)
        tvDescription = findViewById(R.id.tvDescription)
        tvBrand = findViewById(R.id.tvBrand)
        tvSize = findViewById(R.id.tvSize)
        tvManufacturer = findViewById(R.id.tvManufacturer)
        tvCountry = findViewById(R.id.tvCountry)
        discountBadge = findViewById(R.id.discountBadge)
        tvDiscount = findViewById(R.id.tvDiscount)
        tvStockStatus = findViewById(R.id.tvStockStatus)
        stockIndicator = findViewById(R.id.stockIndicator)
        btnAddToCart = findViewById(R.id.btnAddToCart)
        btnBuyNow = findViewById(R.id.btnBuyNow)
        btnFavorite = findViewById(R.id.btnFavorite)
        btnShare = findViewById(R.id.btnShare)
    }

    private fun loadProductData() {
        // Get product data from intent
        productId = intent.getIntExtra("productId", 0)
        productName = intent.getStringExtra("productName") ?: "Product Name"
        val category = intent.getStringExtra("category") ?: "Category"
        productPrice = intent.getDoubleExtra("price", 0.0)
        val originalPrice = intent.getDoubleExtra("originalPrice", productPrice * 1.5)
        val imageRes = intent.getIntExtra("imageRes", R.drawable.ic_medicine)

        // Populate views
        ivProductImage.setImageResource(imageRes)
        tvProductName.text = productName
        tvCategory.text = category
        tvCurrentPrice.text = "₹${String.format("%.2f", productPrice)}"
        
        // Original price with strikethrough
        tvOriginalPrice.text = "₹${String.format("%.2f", originalPrice)}"
        tvOriginalPrice.paintFlags = tvOriginalPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

        // Calculate savings
        val savings = originalPrice - productPrice
        val discountPercentage = ((savings / originalPrice) * 100).toInt()
        
        if (savings > 0) {
            tvSaveAmount.text = "Save ₹${String.format("%.2f", savings)}"
            tvSaveAmount.visibility = View.VISIBLE
            
            // Show discount badge
            discountBadge.visibility = View.VISIBLE
            tvDiscount.text = "$discountPercentage% OFF"
        } else {
            tvSaveAmount.visibility = View.GONE
            tvOriginalPrice.visibility = View.GONE
            discountBadge.visibility = View.GONE
        }

        // Set rating and reviews (dummy data)
        tvRating.text = "4.5"
        tvReviewCount.text = "(234 reviews)"

        // Set product description based on category
        tvDescription.text = getDescriptionForProduct(productName, category)

        // Set product details
        tvBrand.text = getBrandName(productName)
        tvSize.text = getProductSize(productName)
        tvManufacturer.text = "Health Care Ltd."
        tvCountry.text = "India"

        // Set stock status
        updateStockStatus(true) // true = in stock
    }

    private fun getDescriptionForProduct(name: String, category: String): String {
        return when {
            name.contains("Spray", ignoreCase = true) -> 
                "$name is a topical pain relief medication designed to provide quick relief from muscle pain, joint pain, and inflammation. The spray formula allows for easy application and fast absorption."
            name.contains("Tablet", ignoreCase = true) -> 
                "$name provides effective relief from pain and fever. Each tablet is formulated with carefully selected ingredients to ensure fast and lasting relief."
            name.contains("Gel", ignoreCase = true) -> 
                "$name is a topical gel formulation that provides targeted relief from pain and inflammation. Easy to apply and quick to absorb."
            name.contains("Vitamin", ignoreCase = true) -> 
                "$name is a comprehensive multivitamin supplement designed to support overall health and wellness. Contains essential vitamins and minerals for daily nutrition."
            category.contains("Cold & Flu", ignoreCase = true) -> 
                "$name helps relieve symptoms of cold and flu including congestion, headache, and body aches. Formulated for fast-acting relief."
            category.contains("Diabetes", ignoreCase = true) -> 
                "$name is designed to help manage diabetes and maintain healthy blood sugar levels. Always use as directed by your healthcare provider."
            else -> 
                "$name is a high-quality pharmaceutical product designed to provide effective treatment. Always consult with a healthcare professional before use."
        }
    }

    private fun getBrandName(productName: String): String {
        return when {
            productName.contains("Volfx") -> "Volfx Pharmaceuticals"
            productName.contains("Dolo") -> "Dolo Healthcare"
            productName.contains("Crocin") -> "GlaxoSmithKline"
            productName.contains("SupraDyn") -> "Bayer Pharmaceuticals"
            productName.contains("Glucon") -> "Heinz Nutrition"
            else -> "Premium Pharmaceuticals"
        }
    }

    private fun getProductSize(productName: String): String {
        return when {
            productName.contains("100gm") -> "100gm"
            productName.contains("30gm") -> "30gm"
            productName.contains("50ml") -> "50ml"
            productName.contains("500gm") -> "500gm"
            productName.contains("250gm") -> "250gm"
            productName.contains("(5's)") -> "5 Tablets"
            productName.contains("(10's)") -> "10 Tablets"
            else -> "Standard Size"
        }
    }

    private fun updateStockStatus(inStock: Boolean) {
        if (inStock) {
            tvStockStatus.text = "In Stock"
            tvStockStatus.setTextColor(getColor(android.R.color.holo_green_dark))
            stockIndicator.setBackgroundResource(R.drawable.bg_stock_indicator)
        } else {
            tvStockStatus.text = "Out of Stock"
            tvStockStatus.setTextColor(getColor(android.R.color.holo_red_dark))
            // Create out of stock indicator drawable if needed
        }
    }

    private fun setupListeners() {
        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        btnFavorite.setOnClickListener {
            toggleFavorite()
        }

        btnShare.setOnClickListener {
            shareProduct()
        }

        btnAddToCart.setOnClickListener {
            addToCart()
        }

        btnBuyNow.setOnClickListener {
            buyNow()
        }
    }

    private fun toggleFavorite() {
        isFavorite = !isFavorite
        if (isFavorite) {
            btnFavorite.setImageResource(R.drawable.ic_favorite_filled)
            Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show()
        } else {
            btnFavorite.setImageResource(R.drawable.ic_favorite_border)
            Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show()
        }
    }

    private fun shareProduct() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Check out this product!")
        shareIntent.putExtra(
            Intent.EXTRA_TEXT,
            "Check out $productName at ₹${String.format("%.2f", productPrice)} on Medora app!"
        )
        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

    private fun addToCart() {
        Toast.makeText(this, "$productName added to cart", Toast.LENGTH_SHORT).show()
        
        // TODO: Add actual cart functionality
        // You can integrate with OrdersActivity's cart system here
        
        // Optionally finish activity after adding to cart
        // finish()
    }

    private fun buyNow() {
        // Create a single-item cart and go directly to checkout
        val cartItem = CartItem(
            medicineId = productId.toString(),
            name = productName,
            price = productPrice,
            originalPrice = productPrice * 1.5,
            quantity = 1,
            imageRes = intent.getIntExtra("imageRes", R.drawable.ic_medicine)
        )

        val intent = Intent(this, CartActivity::class.java)
        val cartItems = ArrayList<CartItem>()
        cartItems.add(cartItem)
        intent.putExtra("cartItems", cartItems)
        startActivity(intent)
    }
}
