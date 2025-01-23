package com.example.repro.ui.pengelola

import com.google.gson.annotations.SerializedName

data class Ambil(
    @SerializedName("id_ambil")
    val idAmbil: Int = 0,

    @SerializedName("tanggal_ambil")
    val tanggalAmbil: String = "",

    @SerializedName("jumlah_stok_ambil")
    val jumlahStokAmbil: Int = 0,

    @SerializedName("keterangan_ambil")
    val keteranganAmbil: String = "",

    @SerializedName("id_pemasok")
    val idPemasok: Int = 0,

    @SerializedName("nama_pemasok")
    val namaPemasok: String = "",

    @SerializedName("nama_usaha_pemasok")
    val namaUsahaPemasok: String = "",

    @SerializedName("alamat_pemasok")
    val alamatPemasok: String = "",

    @SerializedName("no_hp_pemasok")
    val noHpPemasok: String = "",

    @SerializedName("id_pengelola")
    val idPengelola: Int = 0,

    @SerializedName("nama_pengelola")
    val namaPengelola: String = "",

    @SerializedName("nama_usaha_pengelola")
    val namaUsahaPengelola: String = "",

    @SerializedName("alamat_pengelola")
    val alamatPengelola: String = "",

    @SerializedName("id_stok")
    val idStok: Int = 0,

    @SerializedName("tanggal_stok")
    val tanggalStok: String = "",

    @SerializedName("jenis_stok")
    val jenisStok: String = "",

    @SerializedName("jumlah_stok_status")
    val jumlahStokStatus: Int = 0,

    @SerializedName("harga_stok")
    val hargaStok: Double = 0.0,

    @SerializedName("total_harga_stok")
    val totalHargaStok: Double = 0.0,

    @SerializedName("status_stok")
    val statusStok: String = ""
)
