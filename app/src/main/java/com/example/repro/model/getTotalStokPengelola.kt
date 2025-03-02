package com.example.repro.model

import com.google.gson.annotations.SerializedName

data class getTotalStokPengelola(
    @SerializedName("belum_diambil") val belumDiambil: Float,
    @SerializedName("sudah_diambil") val sudahDiambil: Float,
    @SerializedName("stok_per_bulan") val pemasokStokPerBulan: List<PengelolaStokPerBulan>
)

data class PengelolaStokPerBulan(
    @SerializedName("bulan") val bulan: Int,
    @SerializedName("total_stok") val totalStok: Float
)