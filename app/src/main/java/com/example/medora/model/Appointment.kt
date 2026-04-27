package com.example.appointment.model

data class Appointment(
    val id: Int,
    val name: String,
    val dateTime: String,
    val hospital: String,
    val avatarRes: Int,
    val isUpcoming: Boolean
)
