package com.example.repro.model

import com.google.gson.annotations.SerializedName

data class RiwayatPemasokResponse(
    @SerializedName("id_ambil")
    val idAmbil: Int,

    @SerializedName("id_stok")
    val idStok: Int,

    @SerializedName("tanggal_ambil")
    val tanggalAmbil: String,

    @SerializedName("jumlah_stok_ambil")
    val jumlahStokAmbil: Int,

    @SerializedName("keterangan_ambil")
    val keteranganAmbil: String,

    @SerializedName("tanggal_stok")
    val tanggalStok: String,

    @SerializedName("jenis")
    val jenis: String,

    @SerializedName("jumlah_stok_status")
    val jumlahStokStatus: Int,

    @SerializedName("harga")
    val harga: Double,

    @SerializedName("total_harga")
    val totalHarga: Double,

    @SerializedName("lokasi")
    val lokasi: String,

    @SerializedName("status")
    val status: String,

    @SerializedName("id_olah")
    val idOlah: Int?,

    @SerializedName("tanggal_olah")
    val tanggalOlah: String?,

    @SerializedName("jumlah_stok_olah")
    val jumlahStokOlah: Int?,

    @SerializedName("jumlah_mentah_olah")
    val jumlahMentahOlah: Int?
)