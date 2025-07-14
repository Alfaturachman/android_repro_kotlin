package com.example.repro.model

data class PemasokStok(
    val id: Int,
    val id_pemasok: Int,
    val tanggal: String,
    val jenis: String,
    val jumlah_stok: Int,
    val harga: Int,
    val total_harga: Int,
    val lokasi: String,
    val keterangan: String,
    val foto: String,
    val status: String
)