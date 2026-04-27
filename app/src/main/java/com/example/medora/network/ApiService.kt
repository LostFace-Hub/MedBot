package com.example.medora.network

import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    
    // ==================== AUTHENTICATION ====================
    
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<AuthResponse>>
    
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<AuthResponse>>
    
    @POST("auth/verify-otp")
    suspend fun verifyOtp(
        @Body request: Map<String, String>
    ): Response<ApiResponse<Any>>
    
    @POST("auth/forgot-password")
    suspend fun forgotPassword(
        @Body request: Map<String, String>
    ): Response<ApiResponse<Any>>
    
    // ==================== USER PROFILE ====================
    
    @GET("user/profile")
    suspend fun getProfile(): Response<ApiResponse<User>>
    
    @PUT("user/profile")
    suspend fun updateProfile(
        @Body request: Map<String, Any>
    ): Response<ApiResponse<User>>
    
    @GET("user/address")
    suspend fun getAddresses(): Response<ApiResponse<List<Address>>>
    
    @POST("user/address")
    suspend fun addAddress(@Body address: Address): Response<ApiResponse<Address>>
    
    @PUT("user/address/{addressId}")
    suspend fun updateAddress(
        @Path("addressId") addressId: String,
        @Body address: Address
    ): Response<ApiResponse<Address>>
    
    @DELETE("user/address/{addressId}")
    suspend fun deleteAddress(
        @Path("addressId") addressId: String
    ): Response<ApiResponse<Any>>
    
    // ==================== DOCTORS ====================
    
    @GET("doctors")
    suspend fun getDoctors(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10,
        @Query("specialization") specialization: String? = null,
        @Query("search") search: String? = null
    ): Response<ApiResponse<DoctorsResponse>>
    
    @GET("doctors/{doctorId}")
    suspend fun getDoctorById(
        @Path("doctorId") doctorId: String
    ): Response<ApiResponse<Doctor>>
    
    @GET("doctors/{doctorId}/slots")
    suspend fun getDoctorSlots(
        @Path("doctorId") doctorId: String,
        @Query("date") date: String
    ): Response<ApiResponse<List<TimeSlot>>>
    
    // ==================== APPOINTMENTS ====================
    
    @POST("appointments")
    suspend fun bookAppointment(
        @Body request: BookAppointmentRequest
    ): Response<ApiResponse<Appointment>>
    
    @GET("appointments")
    suspend fun getAppointments(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10,
        @Query("status") status: String? = null
    ): Response<ApiResponse<AppointmentsResponse>>
    
    @GET("appointments/{appointmentId}")
    suspend fun getAppointmentById(
        @Path("appointmentId") appointmentId: String
    ): Response<ApiResponse<Appointment>>
    
    @PUT("appointments/{appointmentId}/reschedule")
    suspend fun rescheduleAppointment(
        @Path("appointmentId") appointmentId: String,
        @Body request: Map<String, String>
    ): Response<ApiResponse<Appointment>>
    
    @PUT("appointments/{appointmentId}/cancel")
    suspend fun cancelAppointment(
        @Path("appointmentId") appointmentId: String,
        @Body request: Map<String, String>
    ): Response<ApiResponse<Appointment>>
    
    // ==================== MEDICINES ====================
    
    @GET("medicines")
    suspend fun getMedicines(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10,
        @Query("category") category: String? = null,
        @Query("search") search: String? = null
    ): Response<ApiResponse<MedicinesResponse>>
    
    @GET("medicines/{medicineId}")
    suspend fun getMedicineById(
        @Path("medicineId") medicineId: String
    ): Response<ApiResponse<Medicine>>
    
    // ==================== ORDERS ====================
    
    @POST("orders")
    suspend fun createOrder(
        @Body request: CreateOrderRequest
    ): Response<ApiResponse<Order>>
    
    @GET("orders")
    suspend fun getOrders(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10,
        @Query("status") status: String? = null
    ): Response<ApiResponse<OrdersResponse>>
    
    @GET("orders/{orderId}")
    suspend fun getOrderById(
        @Path("orderId") orderId: String
    ): Response<ApiResponse<Order>>
    
    @GET("orders/{orderId}/track")
    suspend fun trackOrder(
        @Path("orderId") orderId: String
    ): Response<ApiResponse<Order>>
    
    @PUT("orders/{orderId}/cancel")
    suspend fun cancelOrder(
        @Path("orderId") orderId: String
    ): Response<ApiResponse<Order>>
    
    // ==================== HEALTH TRACKING ====================
    
    @POST("health")
    suspend fun addHealthData(
        @Body request: HealthDataRequest
    ): Response<ApiResponse<HealthData>>
    
    @POST("health/sync")
    suspend fun syncHealthData(
        @Body request: Map<String, Any?>
    ): Response<ApiResponse<Any>>
    
    @GET("health")
    suspend fun getHealthData(
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): Response<ApiResponse<List<HealthData>>>
    
    @GET("health/goals")
    suspend fun getHealthGoals(): Response<ApiResponse<Any>>
    
    @POST("health/goals")
    suspend fun setHealthGoals(
        @Body request: Map<String, Int>
    ): Response<ApiResponse<Any>>
    
    // ==================== AI ASSISTANT ====================
    
    @POST("ai/chat")
    suspend fun chatWithAI(
        @Body request: ChatRequest
    ): Response<ApiResponse<ChatResponse>>
    
    @POST("ai/quick-action")
    suspend fun quickAction(
        @Body request: Map<String, String>
    ): Response<ApiResponse<ChatResponse>>
    
    @GET("ai/history")
    suspend fun getChatHistory(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): Response<ApiResponse<ChatHistoryResponse>>
    
    @GET("ai/history/{sessionId}")
    suspend fun getChatSession(
        @Path("sessionId") sessionId: String
    ): Response<ApiResponse<ChatSession>>
    
    @DELETE("ai/history/{sessionId}")
    suspend fun deleteChatSession(
        @Path("sessionId") sessionId: String
    ): Response<ApiResponse<Any>>
    
    @GET("ai/insights")
    suspend fun getHealthInsights(): Response<ApiResponse<HealthInsightsResponse>>
    
    // ==================== MEDICATIONS ====================
    
    @GET("medications")
    suspend fun getMedications(): Response<ApiResponse<List<Medication>>>
    
    @POST("medications")
    suspend fun addMedication(
        @Body medication: Medication
    ): Response<ApiResponse<List<Medication>>>
    
    @PUT("medications/{medicationId}")
    suspend fun updateMedication(
        @Path("medicationId") medicationId: String,
        @Body medication: Medication
    ): Response<ApiResponse<List<Medication>>>
    
    @DELETE("medications/{medicationId}")
    suspend fun deleteMedication(
        @Path("medicationId") medicationId: String
    ): Response<ApiResponse<List<Medication>>>
    
    // ==================== EMERGENCY ====================
    
    @POST("emergency/sos")
    suspend fun triggerSOS(
        @Body request: Map<String, Any?>
    ): Response<ApiResponse<EmergencySOSResponse>>
    
    @GET("emergency/contacts")
    suspend fun getEmergencyContacts(): Response<ApiResponse<List<EmergencyContact>>>
    
    @POST("emergency/contacts")
    suspend fun addEmergencyContact(
        @Body contact: EmergencyContact
    ): Response<ApiResponse<List<EmergencyContact>>>
    
    @DELETE("emergency/contacts/{contactId}")
    suspend fun deleteEmergencyContact(
        @Path("contactId") contactId: String
    ): Response<ApiResponse<List<EmergencyContact>>>
    
    // ==================== HEALTH REPORTS ====================
    
        @GET("api/reports")
    suspend fun getHealthReport(@Query("period") period: String): Response<ApiResponse<HealthReport>>

    @GET("api/reports/export")
    suspend fun exportHealthReport(
        @Query("period") period: String,
        @Query("format") format: String
    ): Response<ApiResponse<ExportReportResponse>>

    // Health Tracking endpoints
    @GET("api/tracking")
    suspend fun getHealthTracking(
        @Query("startDate") startDate: String?,
        @Query("endDate") endDate: String?
    ): Response<ApiResponse<List<HealthTrackingData>>>

    @GET("api/tracking/analytics")
    suspend fun getHealthAnalytics(@Query("period") period: String): Response<ApiResponse<HealthAnalytics>>

    @POST("api/tracking")
    suspend fun addHealthData(@Body data: AddHealthDataRequest): Response<ApiResponse<HealthTrackingData>>
}
