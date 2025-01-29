package com.example.repro.ui.pemasok

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.repro.databinding.FragmentPemasokBinding
import com.example.repro.ui.pemasok.TambahStokActivity

class PemasokFragment : Fragment() {

    companion object {
        fun newInstance() = PemasokFragment()
    }

    private val viewModel: PemasokViewModel by viewModels()
    private var _binding: FragmentPemasokBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Gunakan View Binding
        _binding = FragmentPemasokBinding.inflate(inflater, container, false)
        val root = binding.root

        // Akses tombol langsung dari binding
        binding.btnTambahStok.setOnClickListener {
            val intent = Intent(requireContext(), TambahStokActivity::class.java)
            startActivity(intent)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
