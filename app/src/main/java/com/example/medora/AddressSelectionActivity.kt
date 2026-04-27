package com.example.medora

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

data class Address(
    val id: Int,
    val name: String,
    val addressLine: String,
    val phone: String,
    var isSelected: Boolean = false
)

class AddressSelectionActivity : AppCompatActivity() {

    private lateinit var rvAddresses: RecyclerView
    private lateinit var btnDeliverHere: MaterialButton
    private lateinit var btnAddNewAddress: MaterialButton
    
    private lateinit var adapter: AddressAdapter
    private val addresses = mutableListOf<Address>()
    private var selectedAddress: Address? = null
    
    private var cartItems: ArrayList<CartItem>? = null
    private var totalAmount: Double = 0.0
    private var savings: Double = 0.0
    
    companion object {
        private const val REQUEST_ADD_ADDRESS = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_selection)

        cartItems = intent.getParcelableArrayListExtra("cartItems")
        totalAmount = intent.getDoubleExtra("totalAmount", 0.0)
        savings = intent.getDoubleExtra("savings", 0.0)

        initViews()
        setupRecyclerView()
        loadAddresses()
        setupListeners()
    }

    private fun initViews() {
        rvAddresses = findViewById(R.id.rvAddresses)
        btnDeliverHere = findViewById(R.id.btnDeliverHere)
        btnAddNewAddress = findViewById(R.id.btnAddNewAddress)
    }

    private fun setupRecyclerView() {
        adapter = AddressAdapter(addresses) { address ->
            addresses.forEach { it.isSelected = false }
            address.isSelected = true
            selectedAddress = address
            adapter.notifyDataSetChanged()
            btnDeliverHere.isEnabled = true
        }
        rvAddresses.layoutManager = LinearLayoutManager(this)
        rvAddresses.adapter = adapter
    }

    private fun loadAddresses() {
        // Sample addresses (replace with actual data from database/API)
        addresses.addAll(
            listOf(
                Address(
                    1,
                    "Aryan Kumar",
                    "Block-1,sector-4, Pragmatia, JPI, Lovely Professional University, Jalandhar Saman Darbar, Punjab-144411",
                    "+91 7017055446",
                    true
                ),
                Address(
                    2,
                    "Aryan Kumar",
                    "Shop-1, sector-4, Pragmatia, JPI, Lovely Professional University, Jalandhar Saman Darbar, Punjab-144411",
                    "+91 7017055446"
                ),
                Address(
                    3,
                    "Aryan Kumar",
                    "Block-1, sector-4, Pragmatia, JPI, Lovely Professional University, Jalandhar Saman Darbar, Punjab-144411",
                    "+91 7017055446"
                )
            )
        )
        
        selectedAddress = addresses.firstOrNull { it.isSelected }
        btnDeliverHere.isEnabled = selectedAddress != null
    }

    private fun setupListeners() {
        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        btnAddNewAddress.setOnClickListener {
            val intent = Intent(this, AddAddressActivity::class.java)
            startActivityForResult(intent, REQUEST_ADD_ADDRESS)
        }

        btnDeliverHere.setOnClickListener {
            selectedAddress?.let { address ->
                val intent = Intent(this, OrderSummaryActivity::class.java)
                intent.putExtra("cartItems", cartItems)
                intent.putExtra("totalAmount", totalAmount)
                intent.putExtra("savings", savings)
                intent.putExtra("selectedAddress", address.addressLine)
                intent.putExtra("recipientName", address.name)
                startActivity(intent)
            }
        }
    }

    inner class AddressAdapter(
        private val addresses: List<Address>,
        private val onAddressSelected: (Address) -> Unit
    ) : RecyclerView.Adapter<AddressAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val radioAddress: RadioButton = view.findViewById(R.id.radioAddress)
            val tvAddressName: TextView = view.findViewById(R.id.tvAddressName)
            val tvAddress: TextView = view.findViewById(R.id.tvAddress)
            val tvPhone: TextView = view.findViewById(R.id.tvPhone)
        }

        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
            val view = android.view.LayoutInflater.from(parent.context)
                .inflate(R.layout.item_address, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val address = addresses[position]
            
            holder.tvAddressName.text = address.name
            holder.tvAddress.text = address.addressLine
            holder.tvPhone.text = address.phone
            holder.radioAddress.isChecked = address.isSelected

            holder.itemView.setOnClickListener {
                onAddressSelected(address)
            }

            holder.radioAddress.setOnClickListener {
                onAddressSelected(address)
            }
        }

        override fun getItemCount() = addresses.size
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        if (requestCode == REQUEST_ADD_ADDRESS && resultCode == RESULT_OK && data != null) {
            val fullName = data.getStringExtra("fullName") ?: return
            val phone = data.getStringExtra("phone") ?: return
            val address = data.getStringExtra("address") ?: return
            val addressType = data.getStringExtra("addressType") ?: "Home"
            val isDefault = data.getBooleanExtra("isDefault", false)
            
            // Create new address
            val newAddress = Address(
                id = addresses.size + 1,
                name = fullName,
                addressLine = address,
                phone = phone,
                isSelected = isDefault
            )
            
            // If it's set as default, deselect all others
            if (isDefault) {
                addresses.forEach { it.isSelected = false }
                selectedAddress = newAddress
                btnDeliverHere.isEnabled = true
            }
            
            // Add to list at the top
            addresses.add(0, newAddress)
            adapter.notifyDataSetChanged()
            
            // Show success message
            android.widget.Toast.makeText(
                this, 
                "Address added successfully!", 
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }
    }
}
