package com.example.medora

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.location.Location
import android.os.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.medora.network.RetrofitClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class EmergencyBottomSheet : BottomSheetDialogFragment() {

    private var sosHandler: Handler? = null
    private var isHolding = false
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    override fun getTheme(): Int = R.style.DialogStyle

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            dialog.window?.setDimAmount(0.7f)
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        return inflater.inflate(R.layout.bottomsheet_emergency, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnClose = view.findViewById<ImageView>(R.id.btnClose)
        val optCall = view.findViewById<CardView>(R.id.optCall)
        val optShare = view.findViewById<CardView>(R.id.optShareLocation)
        val optNearest = view.findViewById<CardView>(R.id.optNearestHospital)
        val btnSOS = view.findViewById<CardView>(R.id.btnSOS)

        btnClose.setOnClickListener { dismiss() }

        optCall.setOnClickListener {
            Toast.makeText(context, "Calling emergency services...", Toast.LENGTH_SHORT).show()
            // TODO: Implement actual emergency call
        }

        optShare.setOnClickListener {
            shareLocation()
        }

        optNearest.setOnClickListener {
            Toast.makeText(context, "Finding nearest hospital...", Toast.LENGTH_SHORT).show()
            // TODO: Implement nearest hospital search
        }

        // SOS LONG PRESS LOGIC
        sosHandler = Handler(Looper.getMainLooper())

        btnSOS.setOnLongClickListener {
            isHolding = true
            sosHandler?.postDelayed({
                if (isHolding) {
                    triggerSOS()
                }
            }, 3000)
            true
        }

        btnSOS.setOnTouchListener { _, event ->
            when (event.action) {
                android.view.MotionEvent.ACTION_UP,
                android.view.MotionEvent.ACTION_CANCEL -> {
                    isHolding = false
                }
            }
            false
        }
    }

    private fun triggerSOS() {
        Toast.makeText(context, "🚨 Triggering SOS...", Toast.LENGTH_LONG).show()

        // Vibration
        val vibrator = context?.getSystemService(android.content.Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(500)
        }

        // Get location and send SOS to backend
        getCurrentLocationAndSendSOS()
    }

    private fun getCurrentLocationAndSendSOS() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Send SOS without location
            sendSOSToBackend(null)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            val locationData = location?.let {
                mapOf(
                    "latitude" to it.latitude,
                    "longitude" to it.longitude,
                    "address" to "Lat: ${it.latitude}, Long: ${it.longitude}"
                )
            }
            sendSOSToBackend(locationData)
        }.addOnFailureListener {
            Log.e("EmergencyBottomSheet", "Failed to get location: ${it.message}")
            sendSOSToBackend(null)
        }
    }

    private fun sendSOSToBackend(location: Map<String, Any>?) {
        lifecycleScope.launch {
            try {
                val sosRequest = mutableMapOf<String, Any?>(
                    "note" to "Emergency SOS triggered from app",
                    "location" to location
                )

                val response = RetrofitClient.getApiService().triggerSOS(sosRequest)
                
                if (response.isSuccessful && response.body()?.status == "success") {
                    val sosData = response.body()?.data
                    Toast.makeText(
                        requireContext(),
                        "🚨 SOS Alert Sent! ID: ${sosData?.sosId}",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.d("EmergencyBottomSheet", "SOS triggered successfully: ${sosData?.sosId}")
                    
                    // TODO: Show confirmation dialog with emergency contacts
                    dismiss()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Failed to send SOS alert",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("EmergencyBottomSheet", "SOS API error: ${response.message()}")
                }
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Error sending SOS: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("EmergencyBottomSheet", "Error sending SOS", e)
            }
        }
    }

    private fun shareLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val locationText = "My location: https://maps.google.com/?q=${it.latitude},${it.longitude}"
                Toast.makeText(requireContext(), locationText, Toast.LENGTH_LONG).show()
                // TODO: Implement actual sharing via SMS/WhatsApp
            } ?: run {
                Toast.makeText(requireContext(), "Unable to get location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sosHandler?.removeCallbacksAndMessages(null)
    }
}
