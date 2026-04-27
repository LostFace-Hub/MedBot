package com.example.medora

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medora.network.Address
import com.example.medora.network.RetrofitClient
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class AddressManagementActivity : AppCompatActivity() {

    private lateinit var ivBack: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAddAddress: FloatingActionButton
    private lateinit var emptyView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_management)

        ivBack = findViewById(R.id.ivBack)
        recyclerView = findViewById(R.id.recyclerView)
        fabAddAddress = findViewById(R.id.fabAddAddress)
        emptyView = findViewById(R.id.emptyView)

        ivBack.setOnClickListener { finish() }
        
        recyclerView.layoutManager = LinearLayoutManager(this)

        fabAddAddress.setOnClickListener {
            // Navigate to AddAddressActivity
            Toast.makeText(this, "Add new address", Toast.LENGTH_SHORT).show()
        }

        loadAddresses()
    }

    private fun loadAddresses() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.getApiService().getAddresses()
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    if (apiResponse.status == "success" && apiResponse.data != null) {
                        val addresses = apiResponse.data
                        if (addresses.isEmpty()) {
                            emptyView.visibility = TextView.VISIBLE
                            recyclerView.visibility = RecyclerView.GONE
                        } else {
                            emptyView.visibility = TextView.GONE
                            recyclerView.visibility = RecyclerView.VISIBLE
                            // Set adapter with addresses
                        }
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@AddressManagementActivity, "Error loading addresses", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
