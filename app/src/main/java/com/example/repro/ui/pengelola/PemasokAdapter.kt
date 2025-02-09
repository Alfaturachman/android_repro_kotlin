package com.example.repro.ui.pengelola

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.repro.R
import com.example.repro.modal.getPemasok
import com.example.repro.ui.pengelola.riwayat_detail.RiwayatDetailActivity

class PemasokAdapter(
    private val context: Context,
    private val getPemasokList: List<getPemasok>
) : RecyclerView.Adapter<PemasokAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.daftar_pemasok, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pemasok = getPemasokList[position]
        holder.tvNamaPemasok.text = pemasok.nama
        holder.tvNamaUsaha.text = pemasok.namaUsaha
        holder.tvNoHp.text = pemasok.noHp
        holder.tvAlamat.text = pemasok.alamat
    }

    override fun getItemCount(): Int = getPemasokList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNamaPemasok: TextView = itemView.findViewById(R.id.tv_nama_pemasok)
        val tvNamaUsaha: TextView = itemView.findViewById(R.id.tv_nama_usaha)
        val tvNoHp: TextView = itemView.findViewById(R.id.tv_no_hp)
        val tvAlamat: TextView = itemView.findViewById(R.id.tv_alamat)
        private val btnAmbil: ImageView = itemView.findViewById(R.id.btn_detail)

        init {
            btnAmbil.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val pemasok = getPemasokList[position]
                    handleAmbilClick(pemasok)
                }
            }
        }

        private fun handleAmbilClick(getPemasok: getPemasok) {
            val context = itemView.context
            val intent = Intent(context, RiwayatDetailActivity::class.java).apply {
                putExtra("PEMASOK_ID", getPemasok.id) // Mengirim ID pemasok
            }
            context.startActivity(intent)
        }
    }
}