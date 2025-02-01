package com.example.repro.ui.home

import com.google.gson.annotations.SerializedName

data class TotalStokPemasok(
    @SerializedName("belum_diambil") val belumDiambil: Float,
    @SerializedName("sudah_diambil") val sudahDiambil: Float,
    @SerializedName("stok_per_bulan") val pemasokStokPerBulan: List<PemasokStokPerBulan>
)

data class PemasokStokPerBulan(
    @SerializedName("bulan") val bulan: Int,
    @SerializedName("total_stok") val totalStok: Float
)