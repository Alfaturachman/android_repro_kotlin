package com.example.repro.model

import com.google.gson.annotations.SerializedName

data class TotalStokPemasok(
    @SerializedName("belum_diambil") val belumDiambil: Float,
    @SerializedName("sudah_diambil") val sudahDiambil: Float
)