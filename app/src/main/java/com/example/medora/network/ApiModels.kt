package com.example.medora.network

import com.google.gson.annotations.SerializedName

// Generic API Response
data class ApiResponse<T>(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: T?,
    @SerializedName("token") val token: String?
)

// Auth Responses
data class RegisterRequest(
    @SerializedName("fullName") val fullName: String,
    @SerializedName("email") val email: String,
    @SerializedName("phoneNumber") val phoneNumber: String,
    @SerializedName("password") val password: String,
    @SerializedName("role") val role: String = "patient"
)

data class LoginRequest(
    @SerializedName("email") val email: String?,
    @SerializedName("phoneNumber") val phoneNumber: String?,
    @SerializedName("password") val password: String
)

data class AuthResponse(
    @SerializedName("user") val user: User,
    @SerializedName("token") val token: String,
    @SerializedName("refreshToken") val refreshToken: String?
)

data class User(
    @SerializedName("_id") val _id: String,
    @SerializedName("id") val id: String? = null,
    @SerializedName("fullName") val fullName: String,
    @SerializedName("email") val email: String,
    @SerializedName("phoneNumber") val phoneNumber: String,
    @SerializedName("role") val role: String? = null,
    @SerializedName("profileImage") val profileImage: String? = null,
    @SerializedName("isVerified") val isVerified: Boolean? = null,
    @SerializedName("isPhoneVerified") val isPhoneVerified: Boolean? = null,
    @SerializedName("gender") val gender: String? = null,
    @SerializedName("dateOfBirth") val dateOfBirth: String? = null,
    @SerializedName("bloodGroup") val bloodGroup: String? = null,
    @SerializedName("height") val height: Double? = null,
    @SerializedName("weight") val weight: Double? = null
) {
    // Convenience property to get the user ID
    val userId: String get() = id ?: _id
}

// Doctor Response
data class DoctorsResponse(
    @SerializedName("doctors") val doctors: List<Doctor>,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("currentPage") val currentPage: Int,
    @SerializedName("total") val total: Int
)

data class Doctor(
    @SerializedName("_id") val id: String,
    @SerializedName("fullName") val fullName: String,
    @SerializedName("specialization") val specialization: String,
    @SerializedName("experience") val experience: Int,
    @SerializedName("qualification") val qualification: String?,
    @SerializedName("profileImage") val profileImage: String?,
    @SerializedName("rating") val rating: Double?,
    @SerializedName("consultationFee") val consultationFee: Double?,
    @SerializedName("availability") val availability: List<Availability>?
)

data class Availability(
    @SerializedName("day") val day: String,
    @SerializedName("slots") val slots: List<TimeSlot>
)

data class TimeSlot(
    @SerializedName("startTime") val startTime: String,
    @SerializedName("endTime") val endTime: String,
    @SerializedName("isAvailable") val isAvailable: Boolean
)

// Appointment Response
data class BookAppointmentRequest(
    @SerializedName("doctorId") val doctorId: String,
    @SerializedName("appointmentDate") val appointmentDate: String,
    @SerializedName("appointmentTime") val appointmentTime: String,
    @SerializedName("appointmentType") val appointmentType: String,
    @SerializedName("symptoms") val symptoms: String?,
    @SerializedName("notes") val notes: String?,
    @SerializedName("reason") val reason: String
)

data class AppointmentsResponse(
    @SerializedName("appointments") val appointments: List<Appointment>,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("currentPage") val currentPage: Int,
    @SerializedName("total") val total: Int
)

data class Appointment(
    @SerializedName("_id") val id: String,
    @SerializedName("doctorId") val doctor: Doctor?,
    @SerializedName("appointmentDate") val appointmentDate: String,
    @SerializedName("appointmentTime") val appointmentTime: String,
    @SerializedName("consultationType") val consultationType: String,
    @SerializedName("status") val status: String,
    @SerializedName("reason") val reason: String?,
    @SerializedName("symptoms") val symptoms: List<String>?,
    @SerializedName("notes") val notes: String?,
    @SerializedName("cancelReason") val cancelReason: String?
)

