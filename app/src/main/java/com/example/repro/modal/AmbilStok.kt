package com.example.repro.modal

data class AmbilStok(
    val id: String,
    val id_pemasok: String,
    val nama_pemasok: String,
    val nama_usaha: String,
    val tanggal: String,
    val jenis: String,
    val total_berat: String,
    val harga: String,
    val total_harga: String,
    val lokasi: String,
    val status: String
)