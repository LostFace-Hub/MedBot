package com.example.medora.network

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private var retrofit: Retrofit? = null
    private var apiService: ApiService? = null
    
    fun initialize(context: Context) {
        if (retrofit == null) {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            
            val client = OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(context))
                .addInterceptor(loggingInterceptor)
                .connectTimeout(ApiConfig.TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(ApiConfig.TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(ApiConfig.TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .build()
            
            retrofit = Retrofit.Builder()
                .baseUrl(ApiConfig.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            
            apiService = retrofit!!.create(ApiService::class.java)
        }
    }
    
    fun getApiService(): ApiService {
        if (apiService == null) {
            throw IllegalStateException("RetrofitClient not initialized. Call initialize(context) first.")
        }
        return apiService!!
    }
}