// Medicine Response
data class MedicinesResponse(
    @SerializedName("medicines") val medicines: List<Medicine>,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("currentPage") val currentPage: Int,
    @SerializedName("total") val total: Int
)

data class Medicine(
    @SerializedName("_id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("manufacturer") val manufacturer: String?,
    @SerializedName("price") val price: Double,
    @SerializedName("category") val category: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("image") val image: String?,
    @SerializedName("stock") val stock: Int?,
    @SerializedName("requiresPrescription") val requiresPrescription: Boolean?
)

// Order Response
data class CreateOrderRequest(
    @SerializedName("items") val items: List<OrderItem>,
    @SerializedName("shippingAddress") val shippingAddress: Address,
    @SerializedName("paymentMethod") val paymentMethod: String,
    @SerializedName("totalAmount") val totalAmount: Double
)

data class OrderItem(
    @SerializedName("medicineId") val medicineId: String,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("price") val price: Double
)

data class Address(
    @SerializedName("_id") val id: String? = null,
    @SerializedName("type") val type: String?,
    @SerializedName("fullName") val fullName: String?,
    @SerializedName("phoneNumber") val phoneNumber: String?,
    @SerializedName("addressLine1") val addressLine1: String,
    @SerializedName("addressLine2") val addressLine2: String?,
    @SerializedName("city") val city: String,
    @SerializedName("state") val state: String,
    @SerializedName("pincode") val pincode: String,
    @SerializedName("isDefault") val isDefault: Boolean?
)

data class OrdersResponse(
    @SerializedName("orders") val orders: List<Order>,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("currentPage") val currentPage: Int,
    @SerializedName("total") val total: Int
)

data class Order(
    @SerializedName("_id") val id: String,
    @SerializedName("orderId") val orderId: String,
    @SerializedName("items") val items: List<OrderItemDetail>,
    @SerializedName("pricing") val pricing: Pricing,
    @SerializedName("orderStatus") val orderStatus: String,
    @SerializedName("payment") val payment: Payment,
    @SerializedName("shippingAddress") val deliveryAddress: Address,
    @SerializedName("createdAt") val createdAt: String
)

data class OrderItemDetail(
    @SerializedName("medicineId") val medicineId: String,
    @SerializedName("medicineName") val medicineName: String,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("price") val price: Double
)

data class Pricing(
    @SerializedName("itemsTotal") val subtotal: Double,
    @SerializedName("discount") val discount: Double,
    @SerializedName("deliveryCharge") val deliveryFee: Double,
    @SerializedName("tax") val tax: Double,
    @SerializedName("totalAmount") val totalAmount: Double
)

data class Payment(
    @SerializedName("method") val method: String,
    @SerializedName("status") val status: String
)

// Health Tracking
data class HealthDataRequest(
    @SerializedName("date") val date: String,
    @SerializedName("steps") val steps: Int?,
    @SerializedName("heartRate") val heartRate: Int?,
    @SerializedName("weight") val weight: Double?,
    @SerializedName("sleep") val sleep: Double?,
    @SerializedName("calories") val calories: Int?,
    @SerializedName("water") val water: Double?
)

data class HealthData(
    @SerializedName("_id") val id: String,
    @SerializedName("date") val date: String,
    @SerializedName("steps") val steps: StepsData?,
    @SerializedName("heartRate") val heartRate: List<HeartRateData>?,
    @SerializedName("weight") val weight: WeightData?,
    @SerializedName("sleep") val sleep: SleepData?,
    @SerializedName("water") val water: WaterData?,
    @SerializedName("calories") val calories: CaloriesData?
)

data class StepsData(
    @SerializedName("count") val count: Int
)

data class HeartRateData(
    @SerializedName("bpm") val bpm: Int,
    @SerializedName("time") val time: String
)

data class WeightData(
    @SerializedName("value") val value: Double
)

data class SleepData(
    @SerializedName("duration") val duration: Double
)

data class WaterData(
    @SerializedName("intake") val intake: Int
)

data class CaloriesData(
    @SerializedName("consumed") val consumed: Int
)

// AI Chat
data class ChatRequest(
    @SerializedName("message") val message: String,
    @SerializedName("sessionId") val sessionId: String?
)

data class ChatResponse(
    @SerializedName("sessionId") val sessionId: String,
    @SerializedName("message") val message: String,
    @SerializedName("tokensUsed") val tokensUsed: Int?
)

data class ChatHistoryResponse(
    @SerializedName("sessions") val sessions: List<ChatSession>,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("currentPage") val currentPage: Int,
    @SerializedName("total") val total: Int
)

data class ChatSession(
    @SerializedName("_id") val id: String,
    @SerializedName("sessionId") val sessionId: String,
    @SerializedName("messages") val messages: List<ChatMessage>,
    @SerializedName("createdAt") val createdAt: String
)

data class ChatMessage(
    @SerializedName("role") val role: String,
    @SerializedName("content") val content: String,
    @SerializedName("timestamp") val timestamp: String
)

// Health Insights
data class HealthInsightsResponse(
    @SerializedName("insights") val insights: List<HealthInsight>,
    @SerializedName("generatedAt") val generatedAt: String?,
    @SerializedName("basedOnDays") val basedOnDays: Int?
)

data class HealthInsight(
    @SerializedName("type") val type: String, // info, warning, suggestion
    @SerializedName("category") val category: String,
    @SerializedName("message") val message: String,
    @SerializedName("priority") val priority: String, // low, medium, high
    @SerializedName("icon") val icon: String,
    @SerializedName("timestamp") val timestamp: String?
)

data class Medication(
    @SerializedName("_id") val _id: String? = null,
    @SerializedName("name") val name: String,
    @SerializedName("dosage") val dosage: String,
    @SerializedName("frequency") val frequency: String,
    @SerializedName("startDate") val startDate: String? = null
)

data class EmergencyContact(
    @SerializedName("_id") val _id: String? = null,
    @SerializedName("name") val name: String,
    @SerializedName("relationship") val relationship: String? = null,
    @SerializedName("phoneNumber") val phoneNumber: String,
    @SerializedName("email") val email: String? = null
)

data class EmergencySOSResponse(
    @SerializedName("sosId") val sosId: String,
    @SerializedName("timestamp") val timestamp: String,
    @SerializedName("emergencyData") val emergencyData: Map<String, Any>?
)

data class HealthReport(
    @SerializedName("period") val period: String,
    @SerializedName("startDate") val startDate: String,
    @SerializedName("endDate") val endDate: String,
    @SerializedName("user") val user: ReportUser?,
    @SerializedName("summary") val summary: ReportSummary,
    @SerializedName("metrics") val metrics: ReportMetrics,
    @SerializedName("trends") val trends: ReportTrends?,
    @SerializedName("dataPoints") val dataPoints: Int
)

data class ReportUser(
    @SerializedName("name") val name: String?,
    @SerializedName("age") val age: Int?,
    @SerializedName("gender") val gender: String?,
    @SerializedName("bloodGroup") val bloodGroup: String?
)

data class ReportSummary(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("issues") val issues: List<String>?
)

data class ReportMetrics(
    @SerializedName("heartRate") val heartRate: MetricDetail?,
    @SerializedName("bloodPressure") val bloodPressure: BPMetric?,
    @SerializedName("steps") val steps: MetricDetail?,
    @SerializedName("calories") val calories: MetricDetail?,
    @SerializedName("sleep") val sleep: MetricDetail?,
    @SerializedName("weight") val weight: WeightMetric?,
    @SerializedName("oxygenSaturation") val oxygenSaturation: MetricDetail?
)

data class MetricDetail(
    @SerializedName("average") val average: String?,
    @SerializedName("min") val min: Int?,
    @SerializedName("max") val max: Int?,
    @SerializedName("total") val total: Int?,
    @SerializedName("unit") val unit: String
)

data class BPMetric(
    @SerializedName("average") val average: String,
    @SerializedName("systolic") val systolic: Int,
    @SerializedName("diastolic") val diastolic: Int,
    @SerializedName("unit") val unit: String
)

data class WeightMetric(
    @SerializedName("average") val average: String?,
    @SerializedName("latest") val latest: Double?,
    @SerializedName("unit") val unit: String
)

data class ReportTrends(
    @SerializedName("heartRateStable") val heartRateStable: Boolean,
    @SerializedName("bloodPressureControlled") val bloodPressureControlled: Boolean,
    @SerializedName("activeLifestyle") val activeLifestyle: Boolean,
    @SerializedName("goodSleepPattern") val goodSleepPattern: Boolean
)

data class ExportReportResponse(
    @SerializedName("format") val format: String,
    @SerializedName("period") val period: String,
    @SerializedName("userId") val userId: String
)

// Health Tracking Models
data class HealthTrackingData(
    @SerializedName("_id") val id: String?,
    @SerializedName("userId") val userId: String,
    @SerializedName("date") val date: String,
    @SerializedName("steps") val steps: Steps?,
    @SerializedName("heartRate") val heartRate: List<HeartRateEntry>?,
    @SerializedName("bloodPressure") val bloodPressure: List<BloodPressureEntry>?,
    @SerializedName("weight") val weight: WeightEntry?,
    @SerializedName("sleep") val sleep: SleepEntry?,
    @SerializedName("calories") val calories: CaloriesEntry?,
    @SerializedName("water") val water: WaterEntry?,
    @SerializedName("oxygenSaturation") val oxygenSaturation: List<OxygenEntry>?
)

data class Steps(
    @SerializedName("count") val count: Int
)

data class HeartRateEntry(
    @SerializedName("time") val time: String,
    @SerializedName("bpm") val bpm: Int
)

data class BloodPressureEntry(
    @SerializedName("systolic") val systolic: Int,
    @SerializedName("diastolic") val diastolic: Int,
    @SerializedName("time") val time: String
)

data class WeightEntry(
    @SerializedName("value") val value: Double,
    @SerializedName("timestamp") val timestamp: String
)

data class SleepEntry(
    @SerializedName("duration") val duration: Double
)

data class CaloriesEntry(
    @SerializedName("consumed") val consumed: Int
)

data class WaterEntry(
    @SerializedName("intake") val intake: Int
)

data class OxygenEntry(
    @SerializedName("time") val time: String,
    @SerializedName("level") val level: Int
)

data class HealthAnalytics(
    @SerializedName("period") val period: String,
    @SerializedName("totalRecords") val totalRecords: Int,
    @SerializedName("averages") val averages: Averages,
    @SerializedName("trends") val trends: Trends
)

data class Averages(
    @SerializedName("steps") val steps: Int,
    @SerializedName("heartRate") val heartRate: Int,
    @SerializedName("weight") val weight: Double,
    @SerializedName("sleep") val sleep: Double,
    @SerializedName("calories") val calories: Int,
    @SerializedName("water") val water: Int
)

data class Trends(
    @SerializedName("steps") val steps: List<TrendData>,
    @SerializedName("heartRate") val heartRate: List<TrendData>,
    @SerializedName("weight") val weight: List<TrendData>,
    @SerializedName("sleep") val sleep: List<TrendData>
)

data class TrendData(
    @SerializedName("date") val date: String,
    @SerializedName("value") val value: Double
)

data class AddHealthDataRequest(
    @SerializedName("date") val date: String,
    @SerializedName("steps") val steps: Int?,
    @SerializedName("heartRate") val heartRate: Int?,
    @SerializedName("bloodPressure") val bloodPressure: BPData?,
    @SerializedName("weight") val weight: Double?,
    @SerializedName("sleep") val sleep: Double?,
    @SerializedName("calories") val calories: Int?,
    @SerializedName("water") val water: Double?
)

data class BPData(
    @SerializedName("systolic") val systolic: Int,
    @SerializedName("diastolic") val diastolic: Int
)

