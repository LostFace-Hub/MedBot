package com.example.medora

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class AppointmentDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointment_details)

        // Setup toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        // Get data from intent
        val doctorName = intent.getStringExtra("DOCTOR_NAME") ?: ""
        val specialization = intent.getStringExtra("SPECIALIZATION") ?: ""
        val dateTime = intent.getStringExtra("DATE_TIME") ?: ""
        val place = intent.getStringExtra("PLACE") ?: ""
        val diagnosis = intent.getStringExtra("DIAGNOSIS") ?: ""
        val chiefComplaint = intent.getStringExtra("CHIEF_COMPLAINT") ?: ""
        val doctorNotes = intent.getStringExtra("DOCTOR_NOTES") ?: ""
        val followUpDate = intent.getStringExtra("FOLLOW_UP_DATE")

        // Vital signs
        val bp = intent.getStringExtra("BP") ?: ""
        val hr = intent.getStringExtra("HR") ?: ""
        val temp = intent.getStringExtra("TEMP") ?: ""
        val weight = intent.getStringExtra("WEIGHT") ?: ""
        val height = intent.getStringExtra("HEIGHT") ?: ""

        // Medicines
        val medicineNames = intent.getStringArrayListExtra("MEDICINE_NAMES") ?: arrayListOf()
        val medicineDosages = intent.getStringArrayListExtra("MEDICINE_DOSAGES") ?: arrayListOf()
        val medicineFrequencies = intent.getStringArrayListExtra("MEDICINE_FREQUENCIES") ?: arrayListOf()
        val medicineDurations = intent.getStringArrayListExtra("MEDICINE_DURATIONS") ?: arrayListOf()
        val medicineInstructions = intent.getStringArrayListExtra("MEDICINE_INSTRUCTIONS") ?: arrayListOf()

        // Lab tests
        val labTests = intent.getStringArrayListExtra("LAB_TESTS") ?: arrayListOf()

        // Set doctor info
        findViewById<TextView>(R.id.tvDoctorName).text = doctorName
        findViewById<TextView>(R.id.tvSpecialization).text = specialization
        findViewById<TextView>(R.id.tvDateTime).text = dateTime
        findViewById<TextView>(R.id.tvPlace).text = place

        // Set vital signs
        findViewById<TextView>(R.id.tvBP).text = bp.split(" ")[0]
        findViewById<TextView>(R.id.tvHeartRate).text = hr.split(" ")[0]
        findViewById<TextView>(R.id.tvTemp).text = temp
        findViewById<TextView>(R.id.tvWeight).text = weight
        findViewById<TextView>(R.id.tvHeight).text = height

        // Set diagnosis
        findViewById<TextView>(R.id.tvDiagnosis).text = diagnosis

        // Set chief complaint
        findViewById<TextView>(R.id.tvChiefComplaint).text = chiefComplaint

        // Setup medicines RecyclerView
        val medicines = mutableListOf<MedicineItem>()
        for (i in medicineNames.indices) {
            medicines.add(
                MedicineItem(
                    name = medicineNames[i],
                    dosage = medicineDosages.getOrElse(i) { "" },
                    frequency = medicineFrequencies.getOrElse(i) { "" },
                    duration = medicineDurations.getOrElse(i) { "" },
                    instructions = medicineInstructions.getOrElse(i) { "" }
                )
            )
        }
        
        val rvMedicines = findViewById<RecyclerView>(R.id.rvMedicines)
        rvMedicines.layoutManager = LinearLayoutManager(this)
        rvMedicines.adapter = MedicineAdapter(medicines)

        // Setup lab tests
        val layoutLabTests = findViewById<LinearLayout>(R.id.layoutLabTests)
        labTests.forEachIndexed { index, test ->
            val testView = layoutInflater.inflate(R.layout.item_lab_test, layoutLabTests, false)
            testView.findViewById<TextView>(R.id.tvTestName).text = test
            testView.findViewById<TextView>(R.id.tvTestNumber).text = "${index + 1}"
            layoutLabTests.addView(testView)
        }

        // Set doctor's notes
        findViewById<TextView>(R.id.tvDoctorNotes).text = doctorNotes

        // Setup follow-up
        val cardFollowUp = findViewById<MaterialCardView>(R.id.cardFollowUp)
        if (followUpDate != null && followUpDate.isNotEmpty()) {
            findViewById<TextView>(R.id.tvFollowUpDate).text = followUpDate
            cardFollowUp.visibility = View.VISIBLE
        } else {
            cardFollowUp.visibility = View.GONE
        }

        // Setup buttons
        findViewById<MaterialButton>(R.id.btnDownload).setOnClickListener {
            Toast.makeText(this, "Downloading medical report...", Toast.LENGTH_SHORT).show()
        }

        findViewById<MaterialButton>(R.id.btnPrint).setOnClickListener {
            Toast.makeText(this, "Printing report...", Toast.LENGTH_SHORT).show()
        }
    }

    data class MedicineItem(
        val name: String,
        val dosage: String,
        val frequency: String,
        val duration: String,
        val instructions: String
    )

    inner class MedicineAdapter(
        private val medicines: List<MedicineItem>
    ) : RecyclerView.Adapter<MedicineAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvMedicineName: TextView = view.findViewById(R.id.tvMedicineName)
            val tvDosage: TextView = view.findViewById(R.id.tvDosage)
            val tvFrequency: TextView = view.findViewById(R.id.tvFrequency)
            val tvDuration: TextView = view.findViewById(R.id.tvDuration)
            val tvInstructions: TextView = view.findViewById(R.id.tvInstructions)
        }

        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
            val view = layoutInflater.inflate(R.layout.item_medicine, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val medicine = medicines[position]
            holder.tvMedicineName.text = medicine.name
            holder.tvDosage.text = medicine.dosage
            holder.tvFrequency.text = medicine.frequency
            holder.tvDuration.text = medicine.duration
            holder.tvInstructions.text = medicine.instructions
        }

        override fun getItemCount() = medicines.size
    }
}
