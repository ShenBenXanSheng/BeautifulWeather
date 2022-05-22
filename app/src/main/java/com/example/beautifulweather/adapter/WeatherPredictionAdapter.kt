package com.example.beautifulweather.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.beautifulweather.R
import com.example.beautifulweather.bean.WeatherDailyBean
import com.example.beautifulweather.bean.WeatherDailyRepository
import com.example.beautifulweather.databinding.ItemWeatherPredictionBinding
import com.example.beautifulweather.utils.SizeUtil


class WeatherPredictionAdapter : RecyclerView.Adapter<WeatherPredictionAdapter.InnerHolder>() {
    private val resultTempList = mutableListOf<WeatherDailyRepository.Temperature>()
    private val resultSkyconList = mutableListOf<WeatherDailyRepository.Skycon>()

    class InnerHolder(itemView: View, val binding: ItemWeatherPredictionBinding) :
        RecyclerView.ViewHolder(itemView) {
    }

    companion object {
        @JvmStatic
        @BindingAdapter("cover")
        fun setWeatherDailyCover(itemView: ImageView, cover: Int) {
            Glide.with(itemView).load(cover).into(itemView)
        }

        const val TAG = "PredictionAdapter"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InnerHolder {
        val binding = DataBindingUtil.inflate<ItemWeatherPredictionBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_weather_prediction,
            parent,
            false
        )

        return InnerHolder(binding.root, binding)
    }

    @SuppressLint("LongLogTag")
    override fun onBindViewHolder(holder: InnerHolder, position: Int) {
        val temperature = resultTempList[position]
        val skycon = resultSkyconList[position]
        var skyString = ""
        var skyCover = 0
        holder.binding.apply {

            when (skycon.value) {
                "CLEAR_DAY", "CLEAR_NIGHT" -> {
                    skyString = "晴"
                    skyCover = R.mipmap.qingtian
                    setViewMarginRight(wpCoverIv)
                }

                "PARTLY_CLOUDY_DAY", "PARTLY_CLOUDY_NIGHT" -> {
                    skyString = "多云"
                    skyCover = R.mipmap.duoyun
                }

                "CLOUDY" -> {
                    skyString = "阴"
                    skyCover = R.mipmap.yintian
                    setViewMarginRight(wpCoverIv)
                }

                "LIGHT_HAZE", "MODERATE_HAZE", "HEAVY_HAZE" -> {
                    skyString = "雾霾"
                    skyCover = R.mipmap.yintian
                }

                "LIGHT_RAIN", "MODERATE_RAIN", "HEAVY_RAIN", "STORM_RAIN", "RAIN" -> {
                    skyString = "雨"
                    skyCover = R.mipmap.yutian
                    setViewMarginRight(wpCoverIv)
                }

                "FOG" -> {
                    skyString = "雾"
                    skyCover = R.mipmap.duoyun
                    setViewMarginRight(wpCoverIv)
                }

                "LIGHT_SNOW", "MODERATE_SNOW", "HEAVY_SNOW", "STORM_SNOW" -> {
                    skyString = "雪"
                    skyCover = R.mipmap.tianqi
                    setViewMarginRight(wpCoverIv)
                }
                "DUST", "SAND", "WIND" -> {
                    skyString = "风沙"
                    skyCover = R.mipmap.fengsha
                    setViewMarginRight(wpCoverIv)
                }
            }
            val minTemp = String.format("%.1f", temperature.min)
            val maxTemp = String.format("%.1f", temperature.max)
            val temp = "${minTemp}~${maxTemp}°C"
            weatherDaily = WeatherDailyBean(temperature.date, temp, skyString, skyCover)

        }
    }

    private fun setViewMarginRight(view: View) {
        val lp = RelativeLayout.LayoutParams(
            SizeUtil.dip2px(30f),
            SizeUtil.dip2px(30f)
        )
        lp.setMargins(0, 0, SizeUtil.dip2px(20f), 0)

        view.layoutParams = lp
    }

    override fun getItemCount() = resultTempList.size

    @SuppressLint("NotifyDataSetChanged")
    fun setData(
        temperature: List<WeatherDailyRepository.Temperature>,
        skycon: List<WeatherDailyRepository.Skycon>
    ) {
        resultTempList.clear()
        resultSkyconList.clear()
        resultTempList.addAll(temperature)
        resultSkyconList.addAll(skycon)

        notifyDataSetChanged()
    }
}