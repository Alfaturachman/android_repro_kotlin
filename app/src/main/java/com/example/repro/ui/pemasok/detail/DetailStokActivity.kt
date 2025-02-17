package com.example.repro.ui.pemasok.detail

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.repro.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.text.NumberFormat
import java.util.Locale

class DetailStokActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private var lokasi: String = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail_stok)

        // Inisialisasi TextView
        val tvTanggal = findViewById<TextView>(R.id.tvTanggal)
        val tvJenis = findViewById<TextView>(R.id.tvJenis)
        val tvJumlahStok = findViewById<TextView>(R.id.tvJumlahStok)
        val tvHargaPerKg = findViewById<TextView>(R.id.tvHargaPerKg)
        val tvTotalHarga = findViewById<TextView>(R.id.tvTotalHarga)

        // Ambil data dari Intent
        val tanggal = intent.getStringExtra("tanggal") ?: "Tidak Ada Data"
        val jenis = intent.getStringExtra("jenis") ?: "Tidak Ada Data"
        lokasi = intent.getStringExtra("lokasi") ?: "Tidak Ada Data"
        val jumlahStok = intent.getFloatExtra("jumlah_stok", -1f).toDouble()
        val hargaPerKg = intent.getFloatExtra("harga_ban", -1f).toDouble()
        val totalHargaBan = intent.getFloatExtra("total_harga_ban", -1f).toDouble()

        // Log data yang diambil dari Intent
        Log.d("DetailStokActivity", "Tanggal: $tanggal")
        Log.d("DetailStokActivity", "Jenis: $jenis")
        Log.d("DetailStokActivity", "Jumlah Stok: $jumlahStok")
        Log.d("DetailStokActivity", "Harga Per Kg: $hargaPerKg")
        Log.d("DetailStokActivity", "Total Harga: $totalHargaBan")

        // Format harga ke Rupiah
        val formatRupiah = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

        // Set nilai ke TextView dengan format Rupiah
        tvTanggal.text = tanggal
        tvJenis.text = jenis
        tvJumlahStok.text = "$jumlahStok kg"
        tvHargaPerKg.text = formatRupiah.format(hargaPerKg.toDouble()).replace(",00", "")
        tvTotalHarga.text = formatRupiah.format(totalHargaBan.toDouble()).replace(",00", "")

        // Inisialisasi MapView
        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Set lokasi awal
        // Ambil lokasi dalam bentuk string dan pecah menjadi dua bagian (latitude, longitude)
        val lokasiArray = lokasi.split(",")
        if (lokasiArray.size == 2) {
            val latitude = lokasiArray[0].toDoubleOrNull() ?: 0.0
            val longitude = lokasiArray[1].toDoubleOrNull() ?: 0.0
            val lokasiLatLng = LatLng(latitude, longitude)

            // Set lokasi pada map
            googleMap.addMarker(MarkerOptions().position(lokasiLatLng).title("Lokasi"))
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lokasiLatLng, 15f))
        } else {
            // Handle error jika format lokasi tidak sesuai
            Log.e("DetailStokActivity", "Format lokasi tidak valid")
        }

        // Aktifkan kontrol zoom dan navigasi
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isMyLocationButtonEnabled = true
    }

    // Menjaga lifecycle MapView agar tetap sesuai dengan Activity
    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }
}