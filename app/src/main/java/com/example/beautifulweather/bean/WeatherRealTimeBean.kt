package com.example.beautifulweather.bean

data class WeatherRealTimeBean(
    val cityName: String,
    val temp: String,
    val cover: Int,
    val bodyTempAndSkycon: String,
    val dressingIndex: String,
    val ultravioletIndex: String,
    val coldRiskIndex: String,
    val carWashing: String
)
