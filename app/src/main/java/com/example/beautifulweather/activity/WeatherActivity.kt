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
import com.example.beautifulweather.repository.SkyRepository
import com.example.beautifulweather.retrofit.KeypadUtil
import com.example.beautifulweather.view.CleanHistoryDialog
import com.example.beautifulweather.viewmode.CityViewModel
import com.example.beautifulweather.viewmode.WeatherViewModel
import com.google.gson.Gson
import com.scwang.smart.refresh.header.MaterialHeader
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
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
    lateinit var navLayout: View//??????????????????

    lateinit var navContent: View

    private var currentLng = 0.0
    private var currentLat = 0.0
    val historyList = mutableListOf<Place>()
    override fun initView() {
        super.initView()

        val intent = intent
        val stringExtra = intent.getStringExtra("cityName")
        val parcelableExtra = intent.getParcelableExtra<Location>("location")
        //???????????????
        if (stringExtra != null) {
            city = stringExtra
        }
        //???viewModel???????????????
        if (parcelableExtra != null) {
            Log.d(TAG, "??????${parcelableExtra.lng} ??????${parcelableExtra.lat}")
            currentLng = parcelableExtra.lng
            currentLat = parcelableExtra.lat
            vm.getWeatherRealTimeData(currentLng, currentLat)
            vm.getDailyData(currentLng, currentLat)
        }

        dataBinding.apply {
            //???????????????
            weatherTitleBar.apply {
                setStatusBarScrimColor(Color.TRANSPARENT)
                setCollapsedTitleTextColor(Color.WHITE)
            }


            //????????????
            weatherPredictionRv.apply {
                layoutManager = LinearLayoutManager(this@WeatherActivity)
                adapter = weatherAdapter
            }

            //????????????
            navLayout = LayoutInflater.from(this@WeatherActivity)
                .inflate(R.layout.activity_city, null)

            //?????????????????????
            navContent = LayoutInflater.from(this@WeatherActivity)
                .inflate(R.layout.activity_city_success, navLayout.activityContainerFL, false)


            //????????????????????????
            navContent.cityRecycler.apply {
                layoutManager = LinearLayoutManager(this@WeatherActivity)
                adapter = cityAdapter
            }
            //????????????????????????
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


            weatherSmartRefresh.setRefreshHeader(MaterialHeader(this@WeatherActivity))
            weatherSmartRefresh.setEnableHeaderTranslationContent(true)
            weatherSmartRefresh.setEnableLoadMore(false)

        }
    }

    override fun initListener() {
        super.initListener()
        //toolbar?????????icon
        dataBinding.apply {
            weatherToolbar.setNavigationOnClickListener {
                val intent = Intent(this@WeatherActivity, CityActivity::class.java)
                cityViewModel.sharedPreferences.edit().clear().apply()
                startActivity(intent)
            }

            //????????????
            weatherSmartRefresh.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
                override fun onRefresh(refreshLayout: RefreshLayout) {
                    Log.d(TAG, "????????????")
                    vm.getWeatherRealTimeData(currentLng, currentLat)
                    vm.getDailyData(currentLng, currentLat)
                    refreshLayout.finishRefresh()
                }

                override fun onLoadMore(refreshLayout: RefreshLayout) {

                }

            })
        }

        //????????????????????????
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
                    Toast.makeText(this@WeatherActivity, "??????????????????????????????", Toast.LENGTH_SHORT).show()
                }

                true
            }
        }

        navLayout.cityEditCleanIv.setOnClickListener {

            cityEditCleanIv.visibility = View.GONE

            navLayout.citySearchEd.setText("")


        }

        //??????????????????
        val historyDialog = CleanHistoryDialog(this)
        navLayout.cityClearHistory.setOnClickListener {
            historyDialog.setOnCleanHistoryListener(object :
                CleanHistoryDialog.OnCleanHistoryListener {
                override fun cleanHistoryClick() {

                    if (ClickCitiesRepository.placeList.size == 0) {
                        Toast.makeText(this@WeatherActivity, "???????????????????????????", Toast.LENGTH_SHORT).show()
                    } else {
                        ClickCitiesRepository.cleanClickCity()
                        Toast.makeText(this@WeatherActivity, "????????????", Toast.LENGTH_SHORT).show()
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

            //????????????
            setCurrentWeatherData()

            //????????????
            weatherDailyResult.observe(this@WeatherActivity) {
                it.daily.apply {
                    weatherAdapter.setData(temperature, skycon)

                }
            }
        }
//===========================????????????=================================
        cityViewModel.cityList.observe(this) {
            cityAdapter.setData(it)
        }
    }


    private fun WeatherViewModel.setCurrentWeatherData() {
        weatherResult.observe(this@WeatherActivity) {
            it.apply {
                val temp = "${it.temperature}??C"

                val sky = SkyRepository.getSky(it.skycon)
                val cover = sky.keys.toIntArray()[0]
                val skycon = sky[cover]

                val bodyTempAndSkycon = "${skycon}|????????????:${it.apparent_temperature}"

                val comfortIndex = SkyRepository.getComfortIndex(it.comfort.index)

                dataBinding.weatherRealTime = WeatherRealTimeBean(
                    city,
                    temp,
                    cover,
                    bodyTempAndSkycon,
                    comfortIndex.dress,
                    comfortIndex.ultraviolet,
                    comfortIndex.coldSick,
                    comfortIndex.carWash
                )
            }
        }
    }
}