package com.example.repro.ui.auth

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.repro.MainActivity
import com.example.repro.R
import com.example.repro.api.ApiService
import com.example.repro.api.RetrofitClient
import org.json.JSONException
import org.json.JSONObject
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

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

        // Check SharedPreferences
        val sharedPreferences: SharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)
        val userId = sharedPreferences.getString("id_user", null)

        if (userId != null) {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Handle login button click
        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            // Validate inputs
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

        // Handle register button click
        pagesRegister.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser(email: String, password: String) {
        val apiInterface = RetrofitClient.retrofitInstance.create(ApiService::class.java)

        // Buat object JSON
        val requestData = mapOf(
            "email" to email,
            "password" to password
        )

        // Buat RequestBody dari Map
        val body = RequestBody.create(
            "application/json; charset=utf-8".toMediaType(),
            JSONObject(requestData).toString()
        )

        // Send login request to the server
        val call = apiInterface.loginUser(body)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    try {
                        // Read response body as string
                        val result = response.body()?.string()

                        if (result != null && result.contains("Login successful")) {
                            // Login successful
                            Log.d("LoginActivity", "Login successful: $result")
                            Toast.makeText(this@LoginActivity, "Login berhasil", Toast.LENGTH_SHORT).show()

                            // Parse response to get userId, email, level, and name from user_details
                            val jsonResponse = JSONObject(result)
                            val userEmail = jsonResponse.getString("email")
                            val userId = jsonResponse.getString("id_user")
                            val userLevel = jsonResponse.getString("level")

                            // Ambil data dari objek user_details
                            val userDetails = jsonResponse.getJSONObject("user_details")
                            val userNama = userDetails.getString("nama")

                            Log.d("SharedPreferences", "User ID: $userId")
                            Log.d("SharedPreferences", "User Email: $userEmail")
                            Log.d("SharedPreferences", "User Level: $userLevel")
                            Log.d("SharedPreferences", "User Nama: $userNama")

                            // Save user data in SharedPreferences
                            val sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString("id_user", userId)
                            editor.putString("email", userEmail)
                            editor.putString("level", userLevel)
                            editor.putString("nama", userNama)
                            editor.apply()

                            // Redirect to MainActivity
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            // Login failed
                            Log.e("LoginActivity", "Login failed: Invalid email or password. Response: $result")
                            Toast.makeText(this@LoginActivity, "Invalid username or password", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: JSONException) {
                        Log.e("LoginActivity", "Error parsing response: " + e.message, e)
                        Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    } catch (e: IOException) {
                        Log.e("LoginActivity", "Error reading response body: " + e.message, e)
                        Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Server response error
                    Log.e("LoginActivity", "Login failed: Response code " + response.code() + ", message: " + response.message())
                    Toast.makeText(this@LoginActivity, "Login failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // Connection or server error
                Log.e("LoginActivity", "Login error: " + t.message, t)
                Toast.makeText(this@LoginActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
