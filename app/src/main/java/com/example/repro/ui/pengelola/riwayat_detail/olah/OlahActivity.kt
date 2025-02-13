package com.example.repro.ui.pengelola.riwayat_detail.olah

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.repro.R

class OlahActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_olah)

        val etJenisKendaraan = findViewById<EditText>(R.id.etJenisKendaraan)
        val etBanBekas = findViewById<EditText>(R.id.etBanBekas)
        val etOlah = findViewById<EditText>(R.id.etOlah)
        val btnSimpanOlah = findViewById<Button>(R.id.btnSimpanOlah)

        // Button Kembali
        val btnKembali: ImageButton = findViewById(R.id.btnKembali)
        btnKembali.setOnClickListener {
            finish()
        }

        // Button Simpan Olah
        btnSimpanOlah.setOnClickListener {
            val jenisKendaraan = etJenisKendaraan.text.toString()
            val jumlahBanBekas = etBanBekas.text.toString()
            val jumlahCrumbRubber = etOlah.text.toString()

            // Simpan ke logcat
            Log.d("OlahActivity", "Jenis Kendaraan: $jenisKendaraan")
            Log.d("OlahActivity", "Jumlah Ban Bekas: $jumlahBanBekas")
            Log.d("OlahActivity", "Jumlah Crumb Rubber: $jumlahCrumbRubber")
        }
    }
}
