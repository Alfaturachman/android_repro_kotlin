package com.example.repro.ui.harga_ban

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.repro.R
import com.example.repro.ui.harga_ban.tambah.postHargaBan
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class HargaBanAdapter(private val hargaBanList: List<postHargaBan>) : RecyclerView.Adapter<HargaBanAdapter.HargaBanViewHolder>() {

    class HargaBanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvJenisBan: TextView = itemView.findViewById(R.id.tvJenisBan)
        val tvHarga: TextView = itemView.findViewById(R.id.tvHarga)
        val tvTanggal: TextView = itemView.findViewById(R.id.tvTanggal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HargaBanViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.daftar_hargaban, parent, false)
        return HargaBanViewHolder(view)
    }

    override fun onBindViewHolder(holder: HargaBanViewHolder, position: Int) {
        val hargaBan = hargaBanList[position]

        // Buat instance DecimalFormatSymbols dan atur pemisah ribuan menjadi titik
        val symbols = DecimalFormatSymbols().apply {
            groupingSeparator = '.' // Mengatur pemisah ribuan menjadi titik
        }

        // Buat DecimalFormat dengan simbol yang sudah diatur
        val formatter = DecimalFormat("#,###", symbols)
        val formattedHarga = formatter.format(hargaBan.harga)

        holder.tvJenisBan.text = hargaBan.jenis
        holder.tvHarga.text = "Rp $formattedHarga"
        holder.tvTanggal.text = hargaBan.ins_time
    }

    override fun getItemCount(): Int {
        return hargaBanList.size
    }
}