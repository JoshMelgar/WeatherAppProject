package me.joshmelgar.weatherapp.network

import com.google.gson.annotations.SerializedName

data class Geocoding(
    @SerializedName("lat") val latitude: Double,
    @SerializedName("lon") val longitude: Double
)
