package com.example.repro.api

import com.example.repro.ui.pengelola.Ambil
import com.example.repro.ui.pengelola.Pemasok
import com.example.repro.ui.pengelola.PemasokResponse
import com.example.repro.ui.pemasok.StatusStok
import com.example.repro.api.ApiResponse
import com.example.repro.ui.home.Stok
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST


interface ApiService {
    @POST("login.php")
    fun loginUser(@Body body: RequestBody): Call<ResponseBody>

    @POST("post_pemasok.php")
    fun registerUser(@Body body: RequestBody): Call<ResponseBody>

    @GET("get_pemasok.php")
    fun getPemasokList(): Call<ApiResponse<List<Pemasok>>>

    @Headers("Content-Type: application/json")
    @POST("get_riwayat_pemasok.php")
    fun getPemasokData(@Body requestBody: HashMap<String, Int>): Call<PemasokResponse>

    @Headers("Content-Type: application/json")
    @POST("get_status_stok.php")
    fun getStokByPemasokId(@Body requestBody: HashMap<String, Int>): Call<ApiResponse<List<StatusStok>>>

    @Headers("Content-Type: application/json")
    @POST("get_total_pemasok.php")
    fun getStokData(@Body requestBody: HashMap<String, Int>): Call<ApiResponse<Stok>>

    @GET("get_ambil.php")
    fun getAmbil(): Call<List<Ambil>>
}
