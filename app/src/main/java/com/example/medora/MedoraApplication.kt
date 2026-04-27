package com.example.medora

import android.app.Application
import com.example.medora.network.RetrofitClient

class MedoraApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Retrofit client
        RetrofitClient.initialize(this)
    }
}
