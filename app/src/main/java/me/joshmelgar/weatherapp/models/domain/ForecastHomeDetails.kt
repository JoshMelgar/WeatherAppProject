package me.joshmelgar.weatherapp.models.domain

data class ForecastHomeDetails(
    val weatherType: String,
    val description: String,
    val iconImageUrl: String,
    val temperature: Double,
    var date: String
)