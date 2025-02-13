package com.example.repro.ui.pengelola.ambil

import android.content.Context
import android.location.Geocoder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.repro.R
import com.example.repro.model.getAmbilStok
import java.util.Locale

class AmbilStokAdapter(
    private val getAmbilStokList: List<getAmbilStok>,
    private val jarakPemasokList: List<Double>, // Tambahkan ini
    private val context: Context
) : RecyclerView.Adapter<AmbilStokAdapter.AmbilStokViewHolder>() {

    class AmbilStokViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNamaUsaha: TextView = itemView.findViewById(R.id.tvNamaUsaha)
        val tvNamaPemilik: TextView = itemView.findViewById(R.id.tvNamaPemilik)
        val tvAlamat: TextView = itemView.findViewById(R.id.tvAlamat)
        val tvJenisBan: TextView = itemView.findViewById(R.id.tvJenisBan)
        val tvTotalBerat: TextView = itemView.findViewById(R.id.tvTotalBerat)
        val tvTotalHarga: TextView = itemView.findViewById(R.id.tvTotalHarga)
        val tvJarak: TextView = itemView.findViewById(R.id.tvJarak) // Tambahkan ini
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AmbilStokViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.daftar_ambil_pemasok, parent, false)
        return AmbilStokViewHolder(view)
    }

    override fun onBindViewHolder(holder: AmbilStokViewHolder, position: Int) {
        val ambilStok = getAmbilStokList[position]
        val jarak = jarakPemasokList[position] // Ambil jarak dari list

        // Format harga dengan DecimalFormat
        val formatter = java.text.DecimalFormat("#,###")
        val formattedHarga = formatter.format(ambilStok.harga.toDouble())
        val formattedTotalHarga = formatter.format(ambilStok.total_harga.toDouble())

        holder.tvNamaUsaha.text = "${ambilStok.nama_usaha}"
        holder.tvNamaPemilik.text = "Pemilik ${ambilStok.nama_pemasok}"
        holder.tvJenisBan.text = ambilStok.jenis
        holder.tvTotalBerat.text = "${ambilStok.total_berat} kg"
        holder.tvTotalHarga.text = "Rp $formattedTotalHarga"
        holder.tvJarak.text = "Jarak: ${"%.2f".format(jarak)} km" // Set jarak ke TextView

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
        return getAmbilStokList.size
    }

    private fun getAddressFromCoordinates(latitude: Double, longitude: Double): String? {
        val geocoder = Geocoder(context, Locale.getDefault())
        return try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses?.isNotEmpty() == true) {
                val address = addresses[0]
                val addressParts = listOfNotNull(
                    address.thoroughfare,
                    address.subLocality,
                    address.locality,
                    address.adminArea,
                    address.postalCode
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