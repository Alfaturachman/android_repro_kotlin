package com.example.repro.model

data class HargaResponse(
    val status: Boolean,
    val message: String,
    val data: List<HargaKendaraan>
)

data class HargaKendaraan(
    val id: String,
    val jenis: String,
    val harga: String,
    val ins_time: String
)
