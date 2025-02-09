package com.example.repro.ui.pengelola.ambil

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.repro.R

class AmbilStokAdapter(private val ambilStokList: List<AmbilStok>) : RecyclerView.Adapter<AmbilStokAdapter.AmbilStokViewHolder>() {

    class AmbilStokViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNamaPemasok: TextView = itemView.findViewById(R.id.tvNamaPemasok)
        val tvAlamat: TextView = itemView.findViewById(R.id.tvAlamat)
        val tvJenisBan: TextView = itemView.findViewById(R.id.tvJenisBan)
        val tvTotalBerat: TextView = itemView.findViewById(R.id.tvTotalBerat)
        val tvTotalHarga: TextView = itemView.findViewById(R.id.tvTotalHarga)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AmbilStokViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.daftar_ambil_pemasok, parent, false)
        return AmbilStokViewHolder(view)
    }

    override fun onBindViewHolder(holder: AmbilStokViewHolder, position: Int) {
        val ambilStok = ambilStokList[position]

        // Format harga dengan DecimalFormat
        val formatter = java.text.DecimalFormat("#,###")
        val formattedHarga = formatter.format(ambilStok.harga.toDouble())
        val formattedTotalHarga = formatter.format(ambilStok.total_harga.toDouble())

        holder.tvNamaPemasok.text = "Bengkel ${ambilStok.id_pemasok}" // Anda bisa mengganti ini dengan nama pemasok yang sebenarnya
        holder.tvAlamat.text = ambilStok.lokasi
        holder.tvJenisBan.text = ambilStok.jenis
        holder.tvTotalBerat.text = "${ambilStok.total_berat} kg"
        holder.tvTotalHarga.text = "Rp $formattedTotalHarga"
    }

    override fun getItemCount(): Int {
        return ambilStokList.size
    }
}