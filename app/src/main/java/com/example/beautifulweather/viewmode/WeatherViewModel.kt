package com.example.beautifulweather.viewmode

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beautifulweather.bean.WeatherDailyRepository

import com.example.beautifulweather.bean.WeatherRealTimeRepository
import com.example.beautifulweather.repository.WeatherRepository
import com.example.beautifulweather.utils.WeatherUrlUtil
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {
    companion object {
        const val TAG = "WeatherViewModel"
        const val DEFAULT_DAILY = 5
    }

    private var daily = DEFAULT_DAILY
    private lateinit var weatherRealTImeUrl: String
    private val weatherRepository by lazy {
        WeatherRepository()
    }

    val weatherResult = MutableLiveData<WeatherRealTimeRepository.Result>()

    val weatherDailyResult = MutableLiveData<WeatherDailyRepository.Result>()

    enum class WeatherLoadState {
        LOADING, SUCCESS, EMPTY, ERROR
    }

    val weatherState = MutableLiveData<WeatherLoadState>()
    fun getWeatherRealTimeData(lng: Double, lat: Double) {
        weatherRealTImeUrl = WeatherUrlUtil.setWeatherRealTImeUrl(lng, lat)
        Log.d(TAG, weatherRealTImeUrl)
        getRealTime(weatherRealTImeUrl)
    }

    private fun getRealTime(url: String) {
        weatherState.postValue(WeatherLoadState.LOADING)
        viewModelScope.launch {
            try {
                val weatherRealTime = weatherRepository.getWeatherRealTime(url)
                if (weatherRealTime != null) {
                    weatherState.postValue(WeatherLoadState.SUCCESS)
                    Log.d(TAG, weatherRealTime.result.temperature.toString())
                    weatherResult.postValue(weatherRealTime.result)
                } else {
                    weatherState.postValue(WeatherLoadState.EMPTY)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                weatherState.postValue(WeatherLoadState.ERROR)
            }

        }
    }

    fun getDailyData(lng: Double, lat: Double) {
        val weatherDailyUrl = WeatherUrlUtil.setWeatherDailyUrl(lng, lat, daily)
        getDaily(weatherDailyUrl)
    }

    private fun getDaily(url: String) {
        Log.d(TAG, url)
        viewModelScope.launch {
            try {
                val weatherDaily = weatherRepository.getWeatherDaily(url)
                if (weatherDaily != null) {
                    Log.d(TAG, weatherDaily.result.toString())
                    weatherDailyResult.postValue(weatherDaily.result)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }
}