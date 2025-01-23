package com.example.repro.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.utils.ColorTemplate
import com.example.repro.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private lateinit var ScrollViewPemasok: ScrollView
    private lateinit var ScrollViewPengelola: ScrollView
    private lateinit var ScrollViewAdmin: ScrollView
    private lateinit var laporanDataPemasok: LineChart
    private lateinit var laporanDataPengelola: LineChart
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

        // Mengambil LineChart dari binding
        laporanDataPemasok = binding?.laporanDataPemasok!!

        // Siapkan data untuk LineChart
        val entries = ArrayList<Entry>()
        entries.add(Entry(1f, 30f))
        entries.add(Entry(2f, 50f))
        entries.add(Entry(3f, 70f))
        entries.add(Entry(4f, 100f))

        // Set data set untuk LineChart
        val dataSet = LineDataSet(entries, "Label Data")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()

        // Buat LineData
        val lineData = LineData(dataSet)

        // Set data ke chart
        laporanDataPemasok.data = lineData

        // Menampilkan chart
        laporanDataPemasok.invalidate()

        // Kembalikan root view
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
