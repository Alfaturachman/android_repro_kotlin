package com.example.repro.api

import com.example.repro.model.HargaBanResponse
import com.example.repro.model.postHargaBan
import com.example.repro.model.Ambil
import com.example.repro.model.HargaKendaraan
import com.example.repro.model.OlahRequest
import com.example.repro.model.OlahResponse
import com.example.repro.model.RiwayatPemasokResponse
import com.example.repro.model.UpdateHargaBanRequest
import com.example.repro.model.getPemasok
import com.example.repro.model.getStokByPemasokId
import com.example.repro.model.getTotalStokPemasok
import com.example.repro.model.getAmbilStok
import com.example.repro.model.getRiwayatPemasok
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {
    // GET
    @GET("get_pemasok.php")
    fun getPemasokList(): Call<ApiResponse<List<getPemasok>>>

    @GET("get_harga_ban.php")
    fun getHargaBan(): Call<ApiResponse<List<postHargaBan>>>

    @GET("get_stok_belum_diambil.php")
    fun getAmbilStok(): Call<ApiResponse<List<getAmbilStok>>>

    @GET("get_harga_ban.php")
    fun getHargaKendaraan(): Call<ApiResponse<List<HargaKendaraan>>>

    @GET("get_ambil.php")
    fun getAmbil(): Call<List<Ambil>>

    // POST
    @POST("login.php")
    fun loginUser(@Body body: RequestBody): Call<ResponseBody>

    @POST("post_pemasok.php")
    fun registerUser(@Body body: RequestBody): Call<ResponseBody>

    @Headers("Content-Type: application/json")
    @POST("get_riwayat_pemasok.php")
    fun getRiwayatByPemasokId(@Body requestBody: HashMap<String, Int>): Call<ApiResponse<getRiwayatPemasok>>

    @Headers("Content-Type: application/json")
    @POST("get_status_stok.php")
    fun getStokByPemasokId(@Body requestBody: HashMap<String, Int>): Call<ApiResponse<List<getStokByPemasokId>>>

    @Headers("Content-Type: application/json")
    @POST("get_total_pemasok.php")
    fun getTotalStokPemasok(@Body requestBody: HashMap<String, Int>): Call<ApiResponse<getTotalStokPemasok>>

    @Headers("Content-Type: application/json")
    @POST("post_harga_ban.php")
    fun tambahHargaBan(@Body HargaBanResponse: HargaBanResponse): Call<ApiResponse<HargaBanResponse>>

    @POST("update_harga_ban.php")
    fun updateHargaBan(@Body request: UpdateHargaBanRequest): Call<ApiResponse<UpdateHargaBanRequest>>

    @POST("get_olah_by_id.php")
    fun getAmbilByPemasokId(@Body requestBody: Map<String, Int>): Call<ApiResponse<List<RiwayatPemasokResponse>>>

    @Headers("Content-Type: application/json")
    @POST("post_olah.php")
    fun simpanOlah(@Body request: OlahRequest): Call<ApiResponse<OlahResponse>>
}
