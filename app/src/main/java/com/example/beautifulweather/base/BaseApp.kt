package com.example.beautifulweather.base

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class BaseApp:Application() {
    companion object{
       @SuppressLint("StaticFieldLeak")
       lateinit var  mContext:Context
    }

    override fun onCreate() {
        super.onCreate()
        mContext = applicationContext
    }
}