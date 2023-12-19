package me.joshmelgar.weatherapp.models

data class DailyForecast(
    val day: String,
    val highTemp: Double,
    val lowTemp: Double,
    val windSpeed: Double,
    val icon: String,
    val iconDesc: String,
    val windDeg: Double
)