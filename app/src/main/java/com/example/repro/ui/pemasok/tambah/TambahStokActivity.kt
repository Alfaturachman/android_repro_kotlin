package com.example.repro.ui.pemasok.tambah

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import com.example.repro.R
import com.example.repro.api.ApiResponse
import com.example.repro.api.RetrofitClient
import com.example.repro.model.HargaKendaraan
import com.example.repro.model.StokRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class TambahStokActivity : AppCompatActivity() {

    // UI Components
    private lateinit var etTanggal: EditText
    private lateinit var radioGroup: RadioGroup
    private lateinit var etJumlahStok: EditText
    private lateinit var etHargaPerKg: EditText
    private lateinit var etTotalHarga: EditText
    private lateinit var etKeterangan: EditText
    private lateinit var btnSimpan: Button
    private lateinit var imagePreview: ImageView
    private lateinit var btnUpload: Button
    private lateinit var btnKembali: ImageButton

    // Location and other variables
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latitude: Double? = null
    private var longitude: Double? = null
    private var hargaKendaraanMap = mutableMapOf<String, String>()
    private var pemasokId: Int = -1
    private var imageUri: Uri? = null

    // Activity result launcher for image picker
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imageUri = it
            imagePreview.setImageURI(it)
            btnUpload.isVisible = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tambah_stok)

        window.statusBarColor = resources.getColor(R.color.white, theme)

        initializeViews()
        setupDateTimeHandler()
        checkPermissionsAndGetLocation()
        setupClickListeners()
        fetchHargaKendaraan()
    }

    private fun initializeViews() {
        etTanggal = findViewById(R.id.etTanggal)
        radioGroup = findViewById(R.id.radioGroupJenisKendaraan)
        etJumlahStok = findViewById(R.id.etJumlahStok)
        etHargaPerKg = findViewById(R.id.etHargaPerKg)
        etTotalHarga = findViewById(R.id.etTotalHarga)
        etKeterangan = findViewById(R.id.etKeterangan)
        btnSimpan = findViewById(R.id.btnSimpanStok)
        imagePreview = findViewById(R.id.imagePreview)
        btnUpload = findViewById(R.id.btnUpload)
        btnKembali = findViewById(R.id.btnKembali)

        // Get user ID from shared preferences
        val sharedPreferences: SharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)
        pemasokId = sharedPreferences.getInt("id_user_detail", -1)
    }

    private fun setupDateTimeHandler() {
        handler = Handler(Looper.getMainLooper())
        runnable = object : Runnable {
            override fun run() {
                val currentDateTime = SimpleDateFormat("dd MMMM yyyy HH:mm:ss", Locale("id", "ID")).format(Date())
                etTanggal.setText(currentDateTime)
                handler.postDelayed(this, 1000)
            }
        }
        handler.post(runnable)
    }

    private fun checkPermissionsAndGetLocation() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        } else {
            getLocation()
        }
    }

    private fun setupClickListeners() {
        btnUpload.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        btnKembali.setOnClickListener {
            finish()
        }

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            updateHargaBasedOnVehicleType(checkedId)
        }

        etJumlahStok.doAfterTextChanged {
            calculateTotalHarga()
        }

        btnSimpan.setOnClickListener {
            validateAndSaveData()
        }
    }

    private fun updateHargaBasedOnVehicleType(checkedId: Int) {
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

    private fun calculateTotalHarga() {
        val jumlah = etJumlahStok.text.toString().toDoubleOrNull()
        val hargaPerKgText = etHargaPerKg.text.toString()

        if (hargaPerKgText.isNotEmpty()) {
            val hargaPerKg = parseRupiahToDouble(hargaPerKgText)

            if (jumlah != null) {
                val total = jumlah * hargaPerKg
                etTotalHarga.setText(formatRupiah(total))
            } else {
                etTotalHarga.setText("")
            }
        }
    }

    private fun validateAndSaveData() {
        val jumlahStok = etJumlahStok.text.toString().trim()
        val keterangan = etKeterangan.text.toString().trim()
        val hargaPerKg = etHargaPerKg.text.toString()
        val totalHarga = etTotalHarga.text.toString()
        val selectedJenis = when (radioGroup.checkedRadioButtonId) {
            R.id.radioMobil -> "Mobil"
            R.id.radioMotor -> "Motor"
            else -> ""
        }

        if (TextUtils.isEmpty(jumlahStok) ||
            TextUtils.isEmpty(hargaPerKg) ||
            TextUtils.isEmpty(totalHarga) ||
            TextUtils.isEmpty(selectedJenis)
        ) {
            Toast.makeText(this, "Harap isi semua data sebelum menyimpan!", Toast.LENGTH_SHORT).show()
            return
        }

        uploadStokWithImage()
    }

    private fun uploadStokWithImage() {
        val file = imageUri?.let { uriToFile(this, it) } ?: run {
            Toast.makeText(this, "Gagal membaca file dari gambar", Toast.LENGTH_SHORT).show()
            return
        }

        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("foto", file.name, requestFile)

        val jumlahStok = etJumlahStok.text.toString().trim().toInt()
        val hargaPerKg = parseRupiahToDouble(etHargaPerKg.text.toString())
        val totalHarga = parseRupiahToDouble(etTotalHarga.text.toString())
        val keterangan = etKeterangan.text.toString().trim()
        val selectedJenis = when (radioGroup.checkedRadioButtonId) {
            R.id.radioMobil -> "Mobil"
            R.id.radioMotor -> "Motor"
            else -> ""
        }

        if (selectedJenis.isEmpty()) {
            Toast.makeText(this, "Silakan pilih jenis kendaraan", Toast.LENGTH_SHORT).show()
            return
        }

        if (jumlahStok <= 0 || hargaPerKg <= 0.0 || totalHarga <= 0.0) {
            Toast.makeText(this, "Jumlah, harga, dan total harga tidak boleh kosong atau nol", Toast.LENGTH_SHORT).show()
            return
        }

        val idPemasok = pemasokId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val jenis = selectedJenis.toRequestBody("text/plain".toMediaTypeOrNull())
        val jumlahStokBody = jumlahStok.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val hargaBody = hargaPerKg.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val totalHargaBody = totalHarga.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val lokasi = "$latitude,$longitude".toRequestBody("text/plain".toMediaTypeOrNull())
        val keteranganBody = keterangan.toRequestBody("text/plain".toMediaTypeOrNull())

        Log.d("UPLOAD_STOK", "idPemasok: $pemasokId")
        Log.d("UPLOAD_STOK", "jenis: $selectedJenis")
        Log.d("UPLOAD_STOK", "jumlahStok: $jumlahStok")
        Log.d("UPLOAD_STOK", "hargaPerKg: $hargaPerKg")
        Log.d("UPLOAD_STOK", "totalHarga: $totalHarga")
        Log.d("UPLOAD_STOK", "lokasi: $latitude,$longitude")
        Log.d("UPLOAD_STOK", "keterangan: $keterangan")
        Log.d("UPLOAD_STOK", "filePath: ${file.absolutePath}")

        // GUNAKAN CALL YANG MENGEMBALIKAN RAW RESPONSE UNTUK DEBUGGING
        val call = RetrofitClient.instance.uploadStok(
            idPemasok,
            jenis,
            jumlahStokBody,
            hargaBody,
            totalHargaBody,
            lokasi,
            keteranganBody,
            body
        )

        // Convert to raw response untuk debugging
        val rawCall = call.clone()

        rawCall.enqueue(object : Callback<ApiResponse<String>> {
            override fun onResponse(call: Call<ApiResponse<String>>, response: Response<ApiResponse<String>>) {
                Log.d("UPLOAD_RESPONSE", "HTTP Code: ${response.code()}")
                Log.d("UPLOAD_RESPONSE", "Is Successful: ${response.isSuccessful}")

                // PENTING: Log raw response body untuk melihat apa yang dikembalikan server
                try {
                    val rawBody = response.raw().body?.string()
                    Log.d("UPLOAD_RESPONSE", "Raw Response Body: '$rawBody'")

                    // Cek jika response kosong atau tidak valid
                    if (rawBody.isNullOrEmpty()) {
                        Log.e("UPLOAD_RESPONSE", "Response body is null or empty")
                        Toast.makeText(this@TambahStokActivity, "Server tidak mengembalikan response", Toast.LENGTH_SHORT).show()
                        return
                    }

                    // Cek jika response berisi HTML error
                    if (rawBody.contains("<html>") || rawBody.contains("<!DOCTYPE")) {
                        Log.e("UPLOAD_RESPONSE", "Server returned HTML error page")
                        Toast.makeText(this@TambahStokActivity, "Server error - HTML response", Toast.LENGTH_SHORT).show()
                        return
                    }

                    // Cek jika response berisi PHP error
                    if (rawBody.contains("Parse error") || rawBody.contains("Fatal error") || rawBody.contains("Warning:")) {
                        Log.e("UPLOAD_RESPONSE", "PHP Error in response: $rawBody")
                        Toast.makeText(this@TambahStokActivity, "Server PHP error", Toast.LENGTH_SHORT).show()
                        return
                    }

                } catch (e: Exception) {
                    Log.e("UPLOAD_RESPONSE", "Error reading raw response: ${e.message}")
                }

                if (response.isSuccessful) {
                    Log.d("UPLOAD_RESPONSE", "Response Body: ${response.body()}")
                    handleSaveResponse(response)
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    Log.e("UPLOAD_RESPONSE", "Error Body: $errorBody")
                    Toast.makeText(this@TambahStokActivity, "HTTP Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<String>>, t: Throwable) {
                Log.e("UPLOAD_RESPONSE", "onFailure: ${t.localizedMessage}")
                Log.e("UPLOAD_RESPONSE", "Exception type: ${t.javaClass.simpleName}")
                t.printStackTrace()

                // Handle specific JSON parsing errors
                if (t is com.google.gson.JsonSyntaxException || t.message?.contains("malformed JSON") == true) {
                    Toast.makeText(this@TambahStokActivity, "Server response bukan JSON valid. Cek server logs.", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@TambahStokActivity, "Gagal: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun <T> handleSaveResponse(response: Response<ApiResponse<T>>) {
        if (response.isSuccessful && response.body()?.status == true) {
            Toast.makeText(this@TambahStokActivity, "Data Berhasil Disimpan", Toast.LENGTH_SHORT).show()
            setResult(RESULT_OK)
            finish()
        } else {
            Toast.makeText(
                this@TambahStokActivity,
                "Gagal menyimpan data: ${response.message()}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun getLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (isGPSEnabled()) {
            getLastLocation()
        } else {
            showGPSDialog()
        }
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

    private fun fetchHargaKendaraan() {
        RetrofitClient.instance.getHargaKendaraan().enqueue(object : Callback<ApiResponse<List<HargaKendaraan>>> {
            override fun onResponse(
                call: Call<ApiResponse<List<HargaKendaraan>>>,
                response: Response<ApiResponse<List<HargaKendaraan>>>
            ) {
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
            finish()
        }

        btnYa.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            alertDialog.dismiss()
        }

        alertDialog.setOnDismissListener {
            finish()
        }

        alertDialog.show()

        val window = alertDialog.window
        window?.setLayout(
            (Resources.getSystem().displayMetrics.widthPixels * 0.90).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    private fun uriToFile(context: Context, uri: Uri): File? {
        return context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val tempFile = File.createTempFile("upload_", ".jpg", context.cacheDir)
            tempFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            tempFile
        }
    }

    companion object {
        fun formatRupiah(value: Double): String {
            val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
            return formatter.format(value).replace(",00", "")
        }

        fun parseRupiahToDouble(rupiah: String): Double {
            return rupiah.replace("Rp", "")
                .replace(".", "")
                .trim()
                .toDoubleOrNull() ?: 0.0
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
    }
}