package com.example.repro.ui.pengelola.riwayat_detail

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.repro.R
import com.example.repro.helper.DateHelper
import com.example.repro.model.RiwayatPemasokResponse
import com.example.repro.ui.pengelola.riwayat_detail.olah.OlahActivity

class RiwayatDetailAdapter(
    private val riwayatList: MutableList<RiwayatPemasokResponse>
) : RecyclerView.Adapter<RiwayatDetailAdapter.RiwayatViewHolder>() {

    class RiwayatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTanggalDiambil: TextView = itemView.findViewById(R.id.tvTanggalDiambil)
        val tvJumlahStokAmbil: TextView = itemView.findViewById(R.id.tvJumlahStok)
        val tvTanggalDiaolah: TextView = itemView.findViewById(R.id.tvTanggalDiaolah)
        val tvJumlahCrumbRubber: TextView = itemView.findViewById(R.id.tvJumlahCrumbRubber)
        val divider: View = itemView.findViewById(R.id.divider)
        val lineTanggalOlah: LinearLayout = itemView.findViewById(R.id.lineTanggalOlah)
        val lineJumlahOlah: LinearLayout = itemView.findViewById(R.id.lineJumlahOlah)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val btnOlah: ImageView = itemView.findViewById(R.id.btnOlah)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RiwayatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.daftar_histori_pemasok, parent, false)
        return RiwayatViewHolder(view)
    }

    override fun onBindViewHolder(holder: RiwayatViewHolder, position: Int) {
        val riwayat = riwayatList[position]
        val context = holder.itemView.context

        holder.tvTanggalDiambil.text = DateHelper.formatTanggal(riwayat.tanggalAmbil)
        holder.tvJumlahStokAmbil.text = "${riwayat.jumlahStokAmbil} kg"

        holder.btnOlah.setOnClickListener {
            val intent = Intent(context as RiwayatDetailActivity, OlahActivity::class.java).apply {
                putExtra("id_ambil", riwayat.idAmbil)
                putExtra("jumlah_stok", riwayat.jumlahStokAmbil.toDouble())
                putExtra("tanggal_ambil", riwayat.tanggalAmbil)
                putExtra("jenis", riwayat.jenis)
            }
            (context as RiwayatDetailActivity).startActivityForResult(intent, 1001)
        }

        if (riwayat.idOlah != null) {
            holder.tvStatus.text = "Sudah diambil"
            holder.tvStatus.backgroundTintList = ContextCompat.getColorStateList(context, R.color.lush_green)

            holder.btnOlah.visibility = View.GONE
            holder.lineJumlahOlah.visibility = View.VISIBLE
            holder.lineTanggalOlah.visibility = View.VISIBLE
            holder.divider.visibility = View.VISIBLE
            holder.tvTanggalDiaolah.text = DateHelper.formatTanggal(riwayat.tanggalOlah)
            holder.tvJumlahCrumbRubber.text = "${riwayat.jumlahMentahOlah} kg"
        } else {
            holder.tvStatus.text = "Belum diambil"
            holder.tvStatus.backgroundTintList = ContextCompat.getColorStateList(context, R.color.amber_orange)

            holder.divider.visibility = View.GONE
            holder.lineTanggalOlah.visibility = View.GONE
            holder.lineJumlahOlah.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return riwayatList.size
    }
}
