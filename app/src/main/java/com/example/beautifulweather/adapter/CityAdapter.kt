package com.example.beautifulweather.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.beautifulweather.R
import com.example.beautifulweather.activity.CityActivity
import com.example.beautifulweather.bean.Place
import com.example.beautifulweather.databinding.ItemCityBinding
import com.example.beautifulweather.viewmode.CityViewModel
import kotlin.collections.contains as contains1

class CityAdapter(
    val cityActivity: Activity,
    val sharedPreferences: SharedPreferences?,
    val vm: CityViewModel
) : RecyclerView.Adapter<CityAdapter.InnerHolder>() {
    private val adapterPlaceList = mutableListOf<Place?>()


    class InnerHolder(itemView: View, val dataBinding: ItemCityBinding) :
        RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InnerHolder {
        val dataBinding = DataBindingUtil.inflate<ItemCityBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_city, parent, false
        )

        return InnerHolder(dataBinding.root, dataBinding)
    }

    override fun onBindViewHolder(holder: InnerHolder, position: Int) {
        val place = adapterPlaceList[position]
        if (place != null) {
            holder.dataBinding.city = place
            val itemClickListener =
                CityActivity().CityItemClickListener(place, cityActivity, sharedPreferences, vm)

            holder.dataBinding.cityClick = itemClickListener
        }

    }

    override fun getItemCount() = adapterPlaceList.size

    @SuppressLint("NotifyDataSetChanged")
    fun setData(it: List<Place>?) {
        adapterPlaceList.clear()
        if (it != null) {
            adapterPlaceList.addAll(it)
        }
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setHistoryData(it: List<Place>?) {
        adapterPlaceList.clear()
        if (it!=null) {
            if (!adapterPlaceList.containsAll(it)) {
                adapterPlaceList.addAll(it)
            }
        }

        notifyDataSetChanged()
    }
}