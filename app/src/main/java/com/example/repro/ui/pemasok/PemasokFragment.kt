package com.example.repro.ui.pemasok

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.repro.R
import com.example.repro.api.ApiResponse
import com.example.repro.api.RetrofitClient
import com.example.repro.databinding.FragmentPemasokBinding
import com.example.repro.model.PemasokStok
import com.example.repro.ui.pemasok.tambah.TambahStokActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PemasokFragment : Fragment() {

    private val viewModel: PemasokViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var _binding: FragmentPemasokBinding? = null
    private val binding get() = _binding!!
    private var pemasokId: Int = -1
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StokAdapter

    // ActivityResultLauncher
    val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            refreshData()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPemasokBinding.inflate(inflater, container, false)
        val root = binding.root

        recyclerView = binding.recyclerViewDaftarStok
        recyclerView.layoutManager = LinearLayoutManager(context)

        // ID dari SharedPreferences
        pemasokId = getPemasokIdFromSharedPreferences()
        if (pemasokId != -1) {
            getStokData(pemasokId)
        } else {
            // Handle jika id_pemasok tidak ditemukan
        }

        // Mengecek izin lokasi
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        // Mengecek apakah GPS aktif
        if (!isGPSEnabled()) {
            showGPSDialog()
        }

        // Tombol tambah stok
        binding.btnTambahStok.setOnClickListener {
            val intent = Intent(requireContext(), TambahStokActivity::class.java)
            startForResult.launch(intent)
        }

        return root
    }

    private fun getPemasokIdFromSharedPreferences(): Int {
        val sharedPreferences = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("id_user_detail", -1)
    }

    private fun getStokData(pemasokId: Int) {
        val apiService = RetrofitClient.instance
        val requestBody = hashMapOf("id_pemasok" to pemasokId)
        Log.d("ID Pemasok Fragment", "Request: $pemasokId")

        val call = apiService.getStokByPemasokId(requestBody)

        call.enqueue(object : Callback<ApiResponse<List<PemasokStok>>> {
            override fun onResponse(call: Call<ApiResponse<List<PemasokStok>>>, response: Response<ApiResponse<List<PemasokStok>>>) {
                if (response.isSuccessful) {
                    Log.d("ID Pemasok Fragment", "Response: ${response.body()}")

                    if (response.body()?.status == true) {
                        val stokList = response.body()?.data ?: emptyList()
                        if (stokList.isNotEmpty()) {
                            // Jika ada stok, sembunyikan cardViewPeringatan
                            binding.cardViewPeringatan.visibility = View.GONE
                            recyclerView.visibility = View.VISIBLE

                            adapter = StokAdapter(stokList, startForResult) {
                                refreshData() // Panggil refreshData() saat data dihapus
                            }

                            recyclerView.adapter = adapter
                            recyclerView.adapter = adapter
                        } else {
                            // Jika tidak ada stok, tampilkan cardViewPeringatan
                            binding.cardViewPeringatan.visibility = View.VISIBLE
                            recyclerView.visibility = View.GONE
                        }
                    } else {
                        Log.e("ID Pemasok Fragment", "Error: ${response.body()?.message}")
                        binding.cardViewPeringatan.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                    }
                } else {
                    Log.e("ID Pemasok Fragment", "Response Error: ${response.code()} - ${response.message()}")
                    binding.cardViewPeringatan.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<PemasokStok>>>, t: Throwable) {
                Log.e("ID Pemasok Fragment", "Request Failed: ${t.message}")
                Toast.makeText(context, "Gagal mengambil data: ${t.message}", Toast.LENGTH_SHORT).show()
                binding.cardViewPeringatan.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            }
        })
    }

    private fun isGPSEnabled(): Boolean {
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun showGPSDialog() {
        // Inflate layout custom dialog
        val dialogView: View = LayoutInflater.from(requireContext()).inflate(R.layout.alert_dialog, null)

        // AlertDialog dengan custom view dan tema
        val alertDialog = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog)
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

        // Button Tidak (Hanya menutup dialog)
        btnTidak.setOnClickListener {
            alertDialog.dismiss()
        }

        // Button Ya (Buka pengaturan lokasi)
        btnYa.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            alertDialog.dismiss() // Tutup dialog
        }

        // Jika dialog di-dismiss (diklik di luar atau tombol back)
        alertDialog.setOnDismissListener {

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

    private fun refreshData() {
        getStokData(pemasokId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}