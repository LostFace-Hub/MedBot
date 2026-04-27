package com.example.medora

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.lifecycleScope
import com.example.medora.network.RetrofitClient
import com.example.medora.network.Medication
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MedicationTrackerBottomSheet : BottomSheetDialogFragment() {

    private lateinit var btnClose: ImageView
    private lateinit var tabCurrent: TextView
    private lateinit var tabWeekly: TextView
    private lateinit var btnAddMedication: Button
    private lateinit var medicationList: LinearLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmptyState: TextView

    private var currentTab = "current"
    private var medications = listOf<Medication>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottomsheet_medication_tracker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        btnClose = view.findViewById(R.id.btnClose)
        tabCurrent = view.findViewById(R.id.tabCurrent)
        tabWeekly = view.findViewById(R.id.tabWeekly)
        btnAddMedication = view.findViewById(R.id.btnAddMedication)
        medicationList = view.findViewById(R.id.medicationList)
        progressBar = view.findViewById(R.id.progressBar)
        tvEmptyState = view.findViewById(R.id.tvEmptyState)

        setupListeners()
        fetchMedications()
    }

    private fun setupListeners() {
        btnClose.setOnClickListener { dismiss() }

        tabCurrent.setOnClickListener {
            currentTab = "current"
            updateTabSelection()
            displayMedications()
        }

        tabWeekly.setOnClickListener {
            currentTab = "weekly"
            updateTabSelection()
            displayMedications()
        }

        btnAddMedication.setOnClickListener {
            Toast.makeText(requireContext(), "Add medication feature coming soon", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateTabSelection() {
        if (currentTab == "current") {
            tabCurrent.setBackgroundResource(R.drawable.tab_active)
            tabWeekly.setBackgroundResource(R.drawable.tab_inactive)
            tabCurrent.setTextColor(resources.getColor(R.color.white, null))
            tabWeekly.setTextColor(resources.getColor(R.color.textSecondary, null))
        } else {
            tabWeekly.setBackgroundResource(R.drawable.tab_active)
            tabCurrent.setBackgroundResource(R.drawable.tab_inactive)
            tabWeekly.setTextColor(resources.getColor(R.color.white, null))
            tabCurrent.setTextColor(resources.getColor(R.color.textSecondary, null))
        }
    }

    private fun fetchMedications() {
        progressBar.visibility = View.VISIBLE
        tvEmptyState.visibility = View.GONE
        medicationList.removeAllViews()

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.getApiService().getMedications()
                Log.d("MedicationTracker", "Response: ${response.code()}")

                if (response.isSuccessful && response.body()?.status == "success") {
                    medications = response.body()?.data ?: emptyList()
                    Log.d("MedicationTracker", "Fetched ${medications.size} medications")
                    displayMedications()
                } else {
                    Log.e("MedicationTracker", "API error: ${response.message()}")
                    showEmptyState("Failed to load medications")
                }
            } catch (e: Exception) {
                Log.e("MedicationTracker", "Error fetching medications: ${e.message}", e)
                showEmptyState("Error loading medications")
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun displayMedications() {
        medicationList.removeAllViews()

        if (medications.isEmpty()) {
            showEmptyState("No medications found")
            return
        }

        tvEmptyState.visibility = View.GONE

        for (medication in medications) {
            val medicationView = createMedicationView(medication)
            medicationList.addView(medicationView)
        }
    }

    private fun createMedicationView(medication: Medication): View {
        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.item_medication, medicationList, false)

        val tvMedicationName = view.findViewById<TextView>(R.id.tvMedicationName)
        val tvDosage = view.findViewById<TextView>(R.id.tvDosage)
        val tvFrequency = view.findViewById<TextView>(R.id.tvFrequency)
        val tvStartDate = view.findViewById<TextView>(R.id.tvStartDate)
        val btnDelete = view.findViewById<ImageView>(R.id.btnDelete)

        tvMedicationName.text = medication.name
        tvDosage.text = medication.dosage
        tvFrequency.text = medication.frequency
        
        medication.startDate?.let {
            try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                inputFormat.timeZone = TimeZone.getTimeZone("UTC")
                val date = inputFormat.parse(it)
                val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                tvStartDate.text = "Since ${outputFormat.format(date)}"
            } catch (e: Exception) {
                tvStartDate.text = "Start date: $it"
            }
        } ?: run {
            tvStartDate.visibility = View.GONE
        }

        btnDelete.setOnClickListener {
            medication._id?.let { id -> deleteMedication(id) }
        }

        return view
    }

    private fun deleteMedication(medicationId: String) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.getApiService().deleteMedication(medicationId)
                if (response.isSuccessful && response.body()?.status == "success") {
                    Toast.makeText(requireContext(), "Medication removed", Toast.LENGTH_SHORT).show()
                    fetchMedications()
                } else {
                    Toast.makeText(requireContext(), "Failed to remove medication", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("MedicationTracker", "Error deleting medication: ${e.message}", e)
                Toast.makeText(requireContext(), "Error removing medication", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showEmptyState(message: String) {
        tvEmptyState.text = message
        tvEmptyState.visibility = View.VISIBLE
        medicationList.removeAllViews()
    }
}
