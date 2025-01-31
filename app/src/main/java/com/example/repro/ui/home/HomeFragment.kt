package com.example.repro.ui.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.repro.api.RetrofitClient
import com.example.repro.api.ApiResponse
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.example.repro.databinding.FragmentHomeBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.HashMap

class HomeFragment : Fragment() {
    private lateinit var pemasokTotalBelumAmbil: TextView
    private lateinit var pemasokTotalSudahAmbil: TextView
    private lateinit var laporanDataPemasok: BarChart
    private var binding: FragmentHomeBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inisialisasi ViewModel
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        // Mengambil referensi binding dan root view
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root = binding?.root

        // TextView binding
        pemasokTotalBelumAmbil = binding?.pemasokTotalBelumAmbil!!
        pemasokTotalSudahAmbil = binding?.pemasokTotalSudahAmbil!!

        // Mengambil BarChart dari binding
        laporanDataPemasok = binding?.laporanDataPemasok!!

        // SharedPreferences
        val pemasokId = getPemasokIdFromSharedPreferences()
        if (pemasokId != -1) {
            fetchStokData(pemasokId)
        } else {
            // Handle jika id_pemasok tidak ditemukan
        }

        // Kembalikan root view
        return root
    }

    private fun getPemasokIdFromSharedPreferences(): Int {
        val sharedPreferences = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("id_user", -1)
    }

    private fun fetchStokData(userId: Int) {
        // Buat request body
        val requestBody = HashMap<String, Int>()
        requestBody["user_id"] = userId

        // Panggil API
        val call = RetrofitClient.instance.getStokData(requestBody)
        call.enqueue(object : Callback<ApiResponse<Stok>> {
            override fun onResponse(call: Call<ApiResponse<Stok>>, response: Response<ApiResponse<Stok>>) {
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!

                    // Log keseluruhan response
                    Log.d("API_RESPONSE", apiResponse.toString())

                    val stokData = apiResponse.data

                    // Log data stok
                    Log.d("STOK_DATA", "belumDiambil: ${stokData.belumDiambil}, sudahDiambil: ${stokData.sudahDiambil}")

                    pemasokTotalBelumAmbil.text = "${stokData.belumDiambil}"
                    pemasokTotalSudahAmbil.text = "${stokData.sudahDiambil}"

                    // Siapkan data untuk BarChart
                    val entries = ArrayList<BarEntry>()

                    // Buat array untuk 12 bulan
                    val stokPerBulan = FloatArray(12) { 0f } // Inisialisasi dengan nilai 0

                    // Isi array dengan data dari API
                    for (stok in stokData.stokPerBulan) {
                        val bulanIndex = stok.bulan - 1 // Konversi bulan (1-12) ke index (0-11)
                        if (bulanIndex in 0..11) {
                            stokPerBulan[bulanIndex] = stok.totalStok
                        }
                    }

                    // Tambahkan data ke entries
                    for (i in 0 until 12) {
                        entries.add(BarEntry(i.toFloat() + 1, stokPerBulan[i])) // Bulan dimulai dari 1
                    }

                    // Set data set untuk BarChart
                    val dataSet = BarDataSet(entries, "Stok per Bulan") // Label untuk dataset
                    dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()

                    // Buat BarData
                    val barData = BarData(dataSet)

                    // Set data ke chart
                    laporanDataPemasok.data = barData

                    // Atur sumbu X (bulan)
                    val bulanLabels = arrayOf(
                        "Jan", "Feb", "Mar", "Apr", "Mei", "Jun",
                        "Jul", "Agu", "Sep", "Okt", "Nov", "Des"
                    )
                    val xAxis = laporanDataPemasok.xAxis
                    xAxis.valueFormatter = IndexAxisValueFormatter(bulanLabels)
                    xAxis.position = XAxis.XAxisPosition.BOTTOM
                    xAxis.granularity = 1f
                    xAxis.setDrawGridLines(false)

                    // Atur legenda
                    laporanDataPemasok.legend.isEnabled = true // Aktifkan legenda
                    laporanDataPemasok.legend.textColor = android.graphics.Color.BLACK // Warna teks legenda

                    // Menampilkan chart
                    laporanDataPemasok.invalidate()
                } else {
                    // Log error jika response tidak sesuai
                    Log.e("API_ERROR", "Response error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ApiResponse<Stok>>, t: Throwable) {
                // Log jika terjadi kegagalan jaringan atau error lainnya
                Log.e("API_FAILURE", "Request gagal: ${t.message}", t)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}