package com.example.repro.helpers

import java.text.NumberFormat
import java.util.Locale

object RupiahHelper {
    fun formatRupiah(amount: Double): String {
        val formatRupiah = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        return formatRupiah.format(amount)
            .replace(",00", "")
            .replace("Rp", "Rp ")
    }
}
