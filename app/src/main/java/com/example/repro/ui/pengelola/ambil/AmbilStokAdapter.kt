package com.example.repro.ui.pengelola.ambil

import android.content.Context
import android.location.Geocoder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.repro.R
import java.util.Locale

class AmbilStokAdapter(
    private val ambilStokList: List<AmbilStok>,
    private val context: Context
) : RecyclerView.Adapter<AmbilStokAdapter.AmbilStokViewHolder>() {

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

        holder.tvNamaPemasok.text = "Bengkel ${ambilStok.id_pemasok}"
        holder.tvJenisBan.text = ambilStok.jenis
        holder.tvTotalBerat.text = "${ambilStok.total_berat} kg"
        holder.tvTotalHarga.text = "Rp $formattedTotalHarga"

        // Ubah koordinat menjadi alamat
        val location = ambilStok.lokasi.split(",")
        if (location.size == 2) {
            val latitude = location[0].toDoubleOrNull()
            val longitude = location[1].toDoubleOrNull()
            if (latitude != null && longitude != null) {
                val address = getAddressFromCoordinates(latitude, longitude)
                holder.tvAlamat.text = address ?: "Alamat tidak ditemukan"
            } else {
                holder.tvAlamat.text = "Koordinat tidak valid"
            }
        } else {
            holder.tvAlamat.text = "Format koordinat tidak valid"
        }
    }

    override fun getItemCount(): Int {
        return ambilStokList.size
    }

    private fun getAddressFromCoordinates(latitude: Double, longitude: Double): String? {
        val geocoder = Geocoder(context, Locale.getDefault())
        return try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses?.isNotEmpty() == true) {
                val address = addresses[0]
                val addressParts = listOfNotNull(
                    address.thoroughfare, // Nama jalan
                    address.subLocality, // Kelurahan
                    address.locality, // Kecamatan
                    address.adminArea, // Kota/Kabupaten
                    address.postalCode // Kode pos
                )
                addressParts.joinToString(", ")
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}