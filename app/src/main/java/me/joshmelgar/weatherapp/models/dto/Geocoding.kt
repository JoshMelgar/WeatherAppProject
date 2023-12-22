package me.joshmelgar.weatherapp.models.dto

import com.google.gson.annotations.SerializedName

data class Geocoding(
    @SerializedName("name") val cityName: String,
    @SerializedName("country") val cityCountry: String,
    @SerializedName("state") val cityState: String
)
