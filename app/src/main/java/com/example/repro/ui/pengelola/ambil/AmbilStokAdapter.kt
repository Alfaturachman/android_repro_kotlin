package com.example.repro.ui.pengelola.ambil

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.location.Geocoder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.repro.R
import com.example.repro.model.getAmbilStok
import java.text.DecimalFormat
import java.util.Locale

class AmbilStokAdapter(
    private val getAmbilStokList: List<getAmbilStok>,
    private val jarakPemasokList: List<Double>,
    private val context: Context,
    private val prefs: SharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AmbilStokViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.daftar_ambil_pemasok, parent, false)
        return AmbilStokViewHolder(view)
    }

    override fun onBindViewHolder(holder: AmbilStokViewHolder, position: Int) {
        val ambilStok = getAmbilStokList[position]
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
            showConfirmationDialog(ambilStok)
        }
    }

    override fun getItemCount(): Int = getAmbilStokList.size

    private fun showConfirmationDialog(ambilStok: getAmbilStok) {
        // Inflate layout custom dialog
        val dialogView: View = LayoutInflater.from(context).inflate(R.layout.alert_dialog, null)

        // Buat AlertDialog dengan custom view dan tema
        val alertDialog = AlertDialog.Builder(context, R.style.CustomAlertDialogTheme) // Terapkan tema di sini
            .setView(dialogView)
            .create()

        // Inisialisasi komponen di custom dialog
        val tvDialogMessage = dialogView.findViewById<TextView>(R.id.tvDialogMessage)
        val btnTidak = dialogView.findViewById<Button>(R.id.btnTidak)
        val btnYa = dialogView.findViewById<Button>(R.id.btnYa)

        // Set pesan dialog
        tvDialogMessage.text = "Apakah Anda yakin ingin mengambil stok dari ${ambilStok.nama_pemasok}?"

        // Handle tombol "Tidak"
        btnTidak.setOnClickListener {
            alertDialog.dismiss() // Tutup dialog
        }

        // Handle tombol "Ya"
        btnYa.setOnClickListener {
            Toast.makeText(context, "Berhasil mengambil stok dari ${ambilStok.nama_pemasok}", Toast.LENGTH_SHORT).show()
            logAmbilStokData(ambilStok)
            alertDialog.dismiss() // Tutup dialog
        }

        // Tampilkan dialog
        alertDialog.show()

        // Atur ukuran dialog agar tidak mepet dengan sisi layar
        val window = alertDialog.window
        window?.setLayout(
            (Resources.getSystem().displayMetrics.widthPixels * 0.90).toInt(),  // 90% dari lebar layar
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    private fun logAmbilStokData(ambilStok: getAmbilStok) {
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
