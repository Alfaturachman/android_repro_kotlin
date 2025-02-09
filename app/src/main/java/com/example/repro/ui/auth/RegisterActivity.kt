package com.example.repro.ui.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.repro.R
import com.example.repro.api.RetrofitClient
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "RegisterActivity"
    }

    private lateinit var etNamaPemilik: EditText
    private lateinit var etNamaUsaha: EditText
    private lateinit var etNomorHp: EditText
    private lateinit var etAlamat: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etKonfirmasiPassword: EditText
    private lateinit var registerButton: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Inisialisasi EditText
        etNamaPemilik = findViewById(R.id.etNamaPemilik)
        etNamaUsaha = findViewById(R.id.etNamaUsaha)
        etNomorHp = findViewById(R.id.etNomorHP)
        etAlamat = findViewById(R.id.etAlamat)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etKonfirmasiPassword = findViewById(R.id.etKonfirmasiPassword)

        // Inisialisasi tombol
        registerButton = findViewById(R.id.btn_register)

        registerButton.setOnClickListener {
            val namaPemilik = etNamaPemilik.text.toString().trim()
            val namaUsaha = etNamaUsaha.text.toString().trim()
            val noHp = etNomorHp.text.toString().trim()
            val alamat = etAlamat.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etKonfirmasiPassword.text.toString().trim()

            // Validasi input
            when {
                namaPemilik.isEmpty() || namaUsaha.isEmpty() || noHp.isEmpty() || alamat.isEmpty() || email.isEmpty() || password.isEmpty() -> {
                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
                password != confirmPassword -> {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    // Kirim data ke server
                    registerUser(namaPemilik, namaUsaha, noHp, alamat, email, password)
                }
            }
        }
    }

    private fun registerUser(namaPemilik: String, namaUsaha: String, noHp: String, alamat: String, email: String, password: String) {
        val apiService = RetrofitClient.instance

        Log.d(TAG, "Data sebelum dikirim: namaPemilik=$namaPemilik, namaUsaha=$namaUsaha, noHp=$noHp, alamat=$alamat, email=$email, password=$password")

        // Buat object JSON
        val requestData = mapOf(
            "email" to email,
            "password" to password,
            "nama" to namaPemilik,
            "nama_usaha" to namaUsaha,
            "no_hp" to noHp,
            "alamat" to alamat
        )

        // Buat RequestBody dari Map
        val body = RequestBody.create(
            "application/json; charset=utf-8".toMediaType(),
            JSONObject(requestData).toString()
        )

        // Kirim ke server
        val call = apiService.registerUser(body)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    try {
                        val result = response.body()?.string() ?: "{}"
                        Log.d(TAG, "Registration successful: $result")

                        val jsonResponse = JSONObject(result)
                        val status = jsonResponse.optBoolean("status", false)
                        val message = jsonResponse.optString("message", "Unknown error")

                        if (status && message.contains("Data pemasok berhasil ditambahkan!")) {
                            Toast.makeText(this@RegisterActivity, "Register berhasil", Toast.LENGTH_SHORT).show()

                            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@RegisterActivity, "Registration failed: $message", Toast.LENGTH_SHORT).show()
                            Log.e(TAG, "Registration failed: $message")
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@RegisterActivity, "Error parsing response: ${e.message}", Toast.LENGTH_SHORT).show()
                        Log.e(TAG, "Error parsing response: ", e)
                    }
                } else {
                    Toast.makeText(this@RegisterActivity, "Server error: ${response.message()}", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "Server response error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@RegisterActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Network error: ", t)
            }
        })
    }
}
