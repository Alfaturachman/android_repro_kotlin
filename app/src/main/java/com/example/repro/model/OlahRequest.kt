package com.example.repro.model

data class OlahRequest(
    val id_ambil: Int,
    val jumlah_stok: Double,
    val jumlah_mentah: Double
)

data class OlahResponse(
    val status: Boolean,
    val message: String
)
