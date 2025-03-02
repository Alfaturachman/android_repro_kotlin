package com.example.repro.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.repro.api.ApiResponse
import com.example.repro.api.RetrofitClient
import com.example.repro.databinding.FragmentHomeBinding
import com.example.repro.model.StatsTotalPemasok
import com.example.repro.model.getTotalStokPemasok
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {
    
    private var userId: Int = -1
    private lateinit var tvNama: TextView
    private lateinit var tvSubTitle1: TextView
    private lateinit var tvSubTitle2: TextView
    private lateinit var TotalData1: TextView
    private lateinit var TotalData2: TextView
    private lateinit var laporanDataPemasok: BarChart
    private lateinit var CardViewTotalBelumDiambil: CardView
    private lateinit var CardViewTotalSudahDiambil: CardView
    private var binding: FragmentHomeBinding? = null

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inisialisasi ViewModel
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        // Mengambil referensi binding dan root view
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root = binding?.root

        // CardView Binding
        CardViewTotalBelumDiambil = binding?.CardViewTotalBelumDiambil!!
        CardViewTotalSudahDiambil = binding?.CardViewTotalSudahDiambil!!

        // TextView binding
        tvNama = binding?.tvNama!!
        tvSubTitle1 = binding?.tvSubTitle1!!
        tvSubTitle2 = binding?.tvSubTitle2!!
        TotalData1 = binding?.TotalData1!!
        TotalData2 = binding?.TotalData2!!

        // Mengambil BarChart dari binding
        laporanDataPemasok = binding?.laporanDataPemasok!!

        // Nama User dari SharedPreferences
        val userNama = getNamaFromSharedPreferences()
        tvNama.text = userNama

        // ID User dari SharedPreferences
        userId = getuserIdFromSharedPreferences()

        // Level dari SharedPreferences
        val level = getLevelFromSharedPreferences()
        when (level) {
            "pemasok" -> {
                pemasokStatsData(userId)
                fetchPemasokTotalData(userId)
                tvSubTitle1.text = "Belum diambil"
                tvSubTitle2.text = "Sudah diambil"
            }
            "pengelola" -> {
                tvSubTitle1.text = "Ban Bekas"
                tvSubTitle2.text = "Crumb Rubber"
            }
            else -> {
                Toast.makeText(requireContext(), "Level pengguna tidak valid", Toast.LENGTH_SHORT).show()
            }
        }

        // Kembalikan root view
        return root
    }

    private fun getuserIdFromSharedPreferences(): Int {
        val sharedPreferences = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("id_user", -1)
    }

    private fun getNamaFromSharedPreferences(): String? {
        val sharedPreferences = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        return sharedPreferences.getString("nama", "0")
    }

    private fun getLevelFromSharedPreferences(): String? {
        val sharedPreferences = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        return sharedPreferences.getString("level", "0")
    }

    private fun pemasokStatsData(userId: Int) {
        // Buat request body
        val requestBody = HashMap<String, Int>()
        requestBody["user_id"] = userId

        // Panggil API
        val call = RetrofitClient.instance.getStatsPemasok(requestBody)
        call.enqueue(object : Callback<ApiResponse<List<StatsTotalPemasok>>> {
            override fun onResponse(
                call: Call<ApiResponse<List<StatsTotalPemasok>>>,
                response: Response<ApiResponse<List<StatsTotalPemasok>>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    Log.d("API_RESPONSE", apiResponse.toString())

                    val stokDataList = apiResponse.data

                    // Siapkan struktur data
                    val stokBelumDiambil = FloatArray(12) { 0f } // Stok belum diambil per bulan
                    val stokSudahDiambil = FloatArray(12) { 0f } // Stok sudah diambil per bulan

                    // Gabungkan data stok berdasarkan bulan
                    for (stokData in stokDataList) {
                        val bulanIndex = stokData.bulan - 1 // Konversi bulan (1-12) ke index (0-11)
                        if (bulanIndex in 0..11) {
                            stokBelumDiambil[bulanIndex] += stokData.totalStokBelumDiambil.toFloat()
                            stokSudahDiambil[bulanIndex] += stokData.totalStokSudahDiambil.toFloat()
                        }
                    }

                    // Buat data untuk BarChart
                    val entriesBelumDiambil = mutableListOf<BarEntry>()
                    val entriesSudahDiambil = mutableListOf<BarEntry>()

                    for (i in 0 until 12) {
                        entriesBelumDiambil.add(BarEntry(i.toFloat(), stokBelumDiambil[i]))
                        entriesSudahDiambil.add(BarEntry(i.toFloat(), stokSudahDiambil[i]))
                    }

                    // Set data set untuk BarChart
                    val dataSetBelumDiambil = BarDataSet(entriesBelumDiambil, "Stok Belum Diambil").apply {
                        color = ContextCompat.getColor(requireContext(), com.example.repro.R.color.badge_warning)
                        valueTextColor = Color.BLACK
                    }

                    val dataSetSudahDiambil = BarDataSet(entriesSudahDiambil, "Stok Sudah Diambil").apply {
                        color = ContextCompat.getColor(requireContext(), com.example.repro.R.color.badge_success)
                        valueTextColor = Color.BLACK
                    }

                    // Buat BarData untuk chart
                    val barData = BarData(dataSetBelumDiambil, dataSetSudahDiambil)
                    barData.barWidth = 0.5f // Lebar bar

                    // Set data ke chart
                    binding?.laporanDataPemasok?.data = barData

                    // Konfigurasi sumbu X
                    val bulanLabels = arrayOf(
                        "Jan", "Feb", "Mar", "Apr", "Mei", "Jun",
                        "Jul", "Agu", "Sep", "Okt", "Nov", "Des"
                    )
                    binding?.laporanDataPemasok?.xAxis?.apply {
                        valueFormatter = IndexAxisValueFormatter(bulanLabels)
                        position = XAxis.XAxisPosition.BOTTOM
                        granularity = 1f
                        setDrawGridLines(false)
                    }

                    // Konfigurasi sumbu Y
                    binding?.laporanDataPemasok?.axisLeft?.apply {
                        axisMinimum = 0f
                        granularity = 1f
                    }
                    binding?.laporanDataPemasok?.axisRight?.isEnabled = false

                    // Konfigurasi legenda
                    binding?.laporanDataPemasok?.legend?.apply {
                        isEnabled = true
                        textColor = Color.BLACK
                    }

                    // Menampilkan chart
                    binding?.laporanDataPemasok?.invalidate()
                } else {
                    Log.e("API_ERROR", "Response error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<StatsTotalPemasok>>>, t: Throwable) {
                Log.e("API_FAILURE", "Request gagal: ${t.message}", t)
            }
        })
    }

    private fun fetchPemasokTotalData(userId: Int) {
        val requestBody = hashMapOf("user_id" to userId)

        RetrofitClient.instance.getTotalStokPemasok(requestBody)
            .enqueue(object : Callback<ApiResponse<getTotalStokPemasok>> {
                override fun onResponse(
                    call: Call<ApiResponse<getTotalStokPemasok>>,
                    response: Response<ApiResponse<getTotalStokPemasok>>
                ) {
                    if (response.isSuccessful) {
                        val pemasokResponse = response.body()

                        if (pemasokResponse?.status == true) {
                            pemasokResponse.data?.let { pemasok ->
                                TotalData1.text = pemasok.belumDiambil.toString()
                                TotalData2.text = pemasok.sudahDiambil.toString()
                            }
                        } else {
                            Toast.makeText(requireContext(), pemasokResponse?.message ?: "Data tidak ditemukan", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Log.e("API_ERROR", "Response Error: ${response.errorBody()?.string()}")
                        Toast.makeText(requireContext(), "Gagal mengambil data", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse<getTotalStokPemasok>>, t: Throwable) {
                    Log.e("API_ERROR", "Failure: ${t.message}")
                    Toast.makeText(requireContext(), "Terjadi kesalahan jaringan", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}