package com.example.repro.ui.pengelola.ambil

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.location.Geocoder
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.repro.R
import com.example.repro.api.ApiResponse
import com.example.repro.api.RetrofitClient
import com.example.repro.model.AmbilStokRequest
import com.example.repro.model.AmbilStok
import java.text.DecimalFormat
import java.util.Locale
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AmbilStokAdapter(
    private val ambilStokList: List<AmbilStok>,
    private val jarakPemasokList: List<Double>,
    private val startForResult: ActivityResultLauncher<Intent>,
    private val context: Context,
    private val prefs: SharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE),
    private val onDataUpdated: () -> Unit // Tambahkan callback untuk refresh data
) : RecyclerView.Adapter<AmbilStokAdapter.AmbilStokViewHolder>() {

    class AmbilStokViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNamaUsaha: TextView = itemView.findViewById(R.id.tvNamaUsaha)
        val tvNamaPemilik: TextView = itemView.findViewById(R.id.tvNamaPemilik)
        val tvAlamat: TextView = itemView.findViewById(R.id.tvAlamat)
        val tvJenisBan: TextView = itemView.findViewById(R.id.tvJenisBan)
        val tvTotalBerat: TextView = itemView.findViewById(R.id.tvTotalBerat)
        val tvTotalHarga: TextView = itemView.findViewById(R.id.tvTotalHarga)
        val tvJarak: TextView = itemView.findViewById(R.id.tvJarak)
        val btnAmbilStok: ImageView = itemView.findViewById(R.id.btnAmbilStok)
        val btnMaps: ImageView = itemView.findViewById(R.id.btnMaps)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AmbilStokViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.daftar_ambil_pemasok, parent, false)
        return AmbilStokViewHolder(view)
    }

    override fun onBindViewHolder(holder: AmbilStokViewHolder, position: Int) {
        val ambilStok = ambilStokList[position]
        val jarak = jarakPemasokList[position]

        val formatter = DecimalFormat("#,###")
        val formattedTotalHarga = formatter.format(ambilStok.total_harga.toDouble())

        holder.tvNamaUsaha.text = ambilStok.nama_usaha
        holder.tvNamaPemilik.text = "Pemilik ${ambilStok.nama_pemasok}"
        holder.tvJenisBan.text = ambilStok.jenis
        holder.tvTotalBerat.text = "${ambilStok.total_berat} kg"
        holder.tvTotalHarga.text = "Rp $formattedTotalHarga"
        holder.tvJarak.text = "Jarak: %.2f km".format(jarak)

        holder.tvAlamat.text = getAddressFromCoordinates(ambilStok.lokasi) ?: "Alamat tidak ditemukan"

        holder.btnAmbilStok.setOnClickListener {
            showAmbilDialog(ambilStok)
        }

        holder.btnMaps.setOnClickListener {
            showMapsDialog(ambilStok)
        }
    }

    override fun getItemCount(): Int = ambilStokList.size

    private fun showAmbilDialog(ambilStok: AmbilStok) {
        // Inflate layout custom dialog
        val dialogView: View = LayoutInflater.from(context).inflate(R.layout.alert_dialog, null)

        // AlertDialog dengan custom view dan tema
        val alertDialog = AlertDialog.Builder(context, R.style.CustomAlertDialog)
            .setView(dialogView)
            .create()

        // Inisialisasi custom dialog
        val tvDialogTitle = dialogView.findViewById<TextView>(R.id.tvDialogTitle)
        val tvDialogMessage = dialogView.findViewById<TextView>(R.id.tvDialogMessage)
        val btnTidak = dialogView.findViewById<Button>(R.id.btnTidak)
        val btnYa = dialogView.findViewById<Button>(R.id.btnYa)

        tvDialogTitle.text = "Konfirmasi Ambil Stok"
        tvDialogMessage.text = "Apakah Anda yakin ingin mengambil stok dari pemilik ${ambilStok.nama_pemasok}?"
        btnTidak.text = "Tidak"
        btnYa.text = "Ya"

        // Button Tidak
        btnTidak.setOnClickListener {
            alertDialog.dismiss() // Tutup dialog
        }

        // Button Ya
        btnYa.setOnClickListener {
            val requestBody = AmbilStokRequest(
                id_pemasok = ambilStok.id_pemasok.toInt(),
                id_pengelola = getUserId(),
                id_stok = ambilStok.id.toInt(),
                jumlah_stok = ambilStok.total_berat.toDouble()
            )

            RetrofitClient.instance.kirimAmbilStok(requestBody)
                .enqueue(object : Callback<ApiResponse<AmbilStokRequest>> {
                    override fun onResponse(
                        call: Call<ApiResponse<AmbilStokRequest>>,
                        response: Response<ApiResponse<AmbilStokRequest>>
                    ) {
                        Log.d("API_AMBIL_STOK", "Request: $requestBody")
                        Log.d("API_AMBIL_STOK", "Response Code: ${response.code()}")
                        Log.d("API_AMBIL_STOK", "Response Body: ${response.body()}")
                        Log.d("API_AMBIL_STOK", "Response Message: ${response.message()}")

                        if (response.isSuccessful && response.body()?.status == true) {
                            Toast.makeText(context, "Berhasil mengirim ambil stok ke server", Toast.LENGTH_SHORT).show()
                            // Panggil callback untuk refresh data
                            onDataUpdated()
                        } else {
                            Toast.makeText(context, "Gagal mengirim data ke server", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<ApiResponse<AmbilStokRequest>>, t: Throwable) {
                        Log.e("API_AMBIL_STOK", "Gagal mengirim ambil stok: ${t.message}", t)
                        Toast.makeText(context, "Terjadi kesalahan jaringan: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })

            alertDialog.dismiss()
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

    private fun showMapsDialog(ambilStok: AmbilStok) {
        val dialogView: View = LayoutInflater.from(context).inflate(R.layout.alert_dialog, null)

        val alertDialog = AlertDialog.Builder(context, R.style.CustomAlertDialog)
            .setView(dialogView)
            .create()

        val tvDialogMessage = dialogView.findViewById<TextView>(R.id.tvDialogMessage)
        val tvDialogTitle = dialogView.findViewById<TextView>(R.id.tvDialogTitle)
        val btnTidak = dialogView.findViewById<Button>(R.id.btnTidak)
        val btnYa = dialogView.findViewById<Button>(R.id.btnYa)

        tvDialogTitle.text = "Konfirmasi Google Maps"
        tvDialogMessage.text = "Buka lokasi ${ambilStok.nama_usaha} di Google Maps?"

        btnTidak.setOnClickListener {
            alertDialog.dismiss()
        }

        btnYa.setOnClickListener {
            openGoogleMaps(ambilStok.lokasi, ambilStok.nama_usaha)
            alertDialog.dismiss()
        }

        alertDialog.show()
        val window = alertDialog.window
        window?.setLayout(
            (Resources.getSystem().displayMetrics.widthPixels * 0.90).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    private fun openGoogleMaps(lokasi: String, namaUsaha: String) {
        val location = lokasi.split(",")
        if (location.size != 2) {
            Toast.makeText(context, "Format koordinat tidak valid", Toast.LENGTH_SHORT).show()
            return
        }

        val latitude = location[0].toDoubleOrNull()
        val longitude = location[1].toDoubleOrNull()
        if (latitude == null || longitude == null) {
            Toast.makeText(context, "Koordinat tidak valid", Toast.LENGTH_SHORT).show()
            return
        }

        val uri = "geo:$latitude,$longitude?q=$latitude,$longitude($namaUsaha)"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        intent.setPackage("com.google.android.apps.maps")

        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            Toast.makeText(context, "Google Maps tidak ditemukan", Toast.LENGTH_SHORT).show()
        }
    }

    private fun logAmbilStokData(ambilStok: AmbilStok) {
        val idUser = getUserId()
        Log.d("AmbilStokAdapter", """
            Data Stok yang Diambil:
            - ID Pemasok: ${ambilStok.id_pemasok}
            - ID Pengelola: $idUser
            - ID Stok: ${ambilStok.id}
            - Jumlah Total Stok: ${ambilStok.total_berat}
        """.trimIndent())
    }

    private fun getUserId(): Int = prefs.getInt("id_user_detail", -1)

    private fun getAddressFromCoordinates(lokasi: String): String? {
        val location = lokasi.split(",")
        if (location.size != 2) return "Format koordinat tidak valid"

        val latitude = location[0].toDoubleOrNull()
        val longitude = location[1].toDoubleOrNull()
        if (latitude == null || longitude == null) return "Koordinat tidak valid"

        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            addresses?.firstOrNull()?.let {
                listOfNotNull(it.thoroughfare, it.subLocality, it.locality, it.adminArea, it.postalCode)
                    .joinToString(", ")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
