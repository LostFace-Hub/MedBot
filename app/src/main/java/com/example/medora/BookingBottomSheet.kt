package com.example.medora

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medora.Adapter.DoctorAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BookingBottomSheet(private val doctors: List<Doctor>) : BottomSheetDialogFragment() {

    private var selectedSlot: String? = null
    private var selectedDoctor: Doctor? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.bottom_sheet_booking, container, false)
        val rv = v.findViewById<RecyclerView>(R.id.rvDoctors)
        rv.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        rv.adapter = DoctorAdapter(doctors) { doc ->
            selectedDoctor = doc
            Toast.makeText(context, doc.name + " selected", Toast.LENGTH_SHORT).show()
        }

        // Close button
        v.findViewById<ImageView>(R.id.btnClose)?.setOnClickListener {
            dismiss()
        }

        val slot1 = v.findViewById<CardView>(R.id.slot1)
        val slot2 = v.findViewById<CardView>(R.id.slot2)
        val slot3 = v.findViewById<CardView>(R.id.slot3)
        val confirm = v.findViewById<Button>(R.id.btnConfirm)

        val slotClick: (CardView, String)->Unit = { card, slot ->
            selectedSlot = slot
            slot1.alpha = if (card == slot1) 1f else 0.5f
            slot2.alpha = if (card == slot2) 1f else 0.5f
            slot3.alpha = if (card == slot3) 1f else 0.5f
        }

        slot1.setOnClickListener { slotClick(slot1, "9:00 AM") }
        slot2.setOnClickListener { slotClick(slot2, "10:30 AM") }
        slot3.setOnClickListener { slotClick(slot3, "2:30 PM") }

        confirm.setOnClickListener {
            if (selectedDoctor == null) {
                Toast.makeText(context, "Please select a doctor", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (selectedSlot == null) {
                Toast.makeText(context, "Please select a slot", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Toast.makeText(context, "Booked ${selectedDoctor!!.name} at $selectedSlot", Toast.LENGTH_LONG).show()
            dismiss()
        }

        return v
    }
}
