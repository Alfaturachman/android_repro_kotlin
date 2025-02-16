package com.example.repro.model

import com.google.gson.annotations.SerializedName

data class StokRequest(
    @SerializedName("id_pemasok") val idPemasok: Int,
    @SerializedName("jenis") val jenis: String,
    @SerializedName("jumlah_stok") val jumlahStok: Int,
    @SerializedName("harga") val harga: Double,
    @SerializedName("total_harga") val totalHarga: Double,
    @SerializedName("lokasi") val lokasi: String
)
