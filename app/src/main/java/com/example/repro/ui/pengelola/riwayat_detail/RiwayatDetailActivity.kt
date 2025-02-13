package com.example.repro.ui.pengelola.riwayat_detail

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.repro.R
import com.example.repro.api.ApiResponse
import com.example.repro.api.RetrofitClient
import com.example.repro.model.RiwayatPemasokResponse
import com.example.repro.model.getRiwayatPemasok
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RiwayatDetailActivity : AppCompatActivity() {
    private lateinit var etNamaPemilik: TextView
    private lateinit var etNamaUsaha: TextView
    private lateinit var etAlamat: TextView
    private lateinit var etNomorHp: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RiwayatDetailAdapter
    private val riwayatList = mutableListOf<RiwayatPemasokResponse>()
    private var pemasokId: Int = -1

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

        // ID pemasok dari intent
        pemasokId = intent.getIntExtra("PEMASOK_ID", -1)

        if (pemasokId != -1) {
            Log.d("Pemasok ID", "Berhasil mengambil pemasok ID: $pemasokId")
            fetchPemasokData(pemasokId)
            loadRiwayatPemasok(pemasokId)
        } else {
            Toast.makeText(this, "ID Pemasok tidak valid", Toast.LENGTH_SHORT).show()
        }

        recyclerView = findViewById(R.id.recyclerViewRiwayatPemasok)
        recyclerView.layoutManager = LinearLayoutManager(this@RiwayatDetailActivity)

        adapter = RiwayatDetailAdapter(riwayatList)
        recyclerView.adapter = adapter

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

    private fun loadRiwayatPemasok(idPemasok: Int) {
        val requestBody = hashMapOf("id_pemasok" to idPemasok)

        RetrofitClient.instance.getAmbilByPemasokId(requestBody)
            .enqueue(object : Callback<ApiResponse<List<RiwayatPemasokResponse>>> {
                override fun onResponse(
                    call: Call<ApiResponse<List<RiwayatPemasokResponse>>>,
                    response: Response<ApiResponse<List<RiwayatPemasokResponse>>>
                ) {
                    if (response.isSuccessful) {
                        val riwayatResponse = response.body()

                        if (riwayatResponse?.status == true) {
                            riwayatResponse.data?.let { data ->
                                riwayatList.clear()
                                riwayatList.addAll(data)
                                adapter.notifyDataSetChanged()
                            }
                        } else {
                            val errorMessage = riwayatResponse?.message ?: "Data tidak ditemukan"
                            Log.e("API_ERROR", "Response failed: $errorMessage")
                            Toast.makeText(this@RiwayatDetailActivity, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        val errorMessage = "Error ${response.code()}: ${errorBody ?: "Unknown error"}"
                        Log.e("API_ERROR", errorMessage)
                        Toast.makeText(this@RiwayatDetailActivity, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse<List<RiwayatPemasokResponse>>>, t: Throwable) {
                    Log.e("API_ERROR", "Network failure", t)
                    Toast.makeText(this@RiwayatDetailActivity, "Gagal menghubungi server: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            refreshData()
        }
    }

    private fun refreshData() {
        // update data
        loadRiwayatPemasok(pemasokId)
    }

}
