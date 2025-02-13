package com.example.repro.helper

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

object DateHelper {
    fun formatTanggal(tanggal: String?): String {
        if (tanggal.isNullOrEmpty()) return "-"
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id"))
        return try {
            val date = inputFormat.parse(tanggal)
            outputFormat.format(date!!)
        } catch (e: ParseException) {
            "-"
        }
    }
}
