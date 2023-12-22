package me.joshmelgar.weatherapp.models.domain

data class ForecastHomeDetails (
    val weatherType: String,
    val description: String,
    val icon: String,
    val temperature: Double,
    val date: String
)