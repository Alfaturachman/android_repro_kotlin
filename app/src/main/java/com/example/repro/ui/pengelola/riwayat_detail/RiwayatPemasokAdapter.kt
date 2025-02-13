package com.example.repro.ui.pengelola.riwayat_detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.repro.R
import com.example.repro.model.RiwayatPemasokResponse

class RiwayatPemasokAdapter(
    private val riwayatList: MutableList<RiwayatPemasokResponse>
) : RecyclerView.Adapter<RiwayatPemasokAdapter.RiwayatViewHolder>() {

    class RiwayatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTanggalDiambil: TextView = itemView.findViewById(R.id.tvTanggalDiambil)
        val tvJumlahStokAmbil: TextView = itemView.findViewById(R.id.tvJumlahStok)
        val tvTanggalDiaolah: TextView = itemView.findViewById(R.id.tvTanggalDiaolah)
        val tvJumlahCrumbRubber: TextView = itemView.findViewById(R.id.tvJumlahCrumbRubber)
        val divider: View = itemView.findViewById(R.id.divider)
        val lineTanggalOlah: LinearLayout = itemView.findViewById(R.id.lineTanggalOlah)
        val lineJumlahOlah: LinearLayout = itemView.findViewById(R.id.lineJumlahOlah)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RiwayatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.daftar_histori_pemasok, parent, false)
        return RiwayatViewHolder(view)
    }

    override fun onBindViewHolder(holder: RiwayatViewHolder, position: Int) {
        val riwayat = riwayatList[position]

        holder.tvTanggalDiambil.text = riwayat.tanggalAmbil
        holder.tvJumlahStokAmbil.text = riwayat.jumlahStokAmbil.toString()

        if (riwayat.idOlah != null) {
            holder.lineJumlahOlah.visibility = View.VISIBLE
            holder.lineTanggalOlah.visibility = View.VISIBLE
            holder.divider.visibility = View.VISIBLE
            holder.tvTanggalDiaolah.text = riwayat.tanggalOlah
            holder.tvJumlahCrumbRubber.text = riwayat.jumlahMentahOlah.toString()
        } else {
            holder.divider.visibility = View.GONE
            holder.lineTanggalOlah.visibility = View.GONE
            holder.lineJumlahOlah.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return riwayatList.size
    }
}
