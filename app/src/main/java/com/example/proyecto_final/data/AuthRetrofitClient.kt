package com.example.proyecto_final.data

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Separado de RetrofitClient.kt porque el login usa otro API Gateway
// (oyweo30w60) distinto al de reportes (87r9qj5x4d)
object AuthRetrofitClient {
    private const val BASE_URL = "https://oyweo30w60.execute-api.us-east-1.amazonaws.com/prod/"

    val api: AuthApi by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApi::class.java)
    }
}