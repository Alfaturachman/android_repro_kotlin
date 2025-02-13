package com.example.repro.ui.auth

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.repro.MainActivity
import com.example.repro.R
import com.example.repro.api.RetrofitClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var pagesRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        usernameEditText = findViewById(R.id.etEmail)
        passwordEditText = findViewById(R.id.etPassword)
        loginButton = findViewById(R.id.btn_login)
        pagesRegister = findViewById(R.id.btn_halaman_register)

        val sharedPreferences: SharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)
        val userId = sharedPreferences.getInt("id_user", -1)

        if (userId != -1) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (TextUtils.isEmpty(username)) {
                usernameEditText.error = "Username is required"
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(password)) {
                passwordEditText.error = "Password is required"
                return@setOnClickListener
            }

            loginUser(username, password)
        }

        pagesRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun loginUser(email: String, password: String) {
        val apiService = RetrofitClient.instance

        val requestData = JSONObject()
        requestData.put("email", email)
        requestData.put("password", password)

        val body = RequestBody.create("application/json".toMediaTypeOrNull(), requestData.toString())

        val call = apiService.loginUser(body)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    try {
                        val result = response.body()?.string()
                        Log.d("LoginActivity", "Response: $result")  // Log the entire response

                        val jsonResponse = JSONObject(result)

                        // Check if the response has the "status" and proceed with parsing
                        if (jsonResponse.getBoolean("status")) {
                            val userDetails = jsonResponse.getJSONObject("user_details")
                            val userDetailId = userDetails.getString("id").toInt()
                            val userId = jsonResponse.getString("id_user").toInt()
                            val userEmail = jsonResponse.getString("email")
                            val userLevel = jsonResponse.getString("level")
                            val userNama = userDetails.getString("nama")

                            val sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putInt("id_user_detail", userDetailId)
                            editor.putInt("id_user", userId)
                            editor.putString("email", userEmail)
                            editor.putString("level", userLevel)
                            editor.putString("nama", userNama)
                            editor.apply()

                            Toast.makeText(this@LoginActivity, "Login Berhasil", Toast.LENGTH_SHORT).show()

                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this@LoginActivity, "Login Gagal: ${jsonResponse.getString("error")}", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: JSONException) {
                        Log.e("LoginActivity", "JSON Error: ${e.message}")
                        Toast.makeText(this@LoginActivity, "Error Parsing Response", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("LoginActivity", "Response Error: ${response.code()} - ${response.message()}")
                    Toast.makeText(this@LoginActivity, "Login Gagal", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("LoginActivity", "Request Error: ${t.message}")
                Toast.makeText(this@LoginActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
