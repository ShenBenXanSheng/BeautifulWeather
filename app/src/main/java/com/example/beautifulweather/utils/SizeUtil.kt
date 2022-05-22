package com.example.beautifulweather.utils

import android.content.Context
import com.example.beautifulweather.base.BaseApp

object SizeUtil {
    fun dip2px(dpValue: Float): Int {
        val scale: Float = BaseApp.mContext.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }
}