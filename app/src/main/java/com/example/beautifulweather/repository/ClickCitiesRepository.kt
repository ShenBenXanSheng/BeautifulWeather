package com.example.beautifulweather.repository

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.beautifulweather.activity.WeatherActivity
import com.example.beautifulweather.base.BaseApp
import com.example.beautifulweather.bean.Place
import com.example.beautifulweather.viewmode.CityViewModel
import com.google.gson.Gson

object ClickCitiesRepository {
    private val citySharedPreferences: SharedPreferences by lazy {
        BaseApp.mContext.getSharedPreferences("Cities", MODE_PRIVATE)
    }
    private val gson = Gson()
    private val citySpList = mutableSetOf<String>()

     val placeList = mutableListOf<Place>()
    fun addIsClickCity(place: Place) {
        val city = gson.toJson(place)
        if (!citySpList.contains(city)) {
            citySpList.add(city)
        }
        Log.d(WeatherActivity.TAG, "记录的城市-->${citySpList.size}")
        citySharedPreferences.edit().putStringSet("cities", citySpList).apply()
    }

    private val cityLivedata = MutableLiveData<MutableList<Place>>()


    fun getClickCity(): MutableLiveData<MutableList<Place>> {
        val stringSet = citySharedPreferences.getStringSet("cities", null)
        if (stringSet != null) {
            citySpList.addAll(stringSet)
            val toMutableList = citySpList.toMutableList()

            toMutableList.forEach {
                if (!placeList.contains(gson.fromJson(it, Place::class.java)))
                    placeList.add(gson.fromJson(it, Place::class.java))
            }
        }
        cityLivedata.postValue(placeList.toMutableSet().toMutableList())
        return cityLivedata
    }

    fun cleanClickCity() {
        citySharedPreferences.edit().clear().apply()
        citySpList.clear()
        placeList.clear()
        cityLivedata.postValue(placeList)
    }
}