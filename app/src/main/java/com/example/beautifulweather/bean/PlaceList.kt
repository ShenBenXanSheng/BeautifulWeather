package com.example.beautifulweather.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class PlaceList(
    val places: List<Place>,
    val query: String,
    val status: String
)

data class Place(
    val formatted_address: String,
    val id: String,
    val location: Location,
    val name: String,
    val place_id: String
)
@Parcelize
data class Location(
    val lat: Double,
    val lng: Double
):Parcelable