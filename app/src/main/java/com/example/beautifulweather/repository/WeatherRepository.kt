package com.example.beautifulweather.repository


import com.example.beautifulweather.retrofit.RetrofitClient

class WeatherRepository {
    suspend fun getCityData(placeParams: Map<String, Any>) =
        RetrofitClient.api.getPlaceList(placeParams)


    suspend fun getWeatherRealTime(weatherRealTimeUrl: String) =
        RetrofitClient.api.getWeatherRealTimeList(weatherRealTimeUrl)

    suspend fun getWeatherDaily(weatherDailyUrl:String) = RetrofitClient.api.getWeatherDailyList(weatherDailyUrl)

}