package com.example.repro.ui.pengelola.riwayat_detail

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.repro.R
import com.example.repro.api.ApiResponse
import com.example.repro.api.RetrofitClient
import com.example.repro.modal.getRiwayatPemasok
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RiwayatDetailActivity : AppCompatActivity() {
    private lateinit var etNamaPemilik: TextView
    private lateinit var etNamaUsaha: TextView
    private lateinit var etAlamat: TextView
    private lateinit var etNomorHp: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_riwayat_detail)

        window.statusBarColor = resources.getColor(R.color.white, theme)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        // Inisialisasi TextView
        etNamaPemilik = findViewById(R.id.etNamaPemilik)
        etNamaUsaha = findViewById(R.id.etNamaUsaha)
        etAlamat = findViewById(R.id.etAlamat)
        etNomorHp = findViewById(R.id.etNomorHp)

        // Ambil ID pemasok dari intent
        val pemasokId = intent.getIntExtra("PEMASOK_ID", -1)

        if (pemasokId != -1) {
            Log.d("Pemasok ID", "Berhasil mengambil pemasok ID: $pemasokId")
            fetchPemasokData(pemasokId)
        } else {
            Toast.makeText(this, "ID Pemasok tidak valid", Toast.LENGTH_SHORT).show()
        }

        // Button Kembali
        val btnKembali: ImageButton = findViewById(R.id.btnKembali)
        btnKembali.setOnClickListener {
            finish()
        }
    }

    private fun fetchPemasokData(idPemasok: Int) {
        val requestBody = hashMapOf("id_pemasok" to idPemasok)

        RetrofitClient.instance.getRiwayatByPemasokId(requestBody)
            .enqueue(object : Callback<ApiResponse<getRiwayatPemasok>> {
                override fun onResponse(
                    call: Call<ApiResponse<getRiwayatPemasok>>,
                    response: Response<ApiResponse<getRiwayatPemasok>>
                ) {
                    if (response.isSuccessful) {
                        val pemasokResponse = response.body()

                        if (pemasokResponse?.status == true) {
                            pemasokResponse.data?.pemasok?.let { pemasok ->
                                etNamaPemilik.text = pemasok.nama_pemasok
                                etNamaUsaha.text = pemasok.nama_usaha
                                etAlamat.text = pemasok.alamat
                                etNomorHp.text = pemasok.no_hp
                            } ?: run {
                                Toast.makeText(this@RiwayatDetailActivity, "Data pemasok tidak ditemukan", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this@RiwayatDetailActivity, pemasokResponse?.message ?: "Data tidak ditemukan", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Log.e("API_ERROR", "Response Error: ${response.errorBody()?.string()}")
                        Toast.makeText(this@RiwayatDetailActivity, "Gagal mengambil data", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse<getRiwayatPemasok>>, t: Throwable) {
                    Log.e("API_ERROR", "Failure: ${t.message}")
                    Toast.makeText(this@RiwayatDetailActivity, "Terjadi kesalahan jaringan", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
