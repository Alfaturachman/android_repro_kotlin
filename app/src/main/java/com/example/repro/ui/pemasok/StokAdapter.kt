package com.example.repro.ui.pemasok

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Resources
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
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.repro.R
import com.example.repro.api.ApiResponse
import com.example.repro.api.RetrofitClient
import com.example.repro.helpers.DateHelper
import com.example.repro.model.DeleteStok
import com.example.repro.model.PemasokStok
import com.example.repro.ui.pemasok.detail.DetailStokActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale

class StokAdapter(
    private val stokList: List<PemasokStok>,
    private val startForResult: ActivityResultLauncher<Intent>,
    private val onUserDeleted: () -> Unit // Callback untuk refresh data
) : RecyclerView.Adapter<StokAdapter.StokViewHolder>() {

    inner class StokViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvJenis: TextView = itemView.findViewById(R.id.tvJenis)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val tvTanggal: TextView = itemView.findViewById(R.id.tvTanggal)
        val tvTotalBerat: TextView = itemView.findViewById(R.id.tvTotalBerat)
        val tvHargaPerKg: TextView = itemView.findViewById(R.id.tvHargaPerKg)
        val tvTotalHarga: TextView = itemView.findViewById(R.id.tvTotalHarga)
        val btnDetailStok: ImageView = itemView.findViewById(R.id.btnDetailStok)
        val btnHapusStok: ImageView = itemView.findViewById(R.id.btnHapusStok)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StokViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.daftar_stok, parent, false)
        return StokViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: StokViewHolder, position: Int) {
        val stok = stokList[position]
        val context = holder.itemView.context
        val formatRupiah = NumberFormat.getNumberInstance(Locale("id", "ID"))

        holder.tvJenis.text = "Jenis Ban: ${stok.jenis}"
        holder.tvStatus.text = stok.status
        holder.tvTanggal.text = DateHelper.formatTanggal(stok.tanggal)
        holder.tvTotalBerat.text = "${stok.jumlah_stok} kg"
        holder.tvHargaPerKg.text = "Rp ${formatRupiah.format(stok.harga)}"
        holder.tvTotalHarga.text = "Rp ${formatRupiah.format(stok.total_harga)}"

        // Button detail
        holder.btnDetailStok.setOnClickListener {
            handleButtonEditClick(stok, context)
        }

        if (stok.status == "Sudah diambil") {
            holder.tvStatus.backgroundTintList = ContextCompat.getColorStateList(context, R.color.forest_green)
            holder.btnDetailStok.visibility = View.VISIBLE
            holder.btnHapusStok.visibility = View.GONE
        } else if (stok.status == "Belum diambil") {
            holder.tvStatus.backgroundTintList = ContextCompat.getColorStateList(context, R.color.amber_orange)

            // Button hapus
            holder.btnHapusStok.setOnClickListener {
                handleButtonHapus(stok, context)
            }
        }
    }

    override fun getItemCount(): Int {
        return stokList.size
    }

    private fun handleButtonEditClick(stok: PemasokStok, context: Context) {
        Log.d("DetailStokActivity", "Sending data: id_user=${stok.id}, tanggal=${stok.tanggal}, jenis_ban=${stok.jenis}, " +
                "jumlah_stok=${stok.jumlah_stok}, harga_ban=${stok.harga}, total_harga_ban=${stok.total_harga}, lokasi=${stok.lokasi}")

        val intent = Intent(context, DetailStokActivity::class.java).apply {
            putExtra("id_user", stok.id)
            putExtra("tanggal", stok.tanggal)
            putExtra("jenis_ban", stok.jenis)
            putExtra("status", stok.status)
            putExtra("jumlah_stok", stok.jumlah_stok.toFloat())
            putExtra("harga_ban", stok.harga.toFloat())
            putExtra("total_harga_ban", stok.total_harga.toFloat())
            putExtra("lokasi", stok.lokasi)
            putExtra("keterangan", stok.keterangan)
            putExtra("foto", stok.foto)
        }
        startForResult.launch(intent)
    }

    // Button Hapus
    @SuppressLint("SetTextI18n")
    private fun handleButtonHapus(stok: PemasokStok, context: Context) {
        // Inflate layout custom dialog
        val dialogView: View = LayoutInflater.from(context).inflate(R.layout.alert_dialog, null)

        // AlertDialog dengan custom view dan tema
        val alertDialog = AlertDialog.Builder(context, R.style.CustomAlertDialog)
            .setView(dialogView)
            .create()

        // Inisialisasi custom dialog
        val tvDialogTitle = dialogView.findViewById<TextView>(R.id.tvDialogTitle)
        val tvDialogMessage = dialogView.findViewById<TextView>(R.id.tvDialogMessage)
        val btnBatal = dialogView.findViewById<Button>(R.id.btnTidak)
        val btnHapus = dialogView.findViewById<Button>(R.id.btnYa)

        // Set judul dan pesan dialog
        tvDialogTitle.text = "Konfirmasi Hapus"
        tvDialogMessage.text = "Apakah Anda yakin ingin menghapus ${stok.jenis} - Rp ${stok.total_harga}?"

        // Button Batal
        btnBatal.setOnClickListener {
            alertDialog.dismiss() // Tutup dialog
        }

        // Button Hapus
        btnHapus.setOnClickListener {
            // Panggil API untuk menghapus stok
            RetrofitClient.instance.deleteStok(DeleteStok(stok.id)).enqueue(object :
                Callback<ApiResponse<DeleteStok>> {
                override fun onResponse(call: Call<ApiResponse<DeleteStok>>, response: Response<ApiResponse<DeleteStok>>) {
                    if (response.isSuccessful && response.body()?.status == true) {
                        Toast.makeText(context, "Data berhasil dihapus!", Toast.LENGTH_SHORT).show()
                        onUserDeleted() // Panggil callback untuk refresh data
                    } else {
                        Log.e("API_ERROR", "Response Error: ${response.errorBody()?.string()}")
                        Toast.makeText(context, "Gagal menghapus data!", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse<DeleteStok>>, t: Throwable) {
                    Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
            alertDialog.dismiss() // Tutup dialog setelah menghapus
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
}