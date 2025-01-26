package com.example.repro.ui.pemasok

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.repro.R
import com.example.repro.ui.pemasok.TambahStokActivity

class PemasokFragment : Fragment() {

    companion object {
        fun newInstance() = PemasokFragment()
    }

    private val viewModel: PemasokViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_pemasok, container, false)

        // Menangani klik pada Button btnTambahStok
        val btnTambahStok = rootView.findViewById<Button>(R.id.btnTambahStok)
        btnTambahStok.setOnClickListener {
            // Menampilkan Toast sebagai pemberitahuan
            Toast.makeText(context, "Tambah Stok Diklik!", Toast.LENGTH_SHORT).show()

            // Membuat Intent untuk membuka TambahStokActivity
            val intent = Intent(context, TambahStokActivity::class.java)

            // Menyertakan data tambahan (opsional, jika diperlukan)
            // intent.putExtra("key", "value")

            // Memulai Activity
            startActivity(intent)
        }

        return rootView
    }
}
