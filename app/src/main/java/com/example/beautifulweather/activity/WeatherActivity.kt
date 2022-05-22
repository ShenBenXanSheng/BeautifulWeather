package com.example.beautifulweather.activity

import android.content.Intent
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.beautifulweather.R
import com.example.beautifulweather.adapter.CityAdapter
import com.example.beautifulweather.adapter.WeatherPredictionAdapter
import com.example.beautifulweather.base.BaseViewModelActivity
import com.example.beautifulweather.bean.Location
import com.example.beautifulweather.bean.Place
import com.example.beautifulweather.bean.WeatherRealTimeBean
import com.example.beautifulweather.databinding.ActivityWeatherBinding
import com.example.beautifulweather.repository.ClickCitiesRepository
import com.example.beautifulweather.retrofit.KeypadUtil
import com.example.beautifulweather.view.CleanHistoryDialog
import com.example.beautifulweather.viewmode.CityViewModel
import com.example.beautifulweather.viewmode.WeatherViewModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_city.*
import kotlinx.android.synthetic.main.activity_city.view.*
import kotlinx.android.synthetic.main.activity_city_success.*
import kotlinx.android.synthetic.main.activity_city_success.view.*


class WeatherActivity : BaseViewModelActivity<WeatherViewModel>() {
    companion object {
        const val TAG = "WeatherActivity"

        @JvmStatic
        @BindingAdapter("bigCover")
        fun loadCover(imageView: ImageView, cover: Int) {
            Glide.with(imageView).load(cover).into(imageView)
        }
    }

    private val dataBinding by lazy {
        DataBindingUtil.inflate<ActivityWeatherBinding>(
            LayoutInflater.from(activityContainer.context),
            R.layout.activity_weather,
            activityContainer,
            false
        )
    }

    private val cityViewModel by lazy {
        ViewModelProvider(this)[CityViewModel::class.java]
    }
    private val weatherAdapter by lazy {
        WeatherPredictionAdapter()
    }
    private val cityAdapter by lazy {
        CityAdapter(this, cityViewModel.sharedPreferences, cityViewModel)
    }

    override fun setSuccessLayout(activityContainer: FrameLayout) = dataBinding.root

    override fun getViewModelClass() = WeatherViewModel::class.java

    lateinit var city: String
    lateinit var navLayout: View//整个侧滑布局

    lateinit var navContent: View

    val historyList = mutableListOf<Place>()
    override fun initView() {
        super.initView()

        val intent = intent
        val stringExtra = intent.getStringExtra("cityName")
        val parcelableExtra = intent.getParcelableExtra<Location>("location")
        //拿到城市名
        if (stringExtra != null) {
            city = stringExtra
        }
        //给viewModel传入经纬度
        if (parcelableExtra != null) {
            Log.d(TAG, "精度${parcelableExtra.lng} 纬度${parcelableExtra.lat}")
            vm.getWeatherRealTimeData(parcelableExtra.lng, parcelableExtra.lat)
            vm.getDailyData(parcelableExtra.lng, parcelableExtra.lat)
        }

        dataBinding.apply {
            //收缩标题栏
            weatherTitleBar.apply {
                setStatusBarScrimColor(Color.TRANSPARENT)
                setCollapsedTitleTextColor(Color.WHITE)
            }


            //预报天气
            weatherPredictionRv.apply {
                layoutManager = LinearLayoutManager(this@WeatherActivity)
                adapter = weatherAdapter
            }

            //侧滑菜单
            navLayout = LayoutInflater.from(this@WeatherActivity)
                .inflate(R.layout.activity_city, null)

            //侧滑菜单的内容
            navContent = LayoutInflater.from(this@WeatherActivity)
                .inflate(R.layout.activity_city_success, navLayout.activityContainerFL, false)


            //侧滑内容正常显示
            navContent.cityRecycler.apply {
                layoutManager = LinearLayoutManager(this@WeatherActivity)
                adapter = cityAdapter
            }
            //侧滑菜单历史记录
            historyList.clear()
            val checkCity = ClickCitiesRepository.getClickCity()
            checkCity.observe(this@WeatherActivity) {

                if (!historyList.containsAll(it)) {
                    historyList.addAll(it)

                }
                cityAdapter.setHistoryData(it)
            }

            navLayout.activityContainerFL.addView(navContent)
            weatherNavigationView.addView(navLayout)
        }


    }

