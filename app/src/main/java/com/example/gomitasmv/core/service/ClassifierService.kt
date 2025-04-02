package com.example.gomitasmv.core.service

import com.example.gomitasmv.core.network.RetrofitHelper
import com.example.gomitasmv.data.model.ClassifierResponse
import com.example.gomitasmv.data.model.ClassesResponse
import com.example.gomitasmv.data.model.HealthResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query


interface ClassifierApiService {


    @GET("health")
    suspend fun checkHealth(): Response<HealthResponse>


    @GET("info")
    suspend fun getAvailableClasses(): Response<ClassesResponse>


    @Multipart
    @POST("predict")
    suspend fun predictImage(
        @Part image: MultipartBody.Part,
        @Query("enhance") enhance: Boolean = false
    ): Response<ClassifierResponse>
}

object ClassifierService {
    val apiService: ClassifierApiService by lazy {
        RetrofitHelper.getRetrofitInstance().create(ClassifierApiService::class.java)
    }
}