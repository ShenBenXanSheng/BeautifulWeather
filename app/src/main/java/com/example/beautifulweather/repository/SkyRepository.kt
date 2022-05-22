package com.example.beautifulweather.repository

import com.example.beautifulweather.R

object SkyRepository {
    fun getSky(currentSkycon: String): MutableMap<Int, String> {
        val skyMap = mutableMapOf<Int, String>()
        var cover = 0
        var skycon = ""
        when (currentSkycon) {
            "CLEAR_DAY", "CLEAR_NIGHT" -> {
                cover = R.mipmap.weather_sunshine
                skycon = "晴天"
            }
            "PARTLY_CLOUDY_DAY",
            "PARTLY_CLOUDY_NIGHT" -> {
                skycon = "多云"
                cover = R.mipmap.weather_gale
            }
            "CLOUDY" -> {
                skycon = "阴天"
                cover = R.mipmap.weather_gale
            }
            "LIGHT_HAZE",
            "MODERATE_HAZE",
            "HEAVY_HAZE" -> {
                skycon = "雾霾"
                cover = R.mipmap.weather_gale
            }
            "FOG" -> {
                skycon = "阴天"
                cover = R.mipmap.weather_gale
            }
            "WIND" -> {
                skycon = "多云"
                cover = R.mipmap.weather_gale
            }

            "LIGHT_SNOW", "MODERATE_SNOW" -> {
                skycon = "小雪"
                cover = R.mipmap.weather_light_snow
            }

            "HEAVY_SNOW", "STORM_SNOW" -> {
                skycon = "大雪"
                cover = R.mipmap.weather_big_snow
            }

            "LIGHT_RAIN", "MODERATE_RAIN" -> {
                skycon = "小雨"
                cover = R.mipmap.weather_rainy_and_snow
            }

            "HEAVY_RAIN", "STORM_RAIN" -> {
                skycon = "大雨"
                cover = R.mipmap.weather_rainy
            }
            else -> {

            }
        }
        skyMap.put(cover, skycon)
        return skyMap
    }


    fun getComfortIndex(index: Int): ComfortIndex {
        var ultravioletIndex = ""
        var dressingIndex = ""
        var coldRiskIndex = ""
        var carWashingIndex = ""
        when (index) {
            0 -> {
                ultravioletIndex = "无"
                dressingIndex = "极热"
                coldRiskIndex = "少发"
                carWashingIndex = "适宜"
            }
            1 -> {
                ultravioletIndex = "很弱"
                dressingIndex = "极热"
                coldRiskIndex = "少发"
                carWashingIndex = "适宜"
            }
            2 -> {
                ultravioletIndex = "很弱"
                dressingIndex = "很热"
                coldRiskIndex = "较易发"
                carWashingIndex = "较适宜"
            }
            3 -> {
                ultravioletIndex = "弱"
                dressingIndex = "较热"
                coldRiskIndex = "易发"
                carWashingIndex = "不适宜"

            }
            4 -> {
                ultravioletIndex = "弱"
                dressingIndex = "温暖"
                coldRiskIndex = "较易发"
                carWashingIndex = "不适应"
            }
            5 -> {
                ultravioletIndex = "中等"
                dressingIndex = "凉爽"
                coldRiskIndex = "较易发"
                carWashingIndex = "不适应"
            }
            6 -> {
                ultravioletIndex = "中等"
                dressingIndex = "较冷"
                coldRiskIndex = "较易发"
                carWashingIndex = "不适应"
            }
            7 -> {
                ultravioletIndex = "强"
                dressingIndex = "寒冷"
                coldRiskIndex = "较易发"
                carWashingIndex = "不适应"
            }
            8 -> {
                ultravioletIndex = "强"
                dressingIndex = "极冷"
                coldRiskIndex = "较易发"
                carWashingIndex = "不适应"
            }
            9 -> {
                ultravioletIndex = "很强"
                dressingIndex = "极冷"
                coldRiskIndex = "较易发"
                carWashingIndex = "不适应"
            }
            10 -> {
                ultravioletIndex = "很强"
                dressingIndex = "极冷"
                coldRiskIndex = "较易发"
                carWashingIndex = "不适应"
            }

            11 -> {
                ultravioletIndex = "很强"
                dressingIndex = "极冷"
                coldRiskIndex = "较易发"
                carWashingIndex = "不适应"
            }

            12 -> {
                ultravioletIndex = "很强"
                dressingIndex = "极冷"
                coldRiskIndex = "较易发"
                carWashingIndex = "不适应"
            }

            13 -> {
                ultravioletIndex = "中等"
                dressingIndex = "极冷"
                coldRiskIndex = "较易发"
                carWashingIndex = "不适应"
            }
            else -> {}
        }
        return ComfortIndex(coldRiskIndex, dressingIndex, ultravioletIndex, carWashingIndex)
    }

    data class ComfortIndex(
        var coldSick: String,
        var dress: String,
        var ultraviolet: String,
        var carWash: String
    )
}