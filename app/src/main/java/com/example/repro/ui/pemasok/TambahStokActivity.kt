package com.example.repro.ui.pemasok

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.repro.R

class TambahStokActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tambah_stok)

        window.statusBarColor = resources.getColor(R.color.white, theme)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val spinnerJenisKendaraan: Spinner = findViewById(R.id.spinnerJenisKendaraan)

        // Buat adapter untuk Spinner menggunakan data dari string-array
        val adapter = ArrayAdapter(
            this,
            R.layout.custom_spinner_item, // Layout kustom
            resources.getStringArray(R.array.jenis_kendaraan_array)
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // Dropdown layout
        spinnerJenisKendaraan.adapter = adapter

        // Listener untuk mendapatkan nilai yang dipilih
        spinnerJenisKendaraan.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = parent?.getItemAtPosition(position).toString()
                // Lakukan sesuatu dengan item yang dipilih
                println("Jenis Kendaraan Terpilih: $selectedItem")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Aksi jika tidak ada item yang dipilih
            }
        })
    }

}