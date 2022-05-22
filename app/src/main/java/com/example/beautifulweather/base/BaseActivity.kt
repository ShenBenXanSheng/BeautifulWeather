package com.example.beautifulweather.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.beautifulweather.R

abstract class BaseActivity : AppCompatActivity() {
    lateinit var activityContainer: FrameLayout
    lateinit var loadingView: View
    lateinit var emptyView:View
    lateinit var errorView:View
    enum class LoadStates{
        LOADING,SUCCESS,EMPTY,ERROR
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(setRootView())
        activityContainer = findViewById(R.id.activityContainerFL)
        loadAllView()
        hideAllView()
        initViewModel()
        initView()
        initListener()
        initDataListener()
    }

    open fun initViewModel() {

    }

    open fun initListener() {

    }

    open fun initDataListener() {

    }

    private fun loadAllView(){
        //加载
        loadingView = LayoutInflater.from(this).inflate(R.layout.state_loading,activityContainer,false)
        activityContainer.addView(loadingView)
        //成功
        activityContainer.addView(setSuccessLayout(activityContainer))
        //为空
        emptyView = LayoutInflater.from(this).inflate(R.layout.state_empty,activityContainer,false)
        activityContainer.addView(emptyView)
        //错误
        errorView = LayoutInflater.from(this).inflate(R.layout.state_error,activityContainer,false)
        activityContainer.addView(errorView)


    }

    private fun hideAllView(){
        loadingView.visibility = View.GONE
        setSuccessLayout(activityContainer).visibility = View.GONE
        emptyView.visibility = View.GONE
        errorView.visibility = View.GONE
    }

    open fun updateState(loadStates: LoadStates){
        hideAllView()
        when(loadStates){
            LoadStates.LOADING->{
                loadingView.visibility = View.VISIBLE
            }

            LoadStates.SUCCESS->{
                setSuccessLayout(activityContainer).visibility = View.VISIBLE
            }

            LoadStates.EMPTY->{
                emptyView.visibility = View.VISIBLE
            }

            LoadStates.ERROR->{
                errorView.visibility = View.VISIBLE
            }
        }
    }

    open fun setRootView(): Int {
        return R.layout.activity_base
    }

    open fun initView() {

    }

    abstract fun setSuccessLayout(activityContainer: FrameLayout): View
}