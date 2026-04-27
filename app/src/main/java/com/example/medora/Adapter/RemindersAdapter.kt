package com.example.medora

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView

data class Reminder(
    val id: Int,
    val title: String,
    val time: String,
    val description: String,
    var isEnabled: Boolean
)

class RemindersAdapter(
    private val context: Context,
    private val reminders: MutableList<Reminder>,
    private val onToggle: (Reminder, Boolean) -> Unit
) : RecyclerView.Adapter<RemindersAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.reminderTitle)
        val time: TextView = view.findViewById(R.id.reminderTime)
        val description: TextView = view.findViewById(R.id.reminderDescription)
        val switch: SwitchCompat = view.findViewById(R.id.reminderSwitch)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reminder, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reminder = reminders[position]
        holder.title.text = reminder.title
        holder.time.text = reminder.time
        holder.description.text = reminder.description
        holder.switch.isChecked = reminder.isEnabled

        holder.switch.setOnCheckedChangeListener { _, isChecked ->
            reminder.isEnabled = isChecked
            onToggle(reminder, isChecked)
        }
    }

    override fun getItemCount() = reminders.size
}
