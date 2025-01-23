package com.example.repro.ui.pengelola

import android.content.Context
import android.widget.Toast
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.repro.R
import com.example.repro.ui.pengelola.riwayat_detail.RiwayatDetailFragment

class PemasokAdapter(
    private val context: Context,
    private val pemasokList: List<Pemasok>
) : RecyclerView.Adapter<PemasokAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.daftar_pemasok, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pemasok = pemasokList[position]
        holder.tvNamaPemasok.text = pemasok.nama
        holder.tvNamaUsaha.text = pemasok.namaUsaha
        holder.tvNoHp.text = pemasok.noHp
        holder.tvAlamat.text = pemasok.alamat
    }

    override fun getItemCount(): Int = pemasokList.size

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
                    val pemasok = pemasokList[position]
                    handleAmbilClick(pemasok)
                }
            }
        }

        private fun handleAmbilClick(pemasok: Pemasok) {
            val context = itemView.context

            if (context is AppCompatActivity) {
                val activity = context
                val fragment = RiwayatDetailFragment.newInstance(pemasok.id)

                activity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment) // Ganti dengan ID container fragment Anda
                    .addToBackStack(null) // Opsional untuk menambah fragment ke back stack
                    .commit()
            } else {
                Toast.makeText(context, "Activity is not an instance of AppCompatActivity", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
