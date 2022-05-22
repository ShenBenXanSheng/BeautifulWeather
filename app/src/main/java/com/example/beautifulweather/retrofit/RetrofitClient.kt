package com.example.beautifulweather.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

   private val retrofit = Retrofit.Builder()
       .baseUrl(WeatherAPI.BASE_URL)
       .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api = retrofit.create(WeatherAPI::class.java)

}