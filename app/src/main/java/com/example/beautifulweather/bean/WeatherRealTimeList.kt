package com.example.beautifulweather.bean
class WeatherRealTimeRepository {
    data class WeatherRealTimeList(
        val api_status: String,
        val api_version: String,
        val lang: String,
        val location: List<Double>,
        val result: Result,
        val server_time: Int,
        val status: String,
        val tzshift: Int,
        val unit: String
    )

    data class Result(
        val apparent_temperature: Double,
        val aqi: Int,
        val cloudrate: Double,
        val co: Double,
        val comfort: Comfort,
        val dswrf: Double,
        val humidity: Double,
        val no2: Int,
        val o3: Int,
        val pm10: Int,
        val pm25: Int,
        val precipitation: Precipitation,
        val pres: Double,
        val skycon: String,
        val so2: Int,
        val status: String,
        val temperature: Double,
        val ultraviolet: Ultraviolet,
        val visibility: Double,
        val wind: Wind
    )

    data class Comfort(
        val desc: String,
        val index: Int
    )

    data class Precipitation(
        val local: Local
    )

    data class Ultraviolet(
        val desc: String,
        val index: Double
    )

    data class Wind(
        val direction: Double,
        val speed: Double
    )

    data class Local(
        val datasource: String,
        val intensity: Double,
        val status: String
    )
}