    override fun initListener() {
        super.initListener()
        //toolbar的返回icon
        dataBinding.apply {
            weatherToolbar.setNavigationOnClickListener {
                val intent = Intent(this@WeatherActivity, CityActivity::class.java)
                cityViewModel.sharedPreferences.edit().clear().apply()
                startActivity(intent)
            }
        }

        //侧滑菜单的聊天框
        navLayout.citySearchEd.apply {
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s.toString().isNotBlank()) {
                        navLayout.cityEditCleanIv.visibility = View.VISIBLE
                        cityViewModel.getCityList(s.toString())
                    } else {
                        cityAdapter.setHistoryData(historyList)
                        navLayout.cityEditCleanIv.visibility = View.GONE
                    }
                }

                override fun afterTextChanged(s: Editable?) {

                }

            })

            setOnEditorActionListener { v, actionId, event ->
                KeypadUtil.hideKeyBroad(this, this@WeatherActivity)

                val str = v.text.toString()
                if (str.isNotBlank()) {
                    cityViewModel.getCityList(str)
                } else {
                    Toast.makeText(this@WeatherActivity, "请输入内容后再搜索！", Toast.LENGTH_SHORT).show()
                }

                true
            }
        }

        navLayout.cityEditCleanIv.setOnClickListener {

            cityEditCleanIv.visibility = View.GONE

            navLayout.citySearchEd.setText("")
        }

        //清楚历史记录
        val historyDialog = CleanHistoryDialog(this)
        navLayout.cityClearHistory.setOnClickListener {
            historyDialog.setOnCleanHistoryListener(object :
                CleanHistoryDialog.OnCleanHistoryListener {
                override fun cleanHistoryClick() {

                    if (ClickCitiesRepository.placeList.size == 0) {
                        Toast.makeText(this@WeatherActivity, "已经没有历史记录了", Toast.LENGTH_SHORT).show()
                    } else {
                        ClickCitiesRepository.cleanClickCity()
                        Toast.makeText(this@WeatherActivity, "删除成功", Toast.LENGTH_SHORT).show()
                    }
                }


            })

            historyDialog.show()
        }
    }

    override fun initDataListener() {
        super.initDataListener()
        vm.apply {
            weatherState.observe(this@WeatherActivity) {
                when (it) {
                    WeatherViewModel.WeatherLoadState.LOADING -> {
                        updateState(LoadStates.LOADING)
                    }

                    WeatherViewModel.WeatherLoadState.SUCCESS -> {
                        updateState(LoadStates.SUCCESS)
                    }

                    WeatherViewModel.WeatherLoadState.ERROR -> {
                        updateState(LoadStates.ERROR)
                    }

                    WeatherViewModel.WeatherLoadState.EMPTY -> {
                        updateState(LoadStates.EMPTY)
                    }
                    else -> {}
                }
            }

            //实况天气
            setCurrentWeatherData()

            //预报天气
            weatherDailyResult.observe(this@WeatherActivity) {
                it.daily.apply {
                    weatherAdapter.setData(temperature, skycon)

                }
            }
        }
//===========================侧滑菜单=================================
        cityViewModel.cityList.observe(this) {
            cityAdapter.setData(it)
        }
    }


    private fun WeatherViewModel.setCurrentWeatherData() {
        weatherResult.observe(this@WeatherActivity) {
            it.apply {
                val temp = "${it.temperature}°C"

                var cover = 0
                var skycon = ""
                when (it.skycon) {
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
                val bodyTempAndSkycon = "${skycon}|体感温度:${it.apparent_temperature}"
                var ultravioletIndex = ""
                var dressingIndex = ""
                var coldRiskIndex = ""
                var carWashingIndex = ""
                when (it.comfort.index) {
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
                        dressingIndex = "热"
                        coldRiskIndex = "易发"
                        carWashingIndex = "较不适宜"

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
                        dressingIndex = "冷"
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

                dataBinding.weatherRealTime = WeatherRealTimeBean(
                    city,
                    temp,
                    cover,
                    bodyTempAndSkycon,
                    dressingIndex,
                    ultravioletIndex,
                    coldRiskIndex,
                    carWashingIndex
                )
            }
        }
    }
}