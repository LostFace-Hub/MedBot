package com.example.medora.network

object ApiConfig {
    // Change this to your computer's IP address when testing on a physical device
    // Use 10.0.2.2 for Android Emulator to access localhost
    const val BASE_URL = "http://medora-be.onrender.com/api/"

    // For physical device, use your computer's IP:
    // const val BASE_URL = "http://192.168.1.XXX:5001/api/"

    // For production:
    // const val BASE_URL = "https://your-domain.com/api/"

    const val TIMEOUT_SECONDS = 30L
}
