package com.example.medora

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.example.medora.network.RetrofitClient
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class ConsultationBottomSheet : BottomSheetDialogFragment() {

    private lateinit var progressBar: ProgressBar
    private lateinit var doctorListContainer: LinearLayout
    private lateinit var tvNoDoctors: TextView
    private var selectedDoctorId: String? = null

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
        return inflater.inflate(R.layout.bottomsheet_consultation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnClose = view.findViewById<ImageView>(R.id.btnClose)
        progressBar = view.findViewById(R.id.progressBar)
        doctorListContainer = view.findViewById(R.id.doctorListContainer)
        tvNoDoctors = view.findViewById(R.id.tvNoDoctors)
        val btnBookNow = view.findViewById<Button>(R.id.btnBookNow)

        btnClose.setOnClickListener { dismiss() }
        
        btnBookNow?.setOnClickListener {
            if (selectedDoctorId != null) {
                // Navigate to appointment booking screen or activity
                Toast.makeText(requireContext(), "Opening appointment booking...", Toast.LENGTH_SHORT).show()
                // TODO: Navigate to DoctorDetailActivity with selectedDoctorId
                dismiss()
            } else {
                Toast.makeText(requireContext(), "Please select a doctor first", Toast.LENGTH_SHORT).show()
            }
        }

        fetchDoctors()
    }

    private fun fetchDoctors() {
        progressBar.visibility = View.VISIBLE
        doctorListContainer.visibility = View.GONE
        tvNoDoctors.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.getApiService().getDoctors(limit = 10)
                Log.d("Consultation", "Response: ${response.code()}")

                if (response.isSuccessful && response.body()?.status == "success") {
                    val doctorsResponse = response.body()?.data
                    val doctors = doctorsResponse?.doctors ?: emptyList()
                    if (doctors.isNotEmpty()) {
                        displayDoctors(doctors)
                    } else {
                        showNoDoctors()
                    }
                } else {
                    Log.e("Consultation", "API error: ${response.message()}")
                    showNoDoctors()
                }
            } catch (e: Exception) {
                Log.e("Consultation", "Error fetching doctors: ${e.message}", e)
                showNoDoctors()
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun displayDoctors(doctors: List<com.example.medora.network.Doctor>) {
        doctorListContainer.removeAllViews()
        doctorListContainer.visibility = View.VISIBLE

        for (doctor in doctors) {
            val doctorCard = createDoctorCard(doctor)
            doctorListContainer.addView(doctorCard)
        }
    }

    private fun createDoctorCard(doctor: com.example.medora.network.Doctor): View {
        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.item_doctor_card, doctorListContainer, false)

        val cardDoctor = view as CardView
        val tvDoctorName = view.findViewById<TextView>(R.id.doctorName)
        val tvSpecialization = view.findViewById<TextView>(R.id.doctorSpec)
        val tvRating = view.findViewById<TextView>(R.id.doctorRating)
        val tvAvailability = view.findViewById<TextView>(R.id.doctorAvailability)

        tvDoctorName.text = doctor.fullName
        tvSpecialization.text = doctor.specialization
        tvRating.text = "${doctor.rating ?: 0.0}"
        
        if (!doctor.availability.isNullOrEmpty()) {
            tvAvailability.text = "Available"
            tvAvailability.setTextColor(resources.getColor(android.R.color.holo_green_dark, null))
        } else {
            tvAvailability.text = "Unavailable"
            tvAvailability.setTextColor(resources.getColor(android.R.color.holo_red_dark, null))
        }

        cardDoctor.setOnClickListener {
            selectedDoctorId = doctor.fullName
            highlightSelectedCard(cardDoctor)
            Toast.makeText(requireContext(), "Selected ${doctor.fullName}", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun highlightSelectedCard(selectedCard: CardView) {
        // Reset all cards - change background
        for (i in 0 until doctorListContainer.childCount) {
            val child = doctorListContainer.getChildAt(i) as? CardView
            child?.setCardBackgroundColor(resources.getColor(android.R.color.white, null))
        }

        // Highlight selected card with light accent color
        selectedCard.setCardBackgroundColor(0x301FA9C1) // 20% opacity accent
    }

    private fun showNoDoctors() {
        tvNoDoctors.visibility = View.VISIBLE
        tvNoDoctors.text = "No doctors available at the moment"
        doctorListContainer.visibility = View.GONE
    }
}
