package com.example.repro.ui.pemasok

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.repro.api.ApiResponse
import com.example.repro.api.ApiService
import com.example.repro.api.RetrofitClient
import com.example.repro.databinding.FragmentPemasokBinding
import com.example.repro.ui.pemasok.StatusStok
import com.example.repro.ui.pengelola.Pemasok
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PemasokFragment : Fragment() {

    private val viewModel: PemasokViewModel by viewModels()
    private var _binding: FragmentPemasokBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StokAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPemasokBinding.inflate(inflater, container, false)
        val root = binding.root

        recyclerView = binding.recyclerViewDaftarStok
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Ambil id_pemasok dari SharedPreferences
        val pemasokId = getPemasokIdFromSharedPreferences()
        if (pemasokId != -1) {
            getStokData(pemasokId)
        } else {
            // Handle jika id_pemasok tidak ditemukan
        }

        // Tombol tambah stok
        binding.btnTambahStok.setOnClickListener {
            val intent = Intent(requireContext(), TambahStokActivity::class.java)
            startActivity(intent)
        }

        return root
    }

    private fun getPemasokIdFromSharedPreferences(): Int {
        val sharedPreferences = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("id_user", -1)
    }

    private fun getStokData(pemasokId: Int) {
        val apiService = RetrofitClient.instance
        val requestBody = hashMapOf("id_pemasok" to pemasokId)
        Log.d("ID Pemasok Fragment", "Request: $pemasokId")

        val call = apiService.getStokByPemasokId(requestBody)

        call.enqueue(object : Callback<ApiResponse<List<StatusStok>>> {
            override fun onResponse(call: Call<ApiResponse<List<StatusStok>>>, response: Response<ApiResponse<List<StatusStok>>>) {
                if (response.isSuccessful) {
                    // Log the successful response for debugging
                    Log.d("ID Pemasok Fragment", "Response: ${response.body()}")

                    if (response.body()?.status == true) {
                        val stokList = response.body()?.data ?: emptyList()
                        adapter = StokAdapter(stokList)
                        recyclerView.adapter = adapter
                    } else {
                        // Log the error message from the API response
                        Log.e("ID Pemasok Fragment", "Error: ${response.body()?.message}")
                        Toast.makeText(context, "Gagal mengambil data", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Log error response from server
                    Log.e("ID Pemasok Fragment", "Response Error: ${response.code()} - ${response.message()}")
                    Toast.makeText(context, "Gagal mengambil data: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<StatusStok>>>, t: Throwable) {
                // Log failure reason
                Log.e("ID Pemasok Fragment", "Request Failed: ${t.message}")
                Toast.makeText(context, "Gagal mengambil data: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}