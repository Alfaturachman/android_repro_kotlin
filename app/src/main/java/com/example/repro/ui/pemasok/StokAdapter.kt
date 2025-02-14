package com.example.repro.ui.pemasok

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.repro.R
import com.example.repro.helper.DateHelper
import com.example.repro.model.getStokByPemasokId
import java.text.NumberFormat
import java.util.Locale

class StokAdapter(private val stokList: List<getStokByPemasokId>) : RecyclerView.Adapter<StokAdapter.StokViewHolder>() {

    inner class StokViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvJenis: TextView = itemView.findViewById(R.id.tvJenis)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val btnEditStok: ImageView = itemView.findViewById(R.id.btnEditStok)
        val btnHapusStok: ImageView = itemView.findViewById(R.id.btnHapusStok)
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
        val context = holder.itemView.context
        val formatRupiah = NumberFormat.getNumberInstance(Locale("id", "ID"))

        holder.tvJenis.text = "Jenis Ban: ${stok.jenis}"
        holder.tvStatus.text = "${stok.status}"
        holder.tvTotalBerat.text = "${stok.jumlah_stok} kg"
        holder.tvHargaPerKg.text = "Rp ${formatRupiah.format(stok.harga)}"
        holder.tvTotalHarga.text = "Rp ${formatRupiah.format(stok.total_harga)}"

        if (stok.status == "Sudah diambil") {
            holder.tvStatus.backgroundTintList = ContextCompat.getColorStateList(context, R.color.forest_green)
            holder.btnEditStok.visibility = View.GONE
            holder.btnHapusStok.visibility = View.GONE
        } else if (stok.status == "Belum diambil") {
            holder.tvStatus.backgroundTintList = ContextCompat.getColorStateList(context, R.color.amber_orange)
        }
    }

    override fun getItemCount(): Int {
        return stokList.size
    }
}