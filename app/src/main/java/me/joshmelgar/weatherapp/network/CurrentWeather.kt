package me.joshmelgar.weatherapp.network

import com.google.gson.annotations.SerializedName

data class CurrentWeather(
    @SerializedName("main") val main: Main,
    @SerializedName("wind") val wind: Wind
)

data class Main(
    @SerializedName("temp") val temp: Double,
    @SerializedName("feels_like") val feelsLike: Double,
    @SerializedName("temp_min") val tempMin: Double,
    @SerializedName("temp_max") val tempMax: Double,
)

data class Wind(
    @SerializedName("speed") val speed: Double,
    @SerializedName("deg") val degree: Int
)
