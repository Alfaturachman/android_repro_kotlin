package com.example.repro.ui.pemasok

data class StatusStok(
    val id: Int,
    val id_pemasok: Int,
    val tanggal: String,
    val jenis: String,
    val jumlah_stok: Int,
    val harga: Int,
    val total_harga: Int,
    val lokasi: String,
    val status: String
)