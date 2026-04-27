package com.example.medora.network

import android.content.Context
import android.content.SharedPreferences
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(context: Context) : Interceptor {
    private val prefs: SharedPreferences = context.getSharedPreferences("medora_prefs", Context.MODE_PRIVATE)
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = prefs.getString("auth_token", null)
        
        val request = if (!token.isNullOrEmpty()) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }
        
        return chain.proceed(request)
    }
}
