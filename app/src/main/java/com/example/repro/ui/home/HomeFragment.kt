package com.example.repro.ui.home

import android.R
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
    private lateinit var tvNama: TextView
    private lateinit var pemasokTotalBelumAmbil: TextView
    private lateinit var pemasokTotalSudahAmbil: TextView
    private lateinit var laporanDataPemasok: BarChart
    private lateinit var CardViewPemasokTotalBelumDiambil: CardView
    private lateinit var CardViewPemasokTotalSudahDiambil: CardView
    private lateinit var CardViewPengelolaTotalBanBekas: CardView
    private lateinit var CardViewPengelolaTotalSudahDiolah: CardView
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

        // CardView Binding
        CardViewPemasokTotalBelumDiambil = binding?.CardViewPemasokTotalBelumDiambil!!
        CardViewPemasokTotalSudahDiambil = binding?.CardViewPemasokTotalSudahDiambil!!
        CardViewPengelolaTotalBanBekas = binding?.CardViewPengelolaTotalBanBekas!!
        CardViewPengelolaTotalSudahDiolah = binding?.CardViewPengelolaTotalSudahDiolah!!

        // TextView binding
        tvNama = binding?.tvNama!!
        pemasokTotalBelumAmbil = binding?.pemasokTotalBelumAmbil!!
        pemasokTotalSudahAmbil = binding?.pemasokTotalSudahAmbil!!

        // Mengambil BarChart dari binding
        laporanDataPemasok = binding?.laporanDataPemasok!!

        // Nama dari SharedPreferences
        val namaUser = getNamaFromSharedPreferences()
        tvNama.text = namaUser

        // ID User dari SharedPreferences
        val pemasokId = getPemasokIdFromSharedPreferences()
        pemasokDataTotal(pemasokId)

        // Level dari SharedPreferences
        val level = getLevelFromSharedPreferences()
        when (level) {
            "pemasok" -> {
                CardViewPemasokTotalBelumDiambil.visibility = View.VISIBLE
                CardViewPemasokTotalSudahDiambil.visibility = View.VISIBLE
                CardViewPengelolaTotalBanBekas.visibility = View.GONE
                CardViewPengelolaTotalSudahDiolah.visibility = View.GONE
            }
            "pengelola" -> {
                CardViewPemasokTotalBelumDiambil.visibility = View.GONE
                CardViewPemasokTotalSudahDiambil.visibility = View.GONE
                CardViewPengelolaTotalBanBekas.visibility = View.VISIBLE
                CardViewPengelolaTotalSudahDiolah.visibility = View.VISIBLE
            }
            else -> {
                Toast.makeText(requireContext(), "Level pengguna tidak valid", Toast.LENGTH_SHORT).show()
            }
        }

        // Kembalikan root view
        return root
    }

    private fun getPemasokIdFromSharedPreferences(): Int {
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

    private fun pemasokDataTotal(userId: Int) {
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
                        color = Color.BLUE
                        valueTextColor = Color.BLACK
                    }

                    val dataSetSudahDiambil = BarDataSet(entriesSudahDiambil, "Stok Sudah Diambil").apply {
                        color = Color.RED
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


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}