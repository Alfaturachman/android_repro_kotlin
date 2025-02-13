package com.example.repro.model

data class HargaBanResponse(
    val tanggal: String,
    val jenis: String,
    val harga: String
)

data class postHargaBan(
    val id: Int,
    val jenis: String,
    val harga: Double,
    val ins_time: String
)
