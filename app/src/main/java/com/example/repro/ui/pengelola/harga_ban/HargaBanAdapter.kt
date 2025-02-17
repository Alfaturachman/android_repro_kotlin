package com.example.repro.ui.pengelola.harga_ban

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.repro.R
import com.example.repro.model.postHargaBan
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class HargaBanAdapter(
    private val hargaBanList: List<postHargaBan>,
    private val onItemClick: (postHargaBan) -> Unit
) : RecyclerView.Adapter<HargaBanAdapter.HargaBanViewHolder>() {

    class HargaBanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvJenisBan: TextView = itemView.findViewById(R.id.tvJenisBan)
        val tvHarga: TextView = itemView.findViewById(R.id.tvHarga)
        val tvTanggal: TextView = itemView.findViewById(R.id.tvTanggal)
        val btnEditHargaBan: ImageView = itemView.findViewById(R.id.btnEditHargaBan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HargaBanViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.daftar_hargaban, parent, false)
        return HargaBanViewHolder(view)
    }

    override fun onBindViewHolder(holder: HargaBanViewHolder, position: Int) {
        val hargaBan = hargaBanList[position]

        // Format harga
        val symbols = DecimalFormatSymbols().apply {
            groupingSeparator = '.'
        }
        val formatter = DecimalFormat("#,###", symbols)
        val formattedHarga = formatter.format(hargaBan.harga.toDouble()) // Pastikan harga adalah numerik

        // Set data ke view
        holder.tvJenisBan.text = "Jenis Ban: ${hargaBan.jenis}" // Gunakan ${} untuk string template
        holder.tvHarga.text = "Rp $formattedHarga"
        holder.tvTanggal.text = "Tanggal: ${hargaBan.ins_time}" // Gunakan ${} untuk string template

        // Handle klik tombol edit
        holder.btnEditHargaBan.setOnClickListener {
            onItemClick(hargaBan) // Panggil callback
        }
    }

    override fun getItemCount(): Int {
        return hargaBanList.size
    }
}