package com.example.repro.ui.pengelola.ambil

import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.repro.R
import com.example.repro.api.ApiResponse
import com.example.repro.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AmbilStokActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AmbilStokAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ambil_stok)

        window.statusBarColor = resources.getColor(R.color.white, theme)

        // Button Kembali
        val btnKembali: ImageButton = findViewById(R.id.btnKembali)
        btnKembali.setOnClickListener {
            finish() // Menutup activity dan kembali ke fragment sebelumnya
        }

        // Inisialisasi RecyclerView
        recyclerView = findViewById(R.id.recyclerViewDaftarPemasok)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Ambil data dari API
        fetchAmbilStokData()
    }

    private fun fetchAmbilStokData() {
        RetrofitClient.instance.getAmbilStok().enqueue(object : Callback<ApiResponse<List<AmbilStok>>> {
            override fun onResponse(call: Call<ApiResponse<List<AmbilStok>>>, response: Response<ApiResponse<List<AmbilStok>>>) {
                if (response.isSuccessful && response.body()?.status == true) {
                    val ambilStokList = response.body()?.data ?: emptyList()
                    adapter = AmbilStokAdapter(ambilStokList, this@AmbilStokActivity)
                    recyclerView.adapter = adapter
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<AmbilStok>>>, t: Throwable) {
                // Handle failure
            }
        })
    }
}