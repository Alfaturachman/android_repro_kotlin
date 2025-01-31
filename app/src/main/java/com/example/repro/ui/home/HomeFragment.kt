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
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.example.repro.databinding.FragmentHomeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.HashMap


class HomeFragment : Fragment() {
    private lateinit var pemasokTotalBelumAmbil: TextView
    private lateinit var pemasokTotalSudahAmbil: TextView
    private lateinit var laporanDataPemasok: LineChart
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

        // Mengambil LineChart dari binding
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

                    // Siapkan data untuk LineChart
                    val entries = ArrayList<Entry>()
                    entries.add(Entry(1f, stokData.belumDiambil))
                    entries.add(Entry(2f, stokData.sudahDiambil))

                    // Set data set untuk LineChart
                    val dataSet = LineDataSet(entries, "Stok Data")
                    dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()

                    // Buat LineData
                    val lineData = LineData(dataSet)

                    // Set data ke chart
                    laporanDataPemasok.data = lineData

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