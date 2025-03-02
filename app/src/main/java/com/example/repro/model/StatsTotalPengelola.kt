package com.example.repro.model

import com.google.gson.annotations.SerializedName

data class StatsTotalPengelola(
    @SerializedName("bulan") val bulan: Int,
    @SerializedName("total_ambil") val totalBanBekas: Int,
    @SerializedName("total_diolah") val totalCrumbRubber: Int,
)