package com.example.repro.model

import com.google.gson.annotations.SerializedName

data class StatsTotalPemasok(
    @SerializedName("tahun") val tahun: Int,
    @SerializedName("bulan") val bulan: Int,
    @SerializedName("total_stok_belum_diambil") val totalStokBelumDiambil: Int,
    @SerializedName("total_stok_sudah_diambil") val totalStokSudahDiambil: Int,
)
