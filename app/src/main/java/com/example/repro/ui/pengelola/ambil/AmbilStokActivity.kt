package com.example.repro.ui.pengelola.ambil

import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.repro.R
import com.example.repro.utils.TSP
import com.example.repro.api.ApiResponse
import com.example.repro.api.RetrofitClient
import com.example.repro.modal.getAmbilStok
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

    // Fungsi untuk mengambil data pemasok dari API dan menghitung TSP
    private fun fetchAmbilStokData() {
        RetrofitClient.instance.getAmbilStok().enqueue(object : Callback<ApiResponse<List<getAmbilStok>>> {
            override fun onResponse(call: Call<ApiResponse<List<getAmbilStok>>>, response: Response<ApiResponse<List<getAmbilStok>>>) {
                if (response.isSuccessful && response.body()?.status == true) {
                    val ambilStokList = response.body()?.data ?: emptyList()

                    // Tambahkan lokasi pengelola secara statis
                    val pengelolaLokasi = "-6.748135,111.056280"
                    val pengelola = getAmbilStok(
                        id = "0",
                        id_pemasok = "0",
                        nama_pemasok = "Pengelola",
                        nama_usaha = "-",
                        tanggal = "-",
                        jenis = "-",
                        total_berat = "-",
                        harga = "-",
                        total_harga = "-",
                        lokasi = pengelolaLokasi,
                        status = "-"
                    )

                    // Gabungkan pengelola dengan pemasok dari database
                    val combinedList = mutableListOf(pengelola)
                    combinedList.addAll(ambilStokList)

                    // Hitung matriks jarak dan jalankan TSP
                    val distanceMatrix = TSP.calculateDistanceMatrix(combinedList)
                    val (tour, totalDistance) = TSP.nearestNeighborTSP(distanceMatrix, 0)

                    println("Jarak setiap pemasok ke pengelola:")
                    for (i in 1 until combinedList.size) { // Mulai dari 1 agar tidak menghitung pengelola ke dirinya sendiri
                        val pemasok = combinedList[i]
                        val jarak = distanceMatrix[0][i] // Selalu dari pengelola (index 0) ke pemasok i
                        println("Pemasok ${pemasok.nama_pemasok} = ${"%.2f".format(jarak)} km")
                    }

                    // Tambahkan total jarak keseluruhan
                    println("Total Jarak Keseluruhan: ${"%.2f".format(totalDistance)} km")

                    // Ambil hasil rute tanpa titik awal terakhir untuk menghindari duplikasi
                    val sortedAmbilStokList = tour.drop(1).map { index -> combinedList[index] }

                    // Set adapter dengan data yang sudah diurutkan
                    adapter = AmbilStokAdapter(sortedAmbilStokList, this@AmbilStokActivity)
                    recyclerView.adapter = adapter
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<getAmbilStok>>>, t: Throwable) {
                // Handle failure
            }
        })
    }
}