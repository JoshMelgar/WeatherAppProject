package me.joshmelgar.weatherapp.models.domain

data class DailyForecast(
    val day: String,
    val highTemp: Double,
    val lowTemp: Double,
    val iconImageUrl: String,
    val iconDesc: String,
    val wind: WindInfo
)