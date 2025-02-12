package com.example.repro.ui.harga_ban

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.repro.api.ApiResponse
import com.example.repro.api.RetrofitClient
import com.example.repro.databinding.FragmentHargaBanBinding
import com.example.repro.ui.harga_ban.tambah.TambahHargaActivity
import com.example.repro.model.postHargaBan
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HargaBanFragment : Fragment() {

    private var _binding: FragmentHargaBanBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HargaBanAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHargaBanBinding.inflate(inflater, container, false)
        val root = binding.root

        recyclerView = binding.recyclerViewDaftarHargaBan
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        fetchHargaBanData()

        return root
    }

    private fun fetchHargaBanData() {
        RetrofitClient.instance.getHargaBan().enqueue(object : Callback<ApiResponse<List<postHargaBan>>> {
            override fun onResponse(call: Call<ApiResponse<List<postHargaBan>>>, response: Response<ApiResponse<List<postHargaBan>>>) {
                if (response.isSuccessful && response.body()?.status == true) {
                    val hargaBanList = response.body()?.data ?: emptyList()
                    adapter = HargaBanAdapter(hargaBanList)
                    recyclerView.adapter = adapter
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<postHargaBan>>>, t: Throwable) {
                // Handle failure
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}