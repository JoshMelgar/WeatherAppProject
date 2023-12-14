package me.joshmelgar.weatherapp.network

import com.google.gson.annotations.SerializedName

data class Forecast(
    @SerializedName("list") val forecastList: List<ForecastItem>
)

data class ForecastItem(
    @SerializedName("dt_txt") val dtText: String,
    @SerializedName("main") val forecastMain: ForecastMain,
    @SerializedName("weather") val forecastWeather: List<ForecastWeather>
)

data class ForecastMain(
    @SerializedName("temp") val temp: Double
)

data class ForecastWeather(
    @SerializedName("main") val weatherType: String,
    @SerializedName("description") val weatherDescription: String,
    @SerializedName("icon") val icon: String
)
