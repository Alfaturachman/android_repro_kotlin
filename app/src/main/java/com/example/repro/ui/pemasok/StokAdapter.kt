package com.example.repro.ui.pemasok

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.repro.R
import com.example.repro.modal.getStokByPemasokId

class StokAdapter(private val stokList: List<getStokByPemasokId>) : RecyclerView.Adapter<StokAdapter.StokViewHolder>() {

    inner class StokViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvJenis: TextView = itemView.findViewById(R.id.tvJenis)
        val tvTotalBerat: TextView = itemView.findViewById(R.id.tvTotalBerat)
        val tvHargaPerKg: TextView = itemView.findViewById(R.id.tvHargaPerKg)
        val tvTotalHarga: TextView = itemView.findViewById(R.id.tvTotalHarga)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StokViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.daftar_stok, parent, false)
        return StokViewHolder(view)
    }

    override fun onBindViewHolder(holder: StokViewHolder, position: Int) {
        val stok = stokList[position]
        holder.tvJenis.text = stok.jenis
        holder.tvTotalBerat.text = "Total Berat: ${stok.jumlah_stok} kg"
        holder.tvHargaPerKg.text = "Harga per kg: Rp${stok.harga}"
        holder.tvTotalHarga.text = "Total Harga: Rp${stok.total_harga}"
    }

    override fun getItemCount(): Int {
        return stokList.size
    }
}