package com.example.repro.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private val ip: String = "192.168.1.5"

    private val BASE_URL = "http://$ip/supplychain_rubber/api/"

    internal val BASE_URL_UPLOADS = "http://$ip/supplychain_rubber/uploads/"

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