package com.example.medora.Adapter

import com.example.medora.R

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.medora.model.OnboardingModel

class OnboardingAdapter(
    private val list: List<OnboardingModel>
) : RecyclerView.Adapter<OnboardingAdapter.OnboardViewHolder>() {

    inner class OnboardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView = view.findViewById(R.id.onboardImg)
        val title: TextView = view.findViewById(R.id.onboardTitle)
        val subtitle: TextView = view.findViewById(R.id.onboardSubtitle)
        val desc: TextView = view.findViewById(R.id.onboardDesc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_onboarding, parent, false)
        return OnboardViewHolder(view)
    }

    override fun onBindViewHolder(holder: OnboardViewHolder, position: Int) {
        val item = list[position]
        holder.img.setImageResource(item.image)
        holder.title.text = item.title
        holder.subtitle.text = item.subtitle
        holder.desc.text = item.desc
    }

    override fun getItemCount(): Int = list.size
}