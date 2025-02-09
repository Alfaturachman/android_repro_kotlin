package com.example.repro.api

import com.example.repro.ui.harga_ban.tambah.getHargaBan
import com.example.repro.ui.harga_ban.tambah.postHargaBan
import com.example.repro.ui.pengelola.Ambil
import com.example.repro.ui.pengelola.Pemasok
import com.example.repro.ui.pengelola.PemasokResponse
import com.example.repro.ui.pemasok.StatusStok
import com.example.repro.ui.home.TotalStokPemasok
import com.example.repro.ui.pengelola.ambil.AmbilStok
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
    fun getStokData(@Body requestBody: HashMap<String, Int>): Call<ApiResponse<TotalStokPemasok>>

    @Headers("Content-Type: application/json")
    @POST("post_harga_ban.php")
    fun tambahHargaBan(@Body getHargaBan: getHargaBan): Call<ApiResponse<getHargaBan>>

    @GET("get_harga_ban.php")
    fun getHargaBan(): Call<ApiResponse<List<postHargaBan>>>

    @GET("get_status_stok_belum_diambil.php")
    fun getAmbilStok(): Call<ApiResponse<List<AmbilStok>>>

    @GET("get_ambil.php")
    fun getAmbil(): Call<List<Ambil>>
}
