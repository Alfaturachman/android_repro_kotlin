package com.example.repro.model

import com.google.gson.annotations.SerializedName

data class getTotalStokPemasok(
    @SerializedName("belum_diambil") val belumDiambil: Float,
    @SerializedName("sudah_diambil") val sudahDiambil: Float
)