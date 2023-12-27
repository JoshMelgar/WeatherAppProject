package me.joshmelgar.weatherapp.models.domain

data class ForecastMainDetails (
    val date: String,
    val highTemp: Double,
    val lowTemp: Double,
    val iconImageUrl: String,
    val weatherType: String,
    val wind: WindInfo
)