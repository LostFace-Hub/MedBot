package com.example.medora.utils

import android.content.Context
import android.content.SharedPreferences

object SessionManager {
    private const val PREF_NAME = "medora_prefs"
    private const val KEY_AUTH_TOKEN = "auth_token"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_EMAIL = "user_email"
    private const val KEY_USER_PHONE = "user_phone"
    private const val KEY_USER_ROLE = "user_role"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"
    
    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }
    
    fun saveAuthToken(context: Context, token: String) {
        getPreferences(context).edit().putString(KEY_AUTH_TOKEN, token).apply()
    }
    
    fun getAuthToken(context: Context): String? {
        return getPreferences(context).getString(KEY_AUTH_TOKEN, null)
    }
    
    fun saveUserData(context: Context, userId: String, name: String, email: String, phone: String, role: String) {
        getPreferences(context).edit().apply {
            putString(KEY_USER_ID, userId)
            putString(KEY_USER_NAME, name)
            putString(KEY_USER_EMAIL, email)
            putString(KEY_USER_PHONE, phone)
            putString(KEY_USER_ROLE, role)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }
    
    fun getUserId(context: Context): String? {
        return getPreferences(context).getString(KEY_USER_ID, null)
    }
    
    fun getUserName(context: Context): String? {
        return getPreferences(context).getString(KEY_USER_NAME, null)
    }
    
    fun saveUserProfile(context: Context, fullName: String) {
        getPreferences(context).edit().putString(KEY_USER_NAME, fullName).apply()
    }
    
    fun getUserEmail(context: Context): String? {
        return getPreferences(context).getString(KEY_USER_EMAIL, null)
    }
    
    fun getUserPhone(context: Context): String? {
        return getPreferences(context).getString(KEY_USER_PHONE, null)
    }
    
    fun getUserRole(context: Context): String? {
        return getPreferences(context).getString(KEY_USER_ROLE, null)
    }
    
    fun isLoggedIn(context: Context): Boolean {
        return getPreferences(context).getBoolean(KEY_IS_LOGGED_IN, false)
    }
    
    fun clearSession(context: Context) {
        getPreferences(context).edit().clear().apply()
    }
}
