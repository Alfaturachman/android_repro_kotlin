package com.example.repro.ui.pengelola.riwayat_detail.olah

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.repro.R
import com.example.repro.api.ApiResponse
import com.example.repro.api.RetrofitClient
import com.example.repro.model.OlahRequest
import com.example.repro.model.OlahResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OlahActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_olah)

        val etJenisKendaraan = findViewById<EditText>(R.id.etJenisKendaraan)
        val etBanBekas = findViewById<EditText>(R.id.etBanBekas)
        val etOlah = findViewById<EditText>(R.id.etOlah)
        val btnSimpanOlah = findViewById<Button>(R.id.btnSimpanOlah)
        val btnKembali: ImageButton = findViewById(R.id.btnKembali)

        btnKembali.setOnClickListener { finish() }

        // Ambil data dari Intent
        val idAmbil = intent.getIntExtra("id_ambil", -1)
        val jumlahStok = intent.getDoubleExtra("jumlah_stok", 0.0)
        val jenis = intent.getStringExtra("jenis") ?: ""

        etJenisKendaraan.setText(jenis)
        etBanBekas.setText(jumlahStok.toString())

        Log.d("OlahActivity", "ID Ambil: $idAmbil")
        Log.d("OlahActivity", "Jumlah Stok: $jumlahStok")

        btnSimpanOlah.setOnClickListener {
            val jumlahBanBekas = etBanBekas.text.toString().toDoubleOrNull() ?: 0.0
            val jumlahCrumbRubber = etOlah.text.toString().toDoubleOrNull() ?: 0.0

            if (jumlahCrumbRubber <= 0) {
                Toast.makeText(this, "Jumlah olah harus lebih dari 0!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = OlahRequest(idAmbil, jumlahBanBekas, jumlahCrumbRubber)

            RetrofitClient.instance.simpanOlah(request).enqueue(object : Callback<ApiResponse<OlahResponse>> {
                override fun onResponse(call: Call<ApiResponse<OlahResponse>>, response: Response<ApiResponse<OlahResponse>>) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        if (result != null && result.status) {
                            Toast.makeText(this@OlahActivity, "Berhasil disimpan!", Toast.LENGTH_SHORT).show()
                            setResult(RESULT_OK)
                            finish()
                        } else {
                            Toast.makeText(this@OlahActivity, "Gagal: ${result?.message}", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@OlahActivity, "Response error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse<OlahResponse>>, t: Throwable) {
                    Toast.makeText(this@OlahActivity, "Gagal terhubung: ${t.message}", Toast.LENGTH_SHORT).show()
                    Log.e("OlahActivity", "Error: ${t.message}")
                }
            })
        }
    }
}
