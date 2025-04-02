package com.example.gomitasmv.core.network


import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {
    private const val BASE_URL = "http://54.92.183.196/api/"

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Método para obtener la instancia de Retrofit
    fun getRetrofitInstance(): Retrofit {
        return retrofit
    }

}