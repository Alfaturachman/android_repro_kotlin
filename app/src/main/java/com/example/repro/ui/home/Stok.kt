package com.example.repro.ui.home

import com.google.gson.annotations.SerializedName

data class Stok(
    @SerializedName("belum_diambil") val belumDiambil: Float,
    @SerializedName("sudah_diambil") val sudahDiambil: Float
)

