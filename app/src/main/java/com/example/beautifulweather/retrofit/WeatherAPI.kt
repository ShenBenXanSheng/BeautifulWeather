package com.example.beautifulweather.retrofit

import com.example.beautifulweather.bean.PlaceList

import com.example.beautifulweather.bean.WeatherDailyRepository


import com.example.beautifulweather.bean.WeatherRealTimeRepository
import retrofit2.http.GET
import retrofit2.http.QueryMap
import retrofit2.http.Url

interface WeatherAPI {
    companion object {
        const val BASE_URL = "https://api.caiyunapp.com/v2/"

    }

    @GET("place")
    suspend fun getPlaceList(
        @QueryMap map:Map<String,@JvmSuppressWildcards Any>
    ): PlaceList

    @GET
    suspend fun getWeatherRealTimeList(@Url weatherRealtimeUrl: String): WeatherRealTimeRepository.WeatherRealTimeList

    @GET
    suspend fun getWeatherDailyList(@Url weatherDailyList:String): WeatherDailyRepository.WeatherDailyList
}