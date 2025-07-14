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
import com.example.repro.model.StatsTotalPengelola
import com.example.repro.model.TotalStokPemasok
import com.example.repro.model.TotalStokPengelola
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class HomeFragment : Fragment() {
    
    private var userId: Int = -1
    private var userIdDetail: Int = -1
    private lateinit var tvLaporanStatistikData: TextView
    private lateinit var tvLaporanTotalData: TextView
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
        tvLaporanStatistikData = binding?.tvLaporanStatistikData!!
        tvLaporanTotalData = binding?.tvLaporanTotalData!!
        tvSubTitle1 = binding?.tvSubTitle1!!
        tvSubTitle2 = binding?.tvSubTitle2!!
        TotalData1 = binding?.TotalData1!!
        TotalData2 = binding?.TotalData2!!

        // BarChart binding
        laporanDataPemasok = binding?.laporanDataPemasok!!

        // Nama User dari SharedPreferences
        val userNama = getNamaFromSharedPreferences()
        tvNama.text = userNama

        // ID User dari SharedPreferences
        userId = getuserIdFromSharedPreferences()
        userIdDetail = getuserIdDetailFromSharedPreferences()

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)

        tvLaporanStatistikData.text = "Laporan Statistik Stok Tahun $year"

        // Level dari SharedPreferences
        val level = getLevelFromSharedPreferences()
        when (level) {
            "pemasok" -> {
                tvLaporanTotalData.text = "Laporan Total Stok Pemasok"
                tvSubTitle1.text = "Belum diambil"
                tvSubTitle2.text = "Sudah diambil"
                pemasokStatsData(userIdDetail)
                fetchPemasokTotalData(userId)
            }
            "pengelola" -> {
                tvLaporanTotalData.text = "Laporan Total Stok Pengelola"
                tvSubTitle1.text = "Ban Bekas"
                tvSubTitle2.text = "Crumb Rubber"
                pengelolaStatsData(userId)
                fetchPengelolaTotalData(userId)
            }
            else -> {
                Toast.makeText(requireContext(), "Level pengguna tidak valid", Toast.LENGTH_SHORT).show()
            }
        }

        // Kembalikan root view
        return root
    }

    private fun fetchPemasokTotalData(userId: Int) {
        val requestBody = hashMapOf("id_user" to userId)

        RetrofitClient.instance.getTotalStokPemasok(requestBody)
            .enqueue(object : Callback<ApiResponse<TotalStokPemasok>> {
                override fun onResponse(
                    call: Call<ApiResponse<TotalStokPemasok>>,
                    response: Response<ApiResponse<TotalStokPemasok>>
                ) {
                    Log.d("API_RESPONSE", "Response Code: ${response.code()}")

                    if (response.isSuccessful) {
                        val pemasokResponse = response.body()
                        Log.d("API_RESPONSE", "Response Body: $pemasokResponse")

                        if (pemasokResponse?.status == true) {
                            pemasokResponse.data?.let { pemasok ->
                                TotalData1.text = pemasok.belumDiambil.toString()
                                TotalData2.text = pemasok.sudahDiambil.toString()
                                Log.d("API_SUCCESS", "Data berhasil diperoleh: $pemasok")
                            }
                        } else {
                            Log.e("API_ERROR", "Pesan error dari server: ${pemasokResponse?.message}")
                            Toast.makeText(requireContext(), pemasokResponse?.message ?: "Data tidak ditemukan", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("API_ERROR", "Response Error Body: $errorBody")
                        Toast.makeText(requireContext(), "Gagal mengambil data", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse<TotalStokPemasok>>, t: Throwable) {
                    Log.e("API_FAILURE", "Failure: ${t.message}", t)
                    Toast.makeText(requireContext(), "Terjadi kesalahan jaringan", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun fetchPengelolaTotalData(userId: Int) {
        val requestBody = hashMapOf("id_user" to userId)

        RetrofitClient.instance.getTotalStokPengelola(requestBody)
            .enqueue(object : Callback<ApiResponse<TotalStokPengelola>> {
                override fun onResponse(
                    call: Call<ApiResponse<TotalStokPengelola>>,
                    response: Response<ApiResponse<TotalStokPengelola>>
                ) {
                    Log.d("API_RESPONSE", "Response Code: ${response.code()}")

                    if (response.isSuccessful) {
                        val pengelolaResponse = response.body()
                        Log.d("API_RESPONSE", "Response Body: $pengelolaResponse")

                        if (pengelolaResponse?.status == true) {
                            pengelolaResponse.data?.let { pengelola ->
                                TotalData1.text = pengelola.totalBanBekas.toString()
                                TotalData2.text = pengelola.totalCrumbRubber.toString()
                                Log.d("API_SUCCESS", "Data berhasil diperoleh: $pengelola")
                            }
                        } else {
                            Log.e("API_ERROR", "Pesan error dari server: ${pengelolaResponse?.message}")
                            Toast.makeText(requireContext(), pengelolaResponse?.message ?: "Data tidak ditemukan", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("API_ERROR", "Response Error Body: $errorBody")
                        Toast.makeText(requireContext(), "Gagal mengambil data", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse<TotalStokPengelola>>, t: Throwable) {
                    Log.e("API_FAILURE", "Failure: ${t.message}", t)
                    Toast.makeText(requireContext(), "Terjadi kesalahan jaringan", Toast.LENGTH_SHORT).show()
                }
            })
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

                    // Spinner tahun
                    // val tahun = selectedYearFromDropdown

                    // Tahun sekarang
                    val calendar = Calendar.getInstance()
                    val tahun = calendar.get(Calendar.YEAR)

                    // Gabungkan data stok berdasarkan bulan untuk tahun yang diinginkan
                    for (stokData in stokDataList) {
                        if (stokData.tahun == tahun) { // Filter tahun
                            val bulanIndex = stokData.bulan - 1 // Konversi bulan (1-12) ke index (0-11)
                            if (bulanIndex in 0..11) {
                                stokBelumDiambil[bulanIndex] += stokData.totalStokBelumDiambil.toFloat()
                                stokSudahDiambil[bulanIndex] += stokData.totalStokSudahDiambil.toFloat()
                            }
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

    private fun pengelolaStatsData(userId: Int) {
        // Buat request body
        val requestBody = HashMap<String, Int>()
        requestBody["user_id"] = userId

        // Panggil API
        val call = RetrofitClient.instance.getStatsPengelola(requestBody)
        call.enqueue(object : Callback<ApiResponse<List<StatsTotalPengelola>>> {
            override fun onResponse(
                call: Call<ApiResponse<List<StatsTotalPengelola>>>,
                response: Response<ApiResponse<List<StatsTotalPengelola>>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    Log.d("API_RESPONSE", apiResponse.toString())

                    val stokDataList = apiResponse.data

                    // Siapkan struktur data
                    val stokBanBekas = FloatArray(12) { 0f } // Stok belum diambil per bulan
                    val stokCrumbRubber = FloatArray(12) { 0f } // Stok sudah diambil per bulan

                    // Spinner tahun
                    // val tahun = selectedYearFromDropdown

                    // Tahun sekarang
                    val calendar = Calendar.getInstance()
                    val tahun = calendar.get(Calendar.YEAR)

                    // Gabungkan data stok berdasarkan bulan untuk tahun yang diinginkan
                    for (stokData in stokDataList) {
                        if (stokData.tahun == tahun) { // Filter tahun
                            val bulanIndex = stokData.bulan - 1 // Konversi bulan (1-12) ke index (0-11)
                            if (bulanIndex in 0..11) {
                                stokBanBekas[bulanIndex] += stokData.totalBanBekas.toFloat()
                                stokCrumbRubber[bulanIndex] += stokData.totalCrumbRubber.toFloat()
                            }
                        }
                    }

                    // Buat data untuk BarChart
                    val entriesBanBekas = mutableListOf<BarEntry>()
                    val entriesCrumbRubber = mutableListOf<BarEntry>()

                    for (i in 0 until 12) {
                        entriesBanBekas.add(BarEntry(i.toFloat(), stokBanBekas[i]))
                        entriesCrumbRubber.add(BarEntry(i.toFloat(), stokCrumbRubber[i]))
                    }

                    // Set data set untuk BarChart
                    val dataSetBanBekas = BarDataSet(entriesBanBekas, "Stok Ban Bekas").apply {
                        color = ContextCompat.getColor(requireContext(), com.example.repro.R.color.badge_warning)
                        valueTextColor = Color.BLACK
                    }

                    val dataSetCrumbRubber = BarDataSet(entriesCrumbRubber, "Stok Crumb Rubber").apply {
                        color = ContextCompat.getColor(requireContext(), com.example.repro.R.color.badge_success)
                        valueTextColor = Color.BLACK
                    }

                    // Buat BarData untuk chart
                    val barData = BarData(dataSetBanBekas, dataSetCrumbRubber)
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

            override fun onFailure(call: Call<ApiResponse<List<StatsTotalPengelola>>>, t: Throwable) {
                Log.e("API_FAILURE", "Request gagal: ${t.message}", t)
            }
        })
    }

    private fun getuserIdFromSharedPreferences(): Int {
        val sharedPreferences = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("id_user", -1)
    }

    private fun getuserIdDetailFromSharedPreferences(): Int {
        val sharedPreferences = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("id_user_detail", -1)
    }

    private fun getNamaFromSharedPreferences(): String? {
        val sharedPreferences = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        return sharedPreferences.getString("nama", "0")
    }

    private fun getLevelFromSharedPreferences(): String? {
        val sharedPreferences = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        return sharedPreferences.getString("level", "0")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}