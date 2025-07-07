package com.example.repro.model

import com.google.gson.annotations.SerializedName

data class getTotalStokPengelola(
    @SerializedName("total_ambil") val totalBanBekas: Float,
    @SerializedName("total_diolah") val totalCrumbRubber: Float
)