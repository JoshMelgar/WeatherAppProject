package me.joshmelgar.weatherapp.respositories.extensions

import me.joshmelgar.weatherapp.models.domain.WeatherDetails
import me.joshmelgar.weatherapp.models.domain.WindInfo
import me.joshmelgar.weatherapp.models.dto.CurrentWeather

fun CurrentWeather.toWeatherDetails(): WeatherDetails {
    return WeatherDetails(
        temperature = this.main.temp,
        feelsLike = this.main.feelsLike,
        lowTemp = this.main.tempMin,
        highTemp = this.main.tempMax,
        wind = WindInfo(speed = this.wind.speed, degree = this.wind.degree)
    )
}