package com.example.repro.ui.pengelola.riwayat_detail

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.repro.R

class RiwayatDetailFragment : Fragment() {

    companion object {
        fun newInstance(id: Int) = RiwayatDetailFragment()
    }

    private val viewModel: RiwayatDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_riwayat_detail, container, false)
    }
}