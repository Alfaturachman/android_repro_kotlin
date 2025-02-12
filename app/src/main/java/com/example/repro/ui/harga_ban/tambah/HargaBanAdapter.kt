package com.example.repro.ui.harga_ban

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.repro.R
import com.example.repro.model.postHargaBan
import com.example.repro.ui.harga_ban.edit.EditHargaActivity
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class HargaBanAdapter(private val hargaBanList: List<postHargaBan>) : RecyclerView.Adapter<HargaBanAdapter.HargaBanViewHolder>() {

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

        // Instance DecimalFormatSymbols
        val symbols = DecimalFormatSymbols().apply {
            groupingSeparator = '.' // Mengatur pemisah menjadi titik
        }

        // Buat DecimalFormat dengan simbol yang sudah diatur
        val formatter = DecimalFormat("#,###", symbols)
        val formattedHarga = formatter.format(hargaBan.harga)

        holder.tvJenisBan.text = hargaBan.jenis
        holder.tvHarga.text = "Rp $formattedHarga"
        holder.tvTanggal.text = hargaBan.ins_time

        holder.btnEditHargaBan.setOnClickListener {
            val intent = Intent(holder.itemView.context, EditHargaActivity::class.java)
            intent.putExtra("id", hargaBan.id)
            intent.putExtra("jenis", hargaBan.jenis)
            intent.putExtra("harga", hargaBan.harga.toFloat())
            holder.itemView.context.startActivity(intent)
            Log.d("HargaBanAdapter", "ID: ${hargaBan.id}, Jenis: ${hargaBan.jenis}, Harga: ${hargaBan.harga}")
        }
    }

    override fun getItemCount(): Int {
        return hargaBanList.size
    }
}