package com.example.repro.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://192.168.1.21:80/repro_api/"

    private val client = OkHttpClient.Builder().build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    val instance: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
