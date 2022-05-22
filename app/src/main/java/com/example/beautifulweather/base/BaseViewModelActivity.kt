package com.example.beautifulweather.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

abstract class BaseViewModelActivity<viewModel:ViewModel> :BaseActivity(){
    lateinit var vm:viewModel
    override fun initViewModel() {
        super.initViewModel()
        vm =  ViewModelProvider(this)[getViewModelClass()]
    }

    abstract fun getViewModelClass(): Class<viewModel>
}