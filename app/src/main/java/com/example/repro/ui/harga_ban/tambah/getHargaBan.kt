package com.example.repro.ui.harga_ban.tambah

data class getHargaBan(
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
