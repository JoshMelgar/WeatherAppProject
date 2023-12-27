package me.joshmelgar.weatherapp.respositories.extensions

import me.joshmelgar.weatherapp.models.domain.ForecastHomeDetails
import me.joshmelgar.weatherapp.models.domain.ForecastMainDetails
import me.joshmelgar.weatherapp.models.domain.WindInfo
import me.joshmelgar.weatherapp.models.dto.ForecastItem

fun ForecastItem.toForecastHomeDetails(): ForecastHomeDetails {
    val weather =
        this.forecastWeather.firstOrNull() ?: throw Exception("Forecast weather list is empty")
    return ForecastHomeDetails(
        weatherType = weather.weatherType,
        description = weather.description,
        iconImageUrl = "https://openweathermap.org/img/wn/${weather.icon}@2x.png",
        temperature = this.forecastMain.temp,
        date = this.dtText
    )
}

fun ForecastItem.toForecastMainDetails(): ForecastMainDetails {
    val weather =
        this.forecastWeather.firstOrNull() ?: throw Exception("Forecast weather list is empty")
    return ForecastMainDetails(
        date = this.dtText,
        highTemp = this.forecastMain.tempMax,
        lowTemp = this.forecastMain.tempMin,
        iconImageUrl = "https://openweathermap.org/img/wn/${weather.icon}@2x.png",
        weatherType = weather.description,
        wind = WindInfo(this.wind.speed, this.wind.deg)
    )
}