package me.joshmelgar.weatherapp.models.domain

data class WeatherDetails(
    val temperature: Double?,
    val feelsLike: Double?,
    val lowTemp: Double?,
    val highTemp: Double?,
    val wind: WindInfo?
)
