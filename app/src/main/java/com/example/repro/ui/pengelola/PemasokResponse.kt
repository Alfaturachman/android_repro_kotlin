package com.example.repro.ui.pengelola

data class PemasokResponse(
    val status: Boolean,
    val message: String,
    val data: PemasokData?
)

data class PemasokData(
    val pemasok: PemasokDetail?,
    val status_stok: List<StatusStok>?
)

data class PemasokDetail(
    val nama_pemasok: String,
    val nama_usaha: String,
    val no_hp: String,
    val alamat: String,
    val lokasi: String
)

data class StatusStok(
    val id_status_stok: Int,
    val tanggal_status_stok: String,
    val jenis: String,
    val jumlah_stok: Int,
    val harga: Int,
    val total_harga: Int,
    val lokasi: String,
    val status: String
)
