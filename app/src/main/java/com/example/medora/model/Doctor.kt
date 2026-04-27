package com.example.appointment.model

data class Doctor(
    val id: Int,
    val name: String,
    val speciality: String,
    val rating: Float,
    val avatarRes: Int,
    val availableToday: Boolean
)
