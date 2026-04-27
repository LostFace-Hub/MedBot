package com.example.medora

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.example.medora.network.HealthDataRequest
import com.example.medora.network.RetrofitClient
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class LogVitalsBottomSheet : BottomSheetDialogFragment() {

    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottomsheet_log_vitals, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnClose = view.findViewById<CardView>(R.id.btnClose)
        val btnSave = view.findViewById<CardView>(R.id.btnSave)
        progressBar = view.findViewById(R.id.progressBar)

        val heart = view.findViewById<EditText>(R.id.inputHeartRate)
        val systolic = view.findViewById<EditText>(R.id.inputSystolic)
        val diastolic = view.findViewById<EditText>(R.id.inputDiastolic)
        val spo2 = view.findViewById<EditText>(R.id.inputSpO2)
        val temp = view.findViewById<EditText>(R.id.inputTemp)
        val weight = view.findViewById<EditText>(R.id.inputWeight)

        val tempUnit = view.findViewById<Spinner>(R.id.tempUnit)
        tempUnit.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            listOf("°C", "°F")
        )

        btnClose.setOnClickListener { dismiss() }

        btnSave.setOnClickListener {
            saveVitalsToBackend(
                heartRate = heart.text.toString().toIntOrNull(),
                systolic = systolic.text.toString().toIntOrNull(),
                diastolic = diastolic.text.toString().toIntOrNull(),
                oxygenLevel = spo2.text.toString().toIntOrNull(),
                temperature = temp.text.toString().toDoubleOrNull(),
                weight = weight.text.toString().toDoubleOrNull()
            )
        }
    }
    
    private fun saveVitalsToBackend(
        heartRate: Int?,
        systolic: Int?,
        diastolic: Int?,
        oxygenLevel: Int?,
        temperature: Double?,
        weight: Double?
    ) {
        lifecycleScope.launch {
            try {
                progressBar.visibility = View.VISIBLE
                
                val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                
                val request = HealthDataRequest(
                    date = today,
                    steps = null,
                    heartRate = heartRate,
                    weight = weight,
                    sleep = null,
                    calories = null,
                    water = null
                )
                
                val response = RetrofitClient.getApiService().addHealthData(request)
                
                if (response.isSuccessful && response.body()?.status == "success") {
                    Toast.makeText(requireContext(), "Vitals Saved Successfully ✓", Toast.LENGTH_SHORT).show()
                    
                    // Refresh home screen data
                    (activity as? HomeActivity)?.let {
                        it.lifecycleScope.launch {
                            // Small delay to ensure backend processing
                            kotlinx.coroutines.delay(500)
                            it.recreate()
                        }
                    }
                    
                    dismiss()
                } else {
                    Toast.makeText(requireContext(), "Failed to save vitals", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.apply {
            // Set dim background
            setDimAmount(0.7f)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        return dialog
    }

    override fun getTheme(): Int = R.style.DialogStyle
}
