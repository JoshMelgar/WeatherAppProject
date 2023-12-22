package me.joshmelgar.weatherapp.models.dto

import com.google.gson.annotations.SerializedName

data class Forecast(
    @SerializedName("list") val forecastList: List<ForecastItem>
)

data class ForecastItem(
    @SerializedName("dt_txt") val dtText: String,
    @SerializedName("main") val forecastMain: ForecastMain,
    @SerializedName("weather") val forecastWeather: List<ForecastWeather>,
    @SerializedName("wind") val wind: ForecastWind
)

data class ForecastMain(
    @SerializedName("temp") val temp: Double,
    @SerializedName("temp_min") val tempMin: Double,
    @SerializedName("temp_max") val tempMax: Double
)

data class ForecastWind(
    @SerializedName("speed") val speed: Double,
    @SerializedName("deg") val deg: Int
)

data class ForecastWeather(
    @SerializedName("main") val weatherType: String,
    @SerializedName("description") val description: String,
    @SerializedName("icon") val icon: String
)
