package com.example.repro.ui.pemasok.tambah

import android.Manifest
import android.content.pm.PackageManager
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
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
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.core.widget.doAfterTextChanged
import com.example.repro.api.ApiResponse
import com.example.repro.api.ApiService
import com.example.repro.api.RetrofitClient
import com.example.repro.model.HargaKendaraan
import com.example.repro.model.StokRequest
import com.example.repro.model.UpdateHargaBanRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat

class TambahStokActivity : AppCompatActivity() {

    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latitude: Double? = null
    private var longitude: Double? = null
    private var hargaKendaraanMap = mutableMapOf<String, String>()
    private var pemasokId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tambah_stok)

        window.statusBarColor = resources.getColor(R.color.white, theme)

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

        val sharedPreferences: SharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)
        pemasokId = sharedPreferences.getInt("id_user", -1)

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (isGPSEnabled()) {
            getLastLocation()
        } else {
            showGPSDialog()
        }

        val btnKembali: ImageButton = findViewById(R.id.btnKembali)
        btnKembali.setOnClickListener {
            finish()
        }

        // Panggil API untuk mendapatkan harga kendaraan
        fetchHargaKendaraan()

        // Saat jenis kendaraan berubah, hanya update harga per kg, jangan langsung hitung total
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val jenisKendaraan = when (checkedId) {
                R.id.radioMobil -> "Mobil"
                R.id.radioMotor -> "Motor"
                else -> ""
            }

            if (hargaKendaraanMap.containsKey(jenisKendaraan)) {
                val hargaPerKg = hargaKendaraanMap[jenisKendaraan]?.toDoubleOrNull() ?: 0.0
                etHargaPerKg.setText(formatRupiah(hargaPerKg))
            } else {
                etHargaPerKg.setText("")
            }

            etTotalHarga.setText("")
        }

        // Saat jumlah stok diubah, baru hitung total harga
        etJumlahStok.doAfterTextChanged {
            val jumlah = etJumlahStok.text.toString().toDoubleOrNull()
            val hargaPerKg = parseRupiahToInt(etHargaPerKg.text.toString()).toDouble() // Perbaiki ini!

            if (jumlah != null) {
                val total = jumlah * hargaPerKg
                etTotalHarga.setText(formatRupiah(total))
            } else {
                etTotalHarga.setText("")
            }
        }

        btnSimpan.setOnClickListener {
            val jumlahStok = etJumlahStok.text.toString().trim()
            val hargaPerKg = parseRupiahToInt(etHargaPerKg.text.toString()).toString()
            val totalHarga = parseRupiahToInt(etTotalHarga.text.toString()).toString()
            val selectedJenis = when (radioGroup.checkedRadioButtonId) {
                R.id.radioMobil -> "Mobil"
                R.id.radioMotor -> "Motor"
                else -> ""
            }

            // Validasi input
            if (TextUtils.isEmpty(jumlahStok) ||
                TextUtils.isEmpty(hargaPerKg) ||
                TextUtils.isEmpty(totalHarga) ||
                TextUtils.isEmpty(selectedJenis)
            ) {
                Toast.makeText(this, "Harap isi semua data sebelum menyimpan!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Buat request object
            val stokRequest = StokRequest(
                idPemasok = pemasokId,
                jenis = selectedJenis,
                jumlahStok = jumlahStok.toInt(),
                harga = hargaPerKg.toDouble(),
                totalHarga = totalHarga.toDouble(),
                lokasi = "$latitude,$longitude"
            )

            // Panggil API
            RetrofitClient.instance.simpanStok(stokRequest).enqueue(object : Callback<ApiResponse<StokRequest>> {
                override fun onResponse(
                    call: Call<ApiResponse<StokRequest>>,
                    response: Response<ApiResponse<StokRequest>>
                ) {
                    if (response.isSuccessful && response.body()?.status == true) {
                        Toast.makeText(this@TambahStokActivity, "Data Berhasil Disimpan", Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        Toast.makeText(this@TambahStokActivity, "Gagal menyimpan data: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse<StokRequest>>, t: Throwable) {
                    Toast.makeText(this@TambahStokActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun fetchHargaKendaraan() {
        val apiService = RetrofitClient.instance
        apiService.getHargaKendaraan().enqueue(object : Callback<ApiResponse<List<HargaKendaraan>>> {
            override fun onResponse(call: Call<ApiResponse<List<HargaKendaraan>>>, response: Response<ApiResponse<List<HargaKendaraan>>>) {
                if (response.isSuccessful) {
                    response.body()?.data?.forEach {
                        hargaKendaraanMap[it.jenis] = it.harga
                    }
                    Log.d("Response API", "Harga kendaraan: $hargaKendaraanMap")
                } else {
                    Log.e("Response API", "Response gagal: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<HargaKendaraan>>>, t: Throwable) {
                Log.e("API Error", "Gagal mengambil data harga kendaraan: ${t.message}")
            }
        })
    }

    private fun isGPSEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun showGPSDialog() {
        // Inflate layout custom dialog
        val dialogView: View = LayoutInflater.from(this).inflate(R.layout.alert_dialog, null)

        // AlertDialog dengan custom view dan tema
        val alertDialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)
            .setView(dialogView)
            .create()

        // Inisialisasi custom dialog
        val tvDialogTitle = dialogView.findViewById<TextView>(R.id.tvDialogTitle)
        val tvDialogMessage = dialogView.findViewById<TextView>(R.id.tvDialogMessage)
        val btnTidak = dialogView.findViewById<Button>(R.id.btnTidak)
        val btnYa = dialogView.findViewById<Button>(R.id.btnYa)

        tvDialogTitle.text = "GPS Tidak Aktif"
        tvDialogMessage.text = "GPS harus diaktifkan untuk mendapatkan lokasi."
        btnTidak.text = "Tidak"
        btnYa.text = "Aktifkan"

        // Button Tidak
        btnTidak.setOnClickListener {
            finish() // Tutup aplikasi
        }

        // Button Ya
        btnYa.setOnClickListener {
            // Buka pengaturan lokasi
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            alertDialog.dismiss() // Tutup dialog
        }

        // Jika dialog di-dismiss (diklik di luar atau tombol back)
        alertDialog.setOnDismissListener {
            finish() // Tutup aplikasi
        }

        // Tampilkan dialog
        alertDialog.show()

        // Ukuran dialog
        val window = alertDialog.window
        window?.setLayout(
            (Resources.getSystem().displayMetrics.widthPixels * 0.90).toInt(),  // 90% dari lebar layar
            WindowManager.LayoutParams.WRAP_CONTENT
        )
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

    fun formatRupiah(value: Double): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        return formatter.format(value).replace(",00", "")
    }

    fun parseRupiahToInt(rupiah: String): Int {
        return rupiah.replace("Rp", "")
            .replace(".", "")
            .trim()
            .toIntOrNull() ?: 0
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable) // Hentikan handler saat activity dihancurkan
    }
}
