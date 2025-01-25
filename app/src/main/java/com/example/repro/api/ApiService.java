package com.example.repro.api;
import com.example.repro.ui.pengelola.Ambil;
import com.example.repro.ui.pengelola.Pemasok;

import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    @POST("login.php")
    Call<ResponseBody> loginUser(@Body RequestBody body);

    @POST("post_pemasok.php")
    Call<ResponseBody> registerUser(@Body RequestBody body);

    @GET("get_pemasok.php")
    Call<ApiResponse<List<Pemasok>>> getPemasokList();

    @GET("get_ambil.php")
    Call<List<Ambil>> getAmbil();
}
