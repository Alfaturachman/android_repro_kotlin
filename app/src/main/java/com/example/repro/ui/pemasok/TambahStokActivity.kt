package com.example.repro.ui.pemasok

import android.Manifest
import android.content.pm.PackageManager
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.repro.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.os.Handler
import android.text.TextUtils

class TambahStokActivity : AppCompatActivity() {

    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latitude: Double? = null
    private var longitude: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tambah_stok)

        window.statusBarColor = resources.getColor(R.color.white, theme)

        // Ambil referensi view
        val etTanggal = findViewById<EditText>(R.id.etTanggal)
        val radioGroup = findViewById<RadioGroup>(R.id.radioGroupJenisKendaraan)
        val etJumlahStok = findViewById<EditText>(R.id.etJumlahStok)
        val etHargaPerKg = findViewById<EditText>(R.id.etHargaPerKg)
        val etTotalHarga = findViewById<EditText>(R.id.etTotalHarga)
        val btnSimpan = findViewById<Button>(R.id.btnSimpanStok)

        handler = Handler(Looper.getMainLooper())

        runnable = object : Runnable {
            override fun run() {
                val currentDateTime = SimpleDateFormat("dd MMMM yyyy HH:mm:ss", Locale("id", "ID")).format(Date())
                etTanggal.setText(currentDateTime)
                handler.postDelayed(this, 1000)
            }
        }
        handler.post(runnable)

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }

        // Inisialisasi Location Provider
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Cek dan dapatkan lokasi
        if (isGPSEnabled()) {
            getLastLocation()
        } else {
            showGPSDialog()
        }

        // Button Kembali
        val btnKembali: ImageButton = findViewById(R.id.btnKembali)
        btnKembali.setOnClickListener {
            finish()
        }

        // Button Simpan
        btnSimpan.setOnClickListener {
            val jumlahStok = etJumlahStok.text.toString().trim()
            val hargaPerKg = etHargaPerKg.text.toString().trim()
            val totalHarga = etTotalHarga.text.toString().trim()

            val selectedJenis = when (radioGroup.checkedRadioButtonId) {
                R.id.radioMobil -> "Mobil"
                R.id.radioMotor -> "Motor"
                else -> ""
            }

            // Cek apakah ada input yang kosong
            if (TextUtils.isEmpty(jumlahStok) ||
                TextUtils.isEmpty(hargaPerKg) ||
                TextUtils.isEmpty(totalHarga) ||
                TextUtils.isEmpty(selectedJenis)) {

                Toast.makeText(this, "Harap isi semua data sebelum menyimpan!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val tanggal = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("id", "ID")).format(Date())

            Log.d("TambahStokActivity", "Tanggal: $tanggal")
            Log.d("TambahStokActivity", "Jenis Kendaraan: $selectedJenis")
            Log.d("TambahStokActivity", "Jumlah Stok: $jumlahStok")
            Log.d("TambahStokActivity", "Harga per kg: $hargaPerKg")
            Log.d("TambahStokActivity", "Total Harga: $totalHarga")

            if (latitude != null && longitude != null) {
                Log.d("TambahStokActivity", "Koordinat: $latitude, $longitude")
            } else {
                Log.d("TambahStokActivity", "Koordinat: Tidak tersedia")
            }

            Toast.makeText(this, "Data Berhasil Disimpan", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isGPSEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun showGPSDialog() {
        AlertDialog.Builder(this)
            .setTitle("GPS Tidak Aktif")
            .setMessage("GPS harus diaktifkan untuk mendapatkan lokasi.")
            .setPositiveButton("Aktifkan") { _, _ ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                latitude = location.latitude
                longitude = location.longitude
                Log.d("TambahStokActivity", "Koordinat: $latitude, $longitude")
            } else {
                Log.d("TambahStokActivity", "Koordinat tidak tersedia, mencoba metode lain...")
                requestNewLocationData()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val locationRequest = com.google.android.gms.location.LocationRequest.create().apply {
            priority = com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
            interval = 5000
            fastestInterval = 2000
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, object :
            com.google.android.gms.location.LocationCallback() {
            override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
                val location = locationResult.lastLocation
                if (location != null) {
                    latitude = location.latitude
                    longitude = location.longitude
                    Log.d("TambahStokActivity", "Koordinat (Metode Baru): $latitude, $longitude")
                    fusedLocationClient.removeLocationUpdates(this)
                }
            }
        }, null)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable) // Hentikan handler saat activity dihancurkan
    }
}
