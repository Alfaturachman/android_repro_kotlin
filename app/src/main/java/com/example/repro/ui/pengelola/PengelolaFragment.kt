package com.example.repro.ui.pengelola

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.repro.api.RetrofitClient
import com.example.repro.api.ApiResponse
import com.example.repro.databinding.FragmentPengelolaBinding
import com.example.repro.modal.getPemasok
import com.example.repro.ui.pengelola.ambil.AmbilStokActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PengelolaFragment : Fragment() {

    private var _binding: FragmentPengelolaBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private var adapter: PemasokAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate layout fragment and set up binding
        _binding = FragmentPengelolaBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize RecyclerView
        recyclerView = binding.recyclerViewDaftarPemasok
        recyclerView.layoutManager = LinearLayoutManager(context)

        binding.btnAmbilStok.setOnClickListener {
            val intent = Intent(context, AmbilStokActivity::class.java)
            startActivity(intent)
        }

        // Fetch data from API
        fetchPemasokData()

        return root
    }

    private fun getPemasokIdFromSharedPreferences(): Int {
        val sharedPreferences = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("id_user", -1)
    }

    private fun fetchPemasokData() {
        // Using Retrofit instance from RetrofitClient
        val apiService = RetrofitClient.instance

        val call = apiService.getPemasokList()
        call.enqueue(object : Callback<ApiResponse<List<getPemasok>>> {
            override fun onResponse(
                call: Call<ApiResponse<List<getPemasok>>>,
                response: Response<ApiResponse<List<getPemasok>>>
            ) {
                if (response.isSuccessful && response.body()?.status == true) {
                    // Data successfully fetched
                    val pemasokList = response.body()?.data ?: emptyList()
                    adapter = PemasokAdapter(requireContext(), pemasokList)
                    recyclerView.adapter = adapter
                } else {
                    Toast.makeText(context, "Data tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<getPemasok>>>, t: Throwable) {
                // Handle failure in fetching data
                Toast.makeText(context, "Gagal mengambil data: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
