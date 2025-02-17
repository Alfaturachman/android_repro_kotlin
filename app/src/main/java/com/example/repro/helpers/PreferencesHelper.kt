package com.example.repro.helpers

import android.content.Context

object PreferencesHelper {
    fun saveString(context: Context, key: String, value: String) {
        val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString(key, value)
            apply()
        }
    }

    fun getString(context: Context, key: String): String? {
        val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPref.getString(key, null)
    }
}
