package com.example.repro.ui.harga_ban

import android.app.Activity.RESULT_OK
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
import com.example.repro.model.postHargaBan
import com.example.repro.ui.harga_ban.edit.EditHargaActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HargaBanFragment : Fragment() {

    private var _binding: FragmentHargaBanBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HargaBanAdapter

    // Request code untuk EditHargaActivity
    private val EDIT_HARGA_REQUEST_CODE = 1

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
                    adapter = HargaBanAdapter(hargaBanList) { hargaBan ->
                        val intent = Intent(requireContext(), EditHargaActivity::class.java)
                        intent.putExtra("id", hargaBan.id)
                        intent.putExtra("jenis", hargaBan.jenis)
                        intent.putExtra("harga", hargaBan.harga.toFloat())
                        startActivityForResult(intent, EDIT_HARGA_REQUEST_CODE)
                    }
                    recyclerView.adapter = adapter
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<postHargaBan>>>, t: Throwable) {
                // Handle failure
            }
        })
    }

    // Terima hasil dari EditHargaActivity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_HARGA_REQUEST_CODE && resultCode == RESULT_OK) {
            // Perbarui data di Fragment
            fetchHargaBanData()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}