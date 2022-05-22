package com.example.beautifulweather.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beautifulweather.R
import com.example.beautifulweather.adapter.CityAdapter
import com.example.beautifulweather.base.BaseViewModelActivity
import com.example.beautifulweather.bean.Place
import com.example.beautifulweather.repository.ClickCitiesRepository
import com.example.beautifulweather.retrofit.KeypadUtil
import com.example.beautifulweather.view.CleanHistoryDialog
import com.example.beautifulweather.viewmode.CityViewModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_city.*
import kotlinx.android.synthetic.main.activity_city_success.*

class CityActivity : BaseViewModelActivity<CityViewModel>() {
    companion object {
        const val TAG = "CityActivity"
    }

    private val cityAdapter by lazy {
        CityAdapter(this, vm.sharedPreferences, vm)
    }

    private val historyList = mutableListOf<Place>()
    override fun setRootView(): Int {
        return R.layout.activity_city
    }

    override fun setSuccessLayout(activityContainer: FrameLayout): View =
        LayoutInflater.from(this).inflate(R.layout.activity_city_success, activityContainer, false)

    override fun initView() {
        super.initView()
        cityRecycler.apply {
            layoutManager = LinearLayoutManager(this@CityActivity)
            adapter = cityAdapter
        }

        val spData = vm.getSpData()
        if (spData != null && spData.isIntent) {
            val intent = Intent(this, WeatherActivity::class.java)
            intent.putExtra("cityName", spData.cityName)
            intent.putExtra("location", spData.location)
            startActivity(intent)
            finish()
        }
        historyList.clear()
        ClickCitiesRepository.getClickCity().observe(this){

            if (!historyList.containsAll(it)) {
                historyList.addAll(it)
            }
            cityAdapter.setHistoryData(it)
        }


    }


    override fun initListener() {
        super.initListener()

        citySearchEd.setOnEditorActionListener { v, actionId, event ->
            val content = v.text.toString()
            if (content.isNotBlank()) {
                vm.getCityList(content)
                KeypadUtil.hideKeyBroad(citySearchEd, this)
                cityRecycler.visibility = View.VISIBLE

            } else {
                Toast.makeText(this, "请输入内容后再搜索！", Toast.LENGTH_SHORT).show()
            }

            true
        }

        citySearchEd.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isBlank()) {
                    cityAdapter.setHistoryData(historyList)
                    cityEditCleanIv.visibility = View.GONE
                    cityClearHistory.visibility = View.VISIBLE

                } else {
                    cityEditCleanIv.visibility = View.VISIBLE
                    cityClearHistory.visibility = View.GONE
                    vm.getCityList(s.toString())
                }
            }

        })

        cityEditCleanIv.setOnClickListener {
            cityEditCleanIv.visibility = View.GONE

            citySearchEd.setText("")

        }

        val historyDialog = CleanHistoryDialog(this)
        cityClearHistory.setOnClickListener {
            historyDialog.setOnCleanHistoryListener(object :
                CleanHistoryDialog.OnCleanHistoryListener {
                override fun cleanHistoryClick() {
                    if (ClickCitiesRepository.placeList.size == 0) {
                        Toast.makeText(this@CityActivity, "已经没有历史记录了", Toast.LENGTH_SHORT).show()
                    } else {
                        ClickCitiesRepository.cleanClickCity()
                        Toast.makeText(this@CityActivity, "删除成功", Toast.LENGTH_SHORT).show()
                    }

                }
            })

            historyDialog.show()
        }
    }

    override fun initDataListener() {
        super.initDataListener()
        vm.cityStateList.observe(this) {
            when (it) {
                CityViewModel.CityLoadState.LOADING -> {
                    updateState(LoadStates.LOADING)
                    cityRecycler.visibility = View.GONE
                }

                CityViewModel.CityLoadState.SUCCESS -> {
                    updateState(LoadStates.SUCCESS)
                    cityRecycler.visibility = View.VISIBLE
                }

                CityViewModel.CityLoadState.EMPTY -> {
                    updateState(LoadStates.EMPTY)
                    cityRecycler.visibility = View.GONE
                }

                CityViewModel.CityLoadState.ERROR -> {
                    updateState(LoadStates.ERROR)
                    cityRecycler.visibility = View.GONE
                }
                else -> {}
            }
        }

        vm.cityList.observe(this) {
            cityAdapter.setData(it)
        }

    }

    override fun getViewModelClass(): Class<CityViewModel> {
        return CityViewModel::class.java
    }

    inner class CityItemClickListener(
        private val place: Place,
        private val activity: Activity,
        private val checkPreferences: SharedPreferences?,
        val cityModel: CityViewModel
    ) {
        @SuppressLint("CommitPrefEdits")
        fun itemClick(item: View) {
            //记录选中过的城市
            ClickCitiesRepository.addIsClickCity(place)
            val intent = Intent(activity, WeatherActivity::class.java)
            intent.putExtra("cityName", place.name)
            intent.putExtra("location", place.location)
            val toBundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                activity,
                item,
                "cityNameTn"
            ).toBundle()
            checkPreferences?.edit()?.apply {
                putBoolean("isIntent", true)
                putString("cityName", place.name)
                putString("location", Gson().toJson(place.location))
                apply()
            }
            activity.startActivity(intent, toBundle)
            activity.finish()
        }
    }
}