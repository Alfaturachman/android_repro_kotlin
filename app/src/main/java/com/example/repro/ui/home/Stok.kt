package com.example.repro.ui.home

import com.google.gson.annotations.SerializedName

data class Stok(
    @SerializedName("belum_diambil") val belumDiambil: Float,
    @SerializedName("sudah_diambil") val sudahDiambil: Float,
    @SerializedName("stok_per_bulan") val stokPerBulan: List<StokPerBulan>
)

data class StokPerBulan(
    @SerializedName("bulan") val bulan: Int,
    @SerializedName("total_stok") val totalStok: Float
)