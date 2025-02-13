package com.example.repro.ui.harga_ban.tambah

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.repro.R
import com.example.repro.api.ApiResponse
import com.example.repro.api.RetrofitClient
import com.example.repro.model.HargaBanResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class TambahHargaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_harga)

        // Inisialisasi View
        val tvTanggal: TextView = findViewById(R.id.etTanggal)
        val rgJenisKendaraan: RadioGroup = findViewById(R.id.rgJenisKendaraan)
        val btnSimpan: Button = findViewById(R.id.btnSimpanStok)

        // Button Kembali
        val btnKembali: ImageButton = findViewById(R.id.btnKembali)
        btnKembali.setOnClickListener {
            finish()
        }

        // Set Tanggal dan Waktu Otomatis (Update Secara Realtime)
        val timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    val tanggalFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    val tanggalSekarang = tanggalFormat.format(Date())
                    tvTanggal.text = tanggalSekarang
                }
            }
        }, 0, 1000) // Update setiap detik

        // Tombol Simpan
        btnSimpan.setOnClickListener {
            val tanggal = tvTanggal.text.toString()
            val selectedId = rgJenisKendaraan.checkedRadioButtonId
            val jenis = findViewById<RadioButton>(selectedId)?.text.toString()
            val harga = findViewById<TextView>(R.id.etHargaPerKg).text.toString()

            if (tanggal.isEmpty() || jenis.isEmpty() || harga.isEmpty()) {
                Toast.makeText(this, "Semua field harus diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Kirim data ke API
            val HargaBanResponse = HargaBanResponse(tanggal, jenis, harga)
            RetrofitClient.instance.tambahHargaBan(HargaBanResponse)
                .enqueue(object : Callback<ApiResponse<HargaBanResponse>> {
                    override fun onResponse(call: Call<ApiResponse<HargaBanResponse>>, response: Response<ApiResponse<HargaBanResponse>>) {
                        if (response.isSuccessful && response.body()?.status == true) {
                            Toast.makeText(this@TambahHargaActivity, "Harga berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this@TambahHargaActivity, "Gagal menyimpan data!", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<ApiResponse<HargaBanResponse>>, t: Throwable) {
                        Toast.makeText(this@TambahHargaActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}
