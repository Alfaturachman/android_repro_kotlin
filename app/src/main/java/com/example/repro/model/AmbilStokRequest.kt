package com.example.repro.model

data class AmbilStokRequest(
    val id_pemasok: Int,
    val id_pengelola: Int,
    val id_stok: Int,
    val jumlah_stok: Double
)
