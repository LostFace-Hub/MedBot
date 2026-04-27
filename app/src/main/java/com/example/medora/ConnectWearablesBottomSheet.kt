package com.example.medora

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.example.medora.network.RetrofitClient
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class ConnectWearablesBottomSheet : BottomSheetDialogFragment() {

    private lateinit var healthConnectManager: HealthConnectManager
    private lateinit var permissionLauncher: ActivityResultLauncher<Set<String>>
    
    private lateinit var btnHealthConnect: CardView
    private lateinit var tvHealthConnectStatus: TextView
    private lateinit var progressBar: ProgressBar

    override fun getTheme(): Int = R.style.DialogStyle

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            dialog.window?.setDimAmount(0.7f)
        }
        return dialog
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        healthConnectManager = HealthConnectManager(requireContext())
        
        // Register permission launcher
        permissionLauncher = registerForActivityResult(
            healthConnectManager.createPermissionContract()
        ) { granted ->
            if (granted.containsAll(healthConnectManager.permissions)) {
                Toast.makeText(context, "All permissions granted! Syncing data...", Toast.LENGTH_SHORT).show()
                syncHealthData()
            } else {
                Toast.makeText(context, "Some permissions were denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottomsheet_connect_wearables, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnClose = view.findViewById<ImageView>(R.id.btnClose)
        btnHealthConnect = view.findViewById<CardView>(R.id.btnHealthConnect)
        tvHealthConnectStatus = view.findViewById<TextView>(R.id.tvHealthConnectStatus)
        progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        
        val btnApple = view.findViewById<CardView>(R.id.btnApple)
        val btnFitbit = view.findViewById<CardView>(R.id.btnFitbit)
        val btnGarmin = view.findViewById<CardView>(R.id.btnGarmin)

        btnClose.setOnClickListener { dismiss() }

        // Health Connect integration (works with most devices)
        btnHealthConnect.setOnClickListener {
            connectHealthConnect()
        }

        // Legacy device-specific buttons
        btnApple?.setOnClickListener {
            Toast.makeText(context, "Use Health Connect to sync Apple Watch data", Toast.LENGTH_LONG).show()
        }

        btnFitbit?.setOnClickListener {
            Toast.makeText(context, "Use Health Connect to sync Fitbit data", Toast.LENGTH_LONG).show()
        }

        btnGarmin?.setOnClickListener {
            Toast.makeText(context, "Use Health Connect to sync Garmin data", Toast.LENGTH_LONG).show()
        }
        
        // Check Health Connect status on load
        checkHealthConnectStatus()
    }
    
    private fun checkHealthConnectStatus() {
        lifecycleScope.launch {
            progressBar.visibility = View.VISIBLE
            
            if (!healthConnectManager.isHealthConnectInstalled()) {
                tvHealthConnectStatus.text = "Health Connect not installed"
                progressBar.visibility = View.GONE
                return@launch
            }
            
            val isAvailable = healthConnectManager.isAvailable()
            if (!isAvailable) {
                tvHealthConnectStatus.text = "Health Connect not available"
                progressBar.visibility = View.GONE
                return@launch
            }
            
            val hasPermissions = healthConnectManager.hasAllPermissions()
            if (hasPermissions) {
                tvHealthConnectStatus.text = "Connected - Tap to sync"
                tvHealthConnectStatus.setTextColor(android.graphics.Color.parseColor("#00C177"))
            } else {
                tvHealthConnectStatus.text = "Tap to connect"
            }
            
            progressBar.visibility = View.GONE
        }
    }
    
    private fun connectHealthConnect() {
        lifecycleScope.launch {
            progressBar.visibility = View.VISIBLE
            
            // Check if Health Connect is installed
            if (!healthConnectManager.isHealthConnectInstalled()) {
                progressBar.visibility = View.GONE
                Toast.makeText(context, "Installing Health Connect...", Toast.LENGTH_SHORT).show()
                healthConnectManager.openHealthConnectPlayStore()
                return@launch
            }
            
            // Check if available
            val isAvailable = healthConnectManager.isAvailable()
            if (!isAvailable) {
                progressBar.visibility = View.GONE
                Toast.makeText(context, "Health Connect is not available on this device", Toast.LENGTH_SHORT).show()
                return@launch
            }
            
            // Check permissions
            val hasPermissions = healthConnectManager.hasAllPermissions()
            if (!hasPermissions) {
                progressBar.visibility = View.GONE
                // Request permissions
                permissionLauncher.launch(healthConnectManager.permissions)
            } else {
                // Already has permissions, sync data
                syncHealthData()
            }
        }
    }
    
    private fun syncHealthData() {
        lifecycleScope.launch {
            progressBar.visibility = View.VISIBLE
            tvHealthConnectStatus.text = "Syncing data..."
            
            try {
                val healthData = healthConnectManager.syncAllHealthData()
                
                // Update status
                tvHealthConnectStatus.text = "✓ Synced successfully"
                tvHealthConnectStatus.setTextColor(android.graphics.Color.parseColor("#00C177"))
                
                // Send data to backend
                syncToBackend(healthData)
                
                Toast.makeText(
                    context, 
                    "Synced: ${healthData.steps} steps, ${healthData.heartRate} bpm", 
                    Toast.LENGTH_LONG
                ).show()
                
                progressBar.visibility = View.GONE
                
                // Dismiss after 1.5 seconds
                view?.postDelayed({
                    dismiss()
                }, 1500)
                
            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                tvHealthConnectStatus.text = "Sync failed"
                tvHealthConnectStatus.setTextColor(android.graphics.Color.parseColor("#FF4B4B"))
                Toast.makeText(context, "Error syncing data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private suspend fun syncToBackend(healthData: HealthDataSync) {
        try {
            // Prepare request body
            val requestBody = mapOf(
                "heartRate" to healthData.heartRate,
                "steps" to healthData.steps,
                "calories" to healthData.calories,
                "sleepHours" to healthData.sleepHours,
                "oxygenLevel" to healthData.oxygenSaturation,
                "bloodPressure" to healthData.bloodPressure?.let {
                    "${it.systolic.toInt()}/${it.diastolic.toInt()}"
                },
                "source" to "health_connect",
                "timestamp" to healthData.timestamp
            )
            
            // Send to backend
            val response = RetrofitClient.getApiService().syncHealthData(requestBody)
            
            if (response.isSuccessful) {
                android.util.Log.d("HealthSync", "Data synced to backend successfully")
            } else {
                android.util.Log.e("HealthSync", "Failed to sync to backend: ${response.code()}")
            }
        } catch (e: Exception) {
            android.util.Log.e("HealthSync", "Error syncing to backend", e)
        }
    }
}

