package com.example.beautifulweather.utils

object WeatherUrlUtil {
    fun setWeatherRealTImeUrl(lng: Double, lat: Double): String {
        return "https://api.caiyunapp.com/v2/jK3fVm8W0w1Xxl5M/${lng},${lat}/realtime"
    }

    fun setWeatherDailyUrl(lng: Double, lat: Double, daily: Int): String {
        return "https://api.caiyunapp.com/v2/jK3fVm8W0w1Xxl5M/${lng},${lat}/daily?dailysteps=${daily}"
    }
}