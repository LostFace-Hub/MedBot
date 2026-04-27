package com.example.medora

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar

data class PastAppointment(
    val id: Int,
    val doctorName: String,
    val specialization: String,
    val dateTime: String,
    val place: String,
    val status: String,
    val avatarRes: Int,
    // Medical record data
    val diagnosis: String,
    val chiefComplaint: String,
    val prescribedMedicines: List<Medicine>,
    val labTests: List<String>,
    val vitalSigns: VitalSigns,
    val doctorNotes: String,
    val followUpDate: String?
)

data class Medicine(
    val name: String,
    val dosage: String,
    val frequency: String,
    val duration: String,
    val instructions: String
)

data class VitalSigns(
    val bloodPressure: String,
    val heartRate: String,
    val temperature: String,
    val weight: String,
    val height: String
)

class PastAppointmentsActivity : AppCompatActivity() {

    private lateinit var allAppointments: MutableList<PastAppointment>
    private lateinit var adapter: PastAppointmentAdapter
    private lateinit var etSearch: EditText
    private lateinit var tvResultsCount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_past_appointments)

        // Setup toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        etSearch = findViewById(R.id.etSearch)
        tvResultsCount = findViewById(R.id.tvResultsCount)

        // Initialize appointments data
        allAppointments = createSampleAppointments()

        // Setup RecyclerView
        setupRecyclerView()

        // Setup search
        setupSearch()

        // Setup bottom navigation
        setupBottomNavigation()

        // Update count
        updateResultsCount(allAppointments.size)
    }

    private fun createSampleAppointments(): MutableList<PastAppointment> {
        return mutableListOf(
            PastAppointment(
                id = 1,
                doctorName = "Dr. Sarah Johnson",
                specialization = "Cardiologist",
                dateTime = "Nov 10, 2024 at 4:30 PM",
                place = "Aster Hospital, Cardiology Wing",
                status = "Completed",
                avatarRes = R.drawable.ic_profile,
                diagnosis = "Hypertension (High Blood Pressure) - Stage 1",
                chiefComplaint = "Patient complained of persistent headaches, dizziness, and occasional chest discomfort for the past 2 weeks.",
                prescribedMedicines = listOf(
                    Medicine(
                        name = "Amlodipine",
                        dosage = "5mg",
                        frequency = "Once daily",
                        duration = "30 days",
                        instructions = "Take in the morning with water. Avoid grapefruit juice."
                    ),
                    Medicine(
                        name = "Aspirin",
                        dosage = "75mg",
                        frequency = "Once daily",
                        duration = "30 days",
                        instructions = "Take after dinner to reduce stomach irritation."
                    ),
                    Medicine(
                        name = "Atorvastatin",
                        dosage = "10mg",
                        frequency = "Once daily at night",
                        duration = "30 days",
                        instructions = "Take before bedtime for better cholesterol control."
                    )
                ),
                labTests = listOf(
                    "Lipid Profile (Cholesterol Test)",
                    "ECG (Electrocardiogram)",
                    "Blood Sugar (Fasting & PP)",
                    "Kidney Function Test"
                ),
                vitalSigns = VitalSigns(
                    bloodPressure = "145/92 mmHg",
                    heartRate = "78 bpm",
                    temperature = "98.4°F",
                    weight = "72 kg",
                    height = "165 cm"
                ),
                doctorNotes = "Patient shows early signs of hypertension. Blood pressure elevated but manageable with medication. Advised lifestyle modifications: reduce salt intake, regular exercise (30 min walking daily), stress management, and weight reduction. Monitor BP at home daily. Avoid smoking and excessive alcohol. Follow up in 2 weeks for medication adjustment if needed.",
                followUpDate = "Nov 24, 2024"
            ),
            PastAppointment(
                id = 2,
                doctorName = "Dr. Neha Kapoor",
                specialization = "Dermatologist",
                dateTime = "Oct 28, 2024 at 2:00 PM",
                place = "Skin Care Clinic, Downtown",
                status = "Completed",
                avatarRes = R.drawable.ic_profile,
                diagnosis = "Atopic Dermatitis (Eczema) - Moderate",
                chiefComplaint = "Patient presented with severe itching, red patches on arms and legs, dry flaky skin for 3 weeks. Condition worsened after exposure to cold weather.",
                prescribedMedicines = listOf(
                    Medicine(
                        name = "Hydrocortisone Cream",
                        dosage = "1% topical",
                        frequency = "Apply twice daily",
                        duration = "14 days",
                        instructions = "Apply thin layer on affected areas. Avoid face and open wounds."
                    ),
                    Medicine(
                        name = "Cetirizine",
                        dosage = "10mg",
                        frequency = "Once daily at bedtime",
                        duration = "14 days",
                        instructions = "Take with water. May cause drowsiness."
                    ),
                    Medicine(
                        name = "Moisturizing Lotion",
                        dosage = "As needed",
                        frequency = "3-4 times daily",
                        duration = "Continuous",
                        instructions = "Apply liberally after bath on damp skin. Use fragrance-free products."
                    )
                ),
                labTests = listOf(
                    "Skin Patch Test (Allergy Testing)",
                    "Complete Blood Count (CBC)",
                    "IgE Level Test"
                ),
                vitalSigns = VitalSigns(
                    bloodPressure = "120/78 mmHg",
                    heartRate = "72 bpm",
                    temperature = "98.2°F",
                    weight = "65 kg",
                    height = "162 cm"
                ),
                doctorNotes = "Moderate eczema flare-up triggered by environmental factors. Skin barrier is compromised. Prescribed topical steroids and antihistamines. Patient advised to avoid hot showers, use mild soaps, wear cotton clothing, and identify/avoid allergen triggers. Keep skin moisturized at all times. If symptoms persist after 2 weeks or worsen, return immediately.",
                followUpDate = "Nov 11, 2024"
            ),
            PastAppointment(
                id = 3,
                doctorName = "Dr. Anirudh Sinha",
                specialization = "Orthopedist",
                dateTime = "Oct 15, 2024 at 11:00 AM",
                place = "City Orthopedic Center",
                status = "Completed",
                avatarRes = R.drawable.ic_profile,
                diagnosis = "Lumbar Strain (Lower Back Pain)",
                chiefComplaint = "Patient reported acute lower back pain for 5 days, difficulty bending, pain radiating to right leg. Pain started after lifting heavy object at work.",
                prescribedMedicines = listOf(
                    Medicine(
                        name = "Diclofenac Sodium",
                        dosage = "50mg",
                        frequency = "Twice daily after meals",
                        duration = "7 days",
                        instructions = "Take with food to avoid stomach upset. Do not exceed recommended dose."
                    ),
                    Medicine(
                        name = "Methocarbamol",
                        dosage = "500mg",
                        frequency = "Three times daily",
                        duration = "7 days",
                        instructions = "Muscle relaxant. May cause drowsiness - avoid driving."
                    ),
                    Medicine(
                        name = "Vitamin B Complex",
                        dosage = "1 tablet",
                        frequency = "Once daily",
                        duration = "30 days",
                        instructions = "Take after breakfast for nerve health support."
                    )
                ),
                labTests = listOf(
                    "X-Ray Lumbar Spine (AP & Lateral views)",
                    "MRI Lower Back (if pain persists)"
                ),
                vitalSigns = VitalSigns(
                    bloodPressure = "128/82 mmHg",
                    heartRate = "75 bpm",
                    temperature = "98.6°F",
                    weight = "78 kg",
                    height = "175 cm"
                ),
                doctorNotes = "Acute lumbar strain due to improper lifting technique. No signs of disc herniation on physical examination. Prescribed NSAIDs and muscle relaxants for pain management. Patient advised: strict bed rest for 2-3 days, apply hot compress, avoid heavy lifting, practice proper posture, start physiotherapy after acute pain subsides. Ergonomic workplace adjustments recommended. Return if numbness, weakness, or loss of bladder control occurs.",
                followUpDate = "Oct 25, 2024"
            ),
            PastAppointment(
                id = 4,
                doctorName = "Dr. Michael Chen",
                specialization = "General Physician",
                dateTime = "Sep 30, 2024 at 5:00 PM",
                place = "Community Health Center",
                status = "Completed",
                avatarRes = R.drawable.ic_profile,
                diagnosis = "Acute Viral Pharyngitis (Sore Throat)",
                chiefComplaint = "Severe sore throat, difficulty swallowing, mild fever (100.2°F), body ache, and fatigue for 3 days. No cough or breathing difficulty.",
                prescribedMedicines = listOf(
                    Medicine(
                        name = "Paracetamol",
                        dosage = "650mg",
                        frequency = "Three times daily",
                        duration = "5 days",
                        instructions = "Take after meals for fever and body ache relief."
                    ),
                    Medicine(
                        name = "Chlorhexidine Gargle",
                        dosage = "15ml",
                        frequency = "Three times daily",
                        duration = "7 days",
                        instructions = "Gargle for 30 seconds after meals. Do not swallow."
                    ),
                    Medicine(
                        name = "Vitamin C",
                        dosage = "500mg",
                        frequency = "Twice daily",
                        duration = "7 days",
                        instructions = "Take with water to boost immunity."
                    )
                ),
                labTests = listOf(
                    "Throat Swab Culture",
                    "Complete Blood Count (CBC)"
                ),
                vitalSigns = VitalSigns(
                    bloodPressure = "118/76 mmHg",
                    heartRate = "80 bpm",
                    temperature = "100.2°F",
                    weight = "68 kg",
                    height = "170 cm"
                ),
                doctorNotes = "Viral pharyngitis, self-limiting condition. No bacterial infection signs. Symptomatic treatment prescribed. Patient advised to: drink plenty of warm fluids, rest voice, avoid cold beverages, steam inhalation twice daily, take adequate rest. Symptoms should improve in 3-5 days. Return if fever persists beyond 3 days or difficulty breathing develops.",
                followUpDate = null
            ),
            PastAppointment(
                id = 5,
                doctorName = "Dr. Priya Sharma",
                specialization = "Gynecologist",
                dateTime = "Sep 20, 2024 at 3:30 PM",
                place = "Women's Health Hospital",
                status = "Completed",
                avatarRes = R.drawable.ic_profile,
                diagnosis = "Iron Deficiency Anemia",
                chiefComplaint = "Patient reported persistent fatigue, weakness, pale skin, shortness of breath on exertion, and irregular heavy menstrual periods for 2 months.",
                prescribedMedicines = listOf(
                    Medicine(
                        name = "Ferrous Sulfate",
                        dosage = "325mg",
                        frequency = "Twice daily",
                        duration = "90 days",
                        instructions = "Take on empty stomach with orange juice. Avoid tea/coffee within 2 hours."
                    ),
                    Medicine(
                        name = "Folic Acid",
                        dosage = "5mg",
                        frequency = "Once daily",
                        duration = "90 days",
                        instructions = "Take with iron supplement for better absorption."
                    ),
                    Medicine(
                        name = "Vitamin B12",
                        dosage = "1000mcg",
                        frequency = "Once daily",
                        duration = "30 days",
                        instructions = "Sublingual tablet - place under tongue until dissolved."
                    )
                ),
                labTests = listOf(
                    "Complete Blood Count (CBC)",
                    "Serum Ferritin Level",
                    "Iron Studies (TIBC, Serum Iron)",
                    "Thyroid Function Test"
                ),
                vitalSigns = VitalSigns(
                    bloodPressure = "110/72 mmHg",
                    heartRate = "88 bpm",
                    temperature = "98.0°F",
                    weight = "58 kg",
                    height = "160 cm"
                ),
                doctorNotes = "Moderate iron deficiency anemia confirmed with Hemoglobin 9.2 g/dL. Likely due to heavy menstrual bleeding. Iron supplementation initiated. Patient counseled on iron-rich diet: green leafy vegetables, red meat, lentils, fortified cereals. Avoid calcium supplements with iron. Monitor for constipation (common side effect). Repeat CBC after 4 weeks. May need gynecological evaluation for menorrhagia management.",
                followUpDate = "Oct 18, 2024"
            ),
            PastAppointment(
                id = 6,
                doctorName = "Dr. Rajesh Kumar",
                specialization = "Dentist",
                dateTime = "Sep 5, 2024 at 10:00 AM",
                place = "Smile Dental Clinic",
                status = "Completed",
                avatarRes = R.drawable.ic_profile,
                diagnosis = "Dental Caries (Tooth Decay) - Left Lower Molar",
                chiefComplaint = "Patient complained of sharp pain in lower left tooth while eating, sensitivity to hot and cold, visible black spot on tooth surface for 2 weeks.",
                prescribedMedicines = listOf(
                    Medicine(
                        name = "Ibuprofen",
                        dosage = "400mg",
                        frequency = "Twice daily after meals",
                        duration = "3 days",
                        instructions = "For pain relief. Take with food to avoid stomach upset."
                    ),
                    Medicine(
                        name = "Chlorhexidine Mouthwash",
                        dosage = "10ml",
                        frequency = "Twice daily",
                        duration = "7 days",
                        instructions = "Rinse mouth for 30 seconds after brushing. Do not eat for 30 min after use."
                    ),
                    Medicine(
                        name = "Amoxicillin",
                        dosage = "500mg",
                        frequency = "Three times daily",
                        duration = "5 days",
                        instructions = "Complete full course even if pain subsides. Take with meals."
                    )
                ),
                labTests = listOf(
                    "Dental X-Ray (Periapical)"
                ),
                vitalSigns = VitalSigns(
                    bloodPressure = "122/80 mmHg",
                    heartRate = "70 bpm",
                    temperature = "98.4°F",
                    weight = "70 kg",
                    height = "168 cm"
                ),
                doctorNotes = "Deep cavity in tooth #36 (lower left first molar) with pulp involvement. Root canal treatment recommended. Temporary filling placed after cleaning cavity. Patient scheduled for RCT next week. Advised proper oral hygiene: brush twice daily, floss regularly, avoid sugary foods, use fluoride toothpaste. Avoid chewing on left side until RCT completed. Return immediately if swelling or severe pain occurs.",
                followUpDate = "Sep 12, 2024"
            )
        )
    }

    private fun setupRecyclerView() {
        val rvPastAppointments = findViewById<RecyclerView>(R.id.rvPastAppointments)
        adapter = PastAppointmentAdapter(allAppointments) { appointment ->
            openAppointmentDetails(appointment)
        }
        rvPastAppointments.layoutManager = LinearLayoutManager(this)
        rvPastAppointments.adapter = adapter
    }

    private fun setupSearch() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filterAppointments(s.toString())
            }
        })
    }

    private fun filterAppointments(query: String) {
        val filteredList = if (query.isEmpty()) {
            allAppointments
        } else {
            allAppointments.filter {
                it.doctorName.contains(query, ignoreCase = true) ||
                        it.specialization.contains(query, ignoreCase = true) ||
                        it.dateTime.contains(query, ignoreCase = true) ||
                        it.place.contains(query, ignoreCase = true)
            }
        }
        adapter.updateList(filteredList)
        updateResultsCount(filteredList.size)
    }

    private fun updateResultsCount(count: Int) {
        tvResultsCount.text = "$count ${if (count == 1) "appointment" else "appointments"} found"
    }

    private fun openAppointmentDetails(appointment: PastAppointment) {
        val intent = Intent(this, AppointmentDetailsActivity::class.java)
        intent.putExtra("APPOINTMENT_ID", appointment.id)
        intent.putExtra("DOCTOR_NAME", appointment.doctorName)
        intent.putExtra("SPECIALIZATION", appointment.specialization)
        intent.putExtra("DATE_TIME", appointment.dateTime)
        intent.putExtra("PLACE", appointment.place)
        intent.putExtra("DIAGNOSIS", appointment.diagnosis)
        intent.putExtra("CHIEF_COMPLAINT", appointment.chiefComplaint)
        intent.putExtra("DOCTOR_NOTES", appointment.doctorNotes)
        intent.putExtra("FOLLOW_UP_DATE", appointment.followUpDate)
        intent.putExtra("BP", appointment.vitalSigns.bloodPressure)
        intent.putExtra("HR", appointment.vitalSigns.heartRate)
        intent.putExtra("TEMP", appointment.vitalSigns.temperature)
        intent.putExtra("WEIGHT", appointment.vitalSigns.weight)
        intent.putExtra("HEIGHT", appointment.vitalSigns.height)
        
        // Pass medicines
        val medicineNames = ArrayList(appointment.prescribedMedicines.map { it.name })
        val medicineDosages = ArrayList(appointment.prescribedMedicines.map { it.dosage })
        val medicineFrequencies = ArrayList(appointment.prescribedMedicines.map { it.frequency })
        val medicineDurations = ArrayList(appointment.prescribedMedicines.map { it.duration })
        val medicineInstructions = ArrayList(appointment.prescribedMedicines.map { it.instructions })
        
        intent.putStringArrayListExtra("MEDICINE_NAMES", medicineNames)
        intent.putStringArrayListExtra("MEDICINE_DOSAGES", medicineDosages)
        intent.putStringArrayListExtra("MEDICINE_FREQUENCIES", medicineFrequencies)
        intent.putStringArrayListExtra("MEDICINE_DURATIONS", medicineDurations)
        intent.putStringArrayListExtra("MEDICINE_INSTRUCTIONS", medicineInstructions)
        
        // Pass lab tests
        intent.putStringArrayListExtra("LAB_TESTS", ArrayList(appointment.labTests))
        
        startActivity(intent)
    }

    private fun setupBottomNavigation() {
        val navHome = findViewById<LinearLayout>(R.id.navHome)
        val navTracking = findViewById<LinearLayout>(R.id.navTracking)
        val navAppt = findViewById<LinearLayout>(R.id.navAppt)
        val navOrders = findViewById<LinearLayout>(R.id.navOrders)
        val navAi = findViewById<LinearLayout>(R.id.navAi)

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
            startActivity(Intent(this, OrdersActivity::class.java))
            finish()
        }

        navAi.setOnClickListener {
            Toast.makeText(this, "AI Assistant - Coming Soon", Toast.LENGTH_SHORT).show()
        }
    }

    inner class PastAppointmentAdapter(
        private var appointments: List<PastAppointment>,
        private val onItemClick: (PastAppointment) -> Unit
    ) : RecyclerView.Adapter<PastAppointmentAdapter.ViewHolder>() {

        inner class ViewHolder(view: android.view.View) : RecyclerView.ViewHolder(view) {
            val ivAvatar: ImageView = view.findViewById(R.id.ivAvatar)
            val tvName: TextView = view.findViewById(R.id.tvName)
            val tvSpecialization: TextView = view.findViewById(R.id.tvSpecialization)
            val tvDate: TextView = view.findViewById(R.id.tvDate)
            val tvPlace: TextView = view.findViewById(R.id.tvPlace)
            val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        }

        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
            val view = android.view.LayoutInflater.from(parent.context)
                .inflate(R.layout.item_past_appointment, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val appointment = appointments[position]
            holder.ivAvatar.setImageResource(appointment.avatarRes)
            holder.tvName.text = appointment.doctorName
            holder.tvSpecialization.text = appointment.specialization
            holder.tvDate.text = appointment.dateTime
            holder.tvPlace.text = appointment.place
            holder.tvStatus.text = appointment.status

            holder.itemView.setOnClickListener {
                onItemClick(appointment)
            }
        }

        override fun getItemCount() = appointments.size

        fun updateList(newList: List<PastAppointment>) {
            appointments = newList
            notifyDataSetChanged()
        }
    }
}
