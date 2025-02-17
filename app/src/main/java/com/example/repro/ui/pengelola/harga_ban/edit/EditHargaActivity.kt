package com.example.repro.ui.pengelola.harga_ban.edit

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.repro.R
import com.example.repro.api.ApiResponse
import com.example.repro.api.RetrofitClient
import com.example.repro.model.UpdateHargaBanRequest
import java.text.NumberFormat
import java.util.Locale

class EditHargaActivity : AppCompatActivity() {
    private lateinit var etHarga: EditText
    private lateinit var etJenisKendaraan: EditText
    private var idHargaBan: Int = 0

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_harga)

        etHarga = findViewById(R.id.etHarga)
        etJenisKendaraan = findViewById(R.id.etJenisKendaraan)
        val btnUpdate = findViewById<Button>(R.id.btnSimpanStok)

        idHargaBan = intent.getIntExtra("id", 0)
        val jenisBan = intent.getStringExtra("jenis")
        val hargaBan = intent.getFloatExtra("harga", -1f).toDouble()

        Log.d("EditHargaActivity", "ID: $idHargaBan, Jenis: $jenisBan, Harga: $hargaBan")

        etHarga.setText(formatRupiah(hargaBan))
        etJenisKendaraan.setText(jenisBan ?: "")

        // Menambahkan TextWatcher untuk memantau perubahan pada etHarga
        etHarga.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                etHarga.removeTextChangedListener(this)

                // Menghapus "Rp " dan tanda titik dari teks
                val cleanString = s.toString().replace("Rp ", "").replace(".", "")

                // Mengubah teks menjadi double
                val parsed = cleanString.toDoubleOrNull() ?: 0.0

                // Memformat ulang teks dengan format Rupiah
                val formatted = formatRupiah(parsed)

                // Menetapkan teks yang sudah diformat kembali ke EditText
                etHarga.setText(formatted)
                etHarga.setSelection(formatted.length)

                etHarga.addTextChangedListener(this)
            }
        })

        btnUpdate.setOnClickListener {
            val hargaBaru = etHarga.text.toString()
                .replace("Rp ", "")
                .replace(".", "")
                .toDouble().toInt()

            updateHargaBan(idHargaBan, hargaBaru)
        }

        // Button Kembali
        val btnKembali: ImageButton = findViewById(R.id.btnKembali)
        btnKembali.setOnClickListener {
            finish()
        }
    }

    private fun updateHargaBan(id: Int, harga: Int) {
        val request = UpdateHargaBanRequest(id, harga)

        RetrofitClient.instance.updateHargaBan(request).enqueue(object : Callback<ApiResponse<UpdateHargaBanRequest>> {
            override fun onResponse(call: Call<ApiResponse<UpdateHargaBanRequest>>, response: Response<ApiResponse<UpdateHargaBanRequest>>) {
                if (response.isSuccessful && response.body()?.status == true) {
                    Toast.makeText(this@EditHargaActivity, "Harga berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                } else {
                    Toast.makeText(this@EditHargaActivity, "Gagal memperbarui harga", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<UpdateHargaBanRequest>>, t: Throwable) {
                Log.d(TAG, "Error: ${t.message}")
                Toast.makeText(this@EditHargaActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun formatRupiah(amount: Double): String {
        val format = NumberFormat.getInstance(Locale("id", "ID"))
        return "Rp " + format.format(amount)
    }
}