package com.example.repro.ui.success

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.repro.R

class SuccessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_success)

        // Inisialisasi TextView berdasarkan ID
        val tvTitle: TextView = findViewById(R.id.tvTitle)
        val tvSubtitle: TextView = findViewById(R.id.tvSubtitle)
        val tvTanggal: TextView = findViewById(R.id.tvTanggal)
        val tvNamaPelanggan: TextView = findViewById(R.id.tvNamaPelanggan)
        val tvIdPelanggan: TextView = findViewById(R.id.tvIdPelanggan)
        val tvJumlahStok: TextView = findViewById(R.id.tvJumlahStok)
        val tvHargaPerKg: TextView = findViewById(R.id.tvHargaPerKg)
        val tvTotalHarga: TextView = findViewById(R.id.tvTotalHarga)

        // Set teks atau properti lainnya sesuai kebutuhan
        tvTitle.text = "Tambah Berhasil!"
        tvSubtitle.text = "Kamu berhasil menambahkan stok ban bekas"
        tvTanggal.text = "09 Okt 2025, 22.55 WIB"
        tvNamaPelanggan.text = "Eka Gelombang Laut"
        tvIdPelanggan.text = "Belum diambil"
        tvJumlahStok.text = "40 kg"
        tvHargaPerKg.text = "Rp 7.500"
        tvTotalHarga.text = "Rp 215.000"
    }
}
