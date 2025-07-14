package com.example.repro.ui.pengelola.ambil

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.repro.R
import com.example.repro.utils.TSP
import com.example.repro.api.ApiResponse
import com.example.repro.api.RetrofitClient
import com.example.repro.model.AmbilStok
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AmbilStokActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AmbilStokAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var tvTotalJarakKeseluruhan: TextView

    val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        refreshData()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ambil_stok)

        window.statusBarColor = resources.getColor(R.color.white, theme)

        // Inisialisasi TextView untuk total jarak
        tvTotalJarakKeseluruhan = findViewById(R.id.tvTotalJarakKeseluruhan)

        // Button Kembali
        val btnKembali: ImageButton = findViewById(R.id.btnKembali)
        btnKembali.setOnClickListener {
            finish()
        }

        // Inisialisasi RecyclerView
        recyclerView = findViewById(R.id.recyclerViewDaftarPemasok)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Inisialisasi FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Cek GPS dan ambil data
        if (isGPSEnabled()) {
            fetchAmbilStokData()
        } else {
            showGPSDialog()
        }
    }

    private fun fetchAmbilStokData() {
        // Pastikan izin lokasi sudah diberikan
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                Log.d("GPS_DEBUG", "Lokasi berhasil didapatkan: ${location.latitude}, ${location.longitude}")
                processAmbilStokData(location.latitude, location.longitude)
            } else {
                Log.e("GPS_DEBUG", "lastLocation mengembalikan null, mencoba mendapatkan lokasi terbaru...")
                requestNewLocation()
            }
        }.addOnFailureListener { e ->
            Log.e("GPS_DEBUG", "Gagal mendapatkan lokasi terakhir: ${e.message}")
        }
    }

    private fun requestNewLocation() {
        val locationRequest = com.google.android.gms.location.LocationRequest.create().apply {
            priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 5000 // Update setiap 5 detik
            fastestInterval = 2000
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, object :
                com.google.android.gms.location.LocationCallback() {
                override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
                    val location = locationResult.lastLocation
                    if (location != null) {
                        Log.d("GPS_DEBUG", "Lokasi real-time didapatkan: ${location.latitude}, ${location.longitude}")
                        processAmbilStokData(location.latitude, location.longitude)
                        fusedLocationClient.removeLocationUpdates(this)
                    } else {
                        Log.e("GPS_DEBUG", "Lokasi real-time tetap null.")
                    }
                }
            }, null)
        }
    }

    private fun processAmbilStokData(latitude: Double, longitude: Double) {
        RetrofitClient.instance.getAmbilStok().enqueue(object : Callback<ApiResponse<List<AmbilStok>>> {
            override fun onResponse(call: Call<ApiResponse<List<AmbilStok>>>, response: Response<ApiResponse<List<AmbilStok>>>) {
                if (response.isSuccessful && response.body()?.status == true) {
                    val ambilStokList = response.body()?.data ?: emptyList()

                    if (ambilStokList.isEmpty()) {
                        tvTotalJarakKeseluruhan.text = "0.00"
                        adapter = AmbilStokAdapter(
                            emptyList(),
                            emptyList(),
                            startForResult,
                            this@AmbilStokActivity,
                            onDataUpdated = { refreshData() }
                        )
                        recyclerView.adapter = adapter
                        return
                    }

                    // Gunakan fungsi getOptimalRoute yang sudah diperbaiki
                    val (sortedAmbilStokList, totalDistance) = TSP.getOptimalRoute(
                        pengelolaLat = latitude,
                        pengelolaLong = longitude,
                        pemasokList = ambilStokList
                    )

                    // Set total jarak keseluruhan
                    tvTotalJarakKeseluruhan.text = "%.2f".format(totalDistance)

                    // Hitung jarak individual menggunakan fungsi yang terpisah
                    val jarakPemasokList = TSP.getIndividualDistances(
                        pengelolaLat = latitude,
                        pengelolaLong = longitude,
                        pemasokList = sortedAmbilStokList
                    )

                    // Set adapter dengan data yang sudah diurutkan
                    adapter = AmbilStokAdapter(
                        sortedAmbilStokList,
                        jarakPemasokList,
                        startForResult,
                        this@AmbilStokActivity,
                        onDataUpdated = { refreshData() }
                    )
                    recyclerView.adapter = adapter

                    // Debug logging
                    Log.d("TSP_DEBUG", "Total Distance: $totalDistance")
                    Log.d("TSP_DEBUG", "Sorted locations count: ${sortedAmbilStokList.size}")

                } else {
                    Log.e("API_DEBUG", "API response not successful or status false")
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<AmbilStok>>>, t: Throwable) {
                Log.e("API_DEBUG", "Gagal memuat data dari API: ${t.message}")
            }
        })
    }

    private fun isGPSEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun showGPSDialog() {
        val dialogView: View = LayoutInflater.from(this).inflate(R.layout.alert_dialog, null)

        val alertDialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)
            .setView(dialogView)
            .create()

        val tvDialogTitle = dialogView.findViewById<TextView>(R.id.tvDialogTitle)
        val tvDialogMessage = dialogView.findViewById<TextView>(R.id.tvDialogMessage)
        val btnTidak = dialogView.findViewById<Button>(R.id.btnTidak)
        val btnYa = dialogView.findViewById<Button>(R.id.btnYa)

        tvDialogTitle.text = "GPS Tidak Aktif"
        tvDialogMessage.text = "GPS harus diaktifkan untuk mendapatkan lokasi."
        btnTidak.text = "Tidak"
        btnYa.text = "Aktifkan"

        btnTidak.setOnClickListener {
            alertDialog.dismiss()
        }

        btnYa.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            alertDialog.dismiss()
        }

        alertDialog.show()
        val window = alertDialog.window
        window?.setLayout(
            (Resources.getSystem().displayMetrics.widthPixels * 0.90).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    private fun refreshData() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    processAmbilStokData(it.latitude, it.longitude)
                } ?: requestNewLocation()
            }
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            fetchAmbilStokData()
        }
    }
}