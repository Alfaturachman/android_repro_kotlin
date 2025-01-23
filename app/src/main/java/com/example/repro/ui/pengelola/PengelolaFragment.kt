package com.example.repro.ui.pengelola

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.repro.R
import com.example.repro.api.ApiService
import com.example.repro.api.RetrofitClient
import com.example.repro.api.ApiResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PengelolaFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private var adapter: PemasokAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate layout fragment
        val root = inflater.inflate(R.layout.fragment_pengelola, container, false)

        // Inisialisasi RecyclerView
        recyclerView = root.findViewById(R.id.recyclerViewDaftarPemasok)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Fetch data dari API
        fetchPemasokData()

        return root
    }

    private fun fetchPemasokData() {
        // Menggunakan instance Retrofit dari RetrofitClient
        val apiService = RetrofitClient.retrofitInstance.create(ApiService::class.java)

        val call = apiService.getPemasokList()
        call.enqueue(object : Callback<ApiResponse<List<Pemasok>>> {
            override fun onResponse(
                call: Call<ApiResponse<List<Pemasok>>>,
                response: Response<ApiResponse<List<Pemasok>>>
            ) {
                if (response.isSuccessful && response.body()?.status == true) {
                    // Data berhasil diambil
                    val pemasokList = response.body()?.data ?: emptyList()
                    adapter = PemasokAdapter(requireContext(), pemasokList)
                    recyclerView.adapter = adapter
                } else {
                    Toast.makeText(context, "Data tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<Pemasok>>>, t: Throwable) {
                // Handle kegagalan mengambil data
                Toast.makeText(context, "Gagal mengambil data: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
