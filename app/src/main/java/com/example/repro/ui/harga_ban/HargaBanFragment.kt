package com.example.repro.ui.harga_ban

import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.repro.databinding.FragmentHargaBanBinding
import com.example.repro.ui.harga_ban.tambah.TambahHargaActivity
import com.example.repro.ui.pemasok.PemasokViewModel

class HargaBanFragment : Fragment() {

    companion object {
        fun newInstance() = HargaBanFragment()
    }

    private val viewModel: PemasokViewModel by viewModels()
    private var _binding: FragmentHargaBanBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Gunakan View Binding
        _binding = FragmentHargaBanBinding.inflate(inflater, container, false)
        val root = binding.root

        // Akses tombol langsung dari binding
        binding.btnTambahHarga.setOnClickListener {
            val intent = Intent(requireContext(), TambahHargaActivity::class.java)
            startActivity(intent)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}