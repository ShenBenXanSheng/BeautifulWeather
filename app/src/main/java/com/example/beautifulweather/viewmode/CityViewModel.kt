package com.example.beautifulweather.viewmode

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.beautifulweather.bean.Location
import com.example.beautifulweather.bean.Place
import com.example.beautifulweather.repository.WeatherRepository
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.lang.NullPointerException

class CityViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        const val TAG = "CityViewModel"
    }

    private val weatherRepository by lazy {
        WeatherRepository()
    }
    val sharedPreferences: SharedPreferences by lazy {
        application.applicationContext.getSharedPreferences("checkCity", MODE_PRIVATE)
    }



    private val gson by lazy {
        Gson()
    }

    enum class CityLoadState {
        LOADING, SUCCESS, EMPTY, ERROR
    }

    val cityList = MutableLiveData<List<Place>>()
    val cityStateList = MutableLiveData<CityLoadState>()
    fun getCityList(city: String) {
        //保存搜索记录
        Log.d(TAG, city)
        val map = mutableMapOf<String, Any>(
            "query" to city,
            "token" to "jK3fVm8W0w1Xxl5M",
            "lang" to "zh_CN"
        )
        loadCityData(map)
    }

    private fun loadCityData(placeParams: MutableMap<String, Any>) {
        viewModelScope.launch {
            cityStateList.postValue(CityLoadState.LOADING)
            try {
                val cityData = weatherRepository.getCityData(placeParams)
                if (cityData != null && cityData.places.isNotEmpty()) {
                    cityList.postValue(cityData.places)
                    cityStateList.postValue(CityLoadState.SUCCESS)
                } else {
                    cityStateList.postValue(CityLoadState.EMPTY)
                    Log.d(TAG, "暂无数据")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                cityStateList.postValue(CityLoadState.ERROR)
            }
        }
    }

    fun getSpData(): CitySPData? {
        val isIntent = sharedPreferences.getBoolean("isIntent", false)
        val cityName = sharedPreferences.getString("cityName", "")
        val location =
            gson.fromJson(sharedPreferences.getString("location", ""), Location::class.java)
        if (!cityName.isNullOrBlank()) {
            return CitySPData(isIntent, cityName, location)
        }
        return null
    }


    data class CitySPData(var isIntent: Boolean, var cityName: String, var location: Location)
}
