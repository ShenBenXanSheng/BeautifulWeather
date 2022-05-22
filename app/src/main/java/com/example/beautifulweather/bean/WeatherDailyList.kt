package com.example.beautifulweather.bean
class WeatherDailyRepository{

data class WeatherDailyList(
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
    val daily: Daily,
    val primary: Int
)

data class Daily(
    val skycon: List<Skycon>,
    val temperature: List<Temperature>,

    )

data class Skycon(
    val date: String,
    val value: String
)


data class Temperature(
    val avg: Double,
    val date: String,
    val max: Double,
    val min: Double
)


data class Avg(
    val direction: Double,
    val speed: Double
)

data class Max(
    val direction: Double,
    val speed: Double
)

data class Min(
    val direction: Double,
    val speed: Double
)
}