package me.joshmelgar.weatherapp.respositories

import me.joshmelgar.weatherapp.models.domain.ForecastHomeDetails
import me.joshmelgar.weatherapp.models.domain.ForecastMainDetails
import me.joshmelgar.weatherapp.models.domain.LocationInfo
import me.joshmelgar.weatherapp.models.domain.WeatherDetails
import me.joshmelgar.weatherapp.models.domain.WindInfo
import me.joshmelgar.weatherapp.models.dto.CurrentWeather
import me.joshmelgar.weatherapp.models.dto.ForecastItem
import me.joshmelgar.weatherapp.models.dto.Geocoding
import me.joshmelgar.weatherapp.network.WeatherApiService
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val weatherApiService: WeatherApiService
) {

    suspend fun getGeocoding(
        latitude: Double,
        longitude: Double,
        callLimit: Int,
        apiKey: String
    ): LocationInfo {
        val geocodingResults =
            weatherApiService.getGeocoding(latitude, longitude, callLimit, apiKey)
        // take first from list for simplicity (change later?)
        return geocodingResults.first().toLocationInfo()
    }

    suspend fun getWeather(
        latitude: Double,
        longitude: Double,
        units: String,
        apiKey: String
    ): WeatherDetails {
        val currentWeatherDto = weatherApiService.getWeather(latitude, longitude, units, apiKey)
        return currentWeatherDto.toWeatherDetails()
    }

    suspend fun getForecastHomeScreenWeatherList(
        latitude: Double,
        longitude: Double,
        units: String,
        apiKey: String
    ): List<ForecastHomeDetails> {
        val forecastDto = weatherApiService.getForecast(latitude, longitude, units, apiKey)
        return forecastDto.forecastList.map { it.toForecastHomeDetails() }
    }

    suspend fun getForecastScreenWeatherList(
        latitude: Double,
        longitude: Double,
        units: String,
        apiKey: String
    ): List<ForecastMainDetails> {
        val forecastDto = weatherApiService.getForecast(latitude, longitude, units, apiKey)
        return forecastDto.forecastList.map { it.toForecastMainDetails() }
    }

    private fun CurrentWeather.toWeatherDetails(): WeatherDetails {
        return WeatherDetails(
            temperature = this.main.temp,
            feelsLike = this.main.feelsLike,
            lowTemp = this.main.tempMin,
            highTemp = this.main.tempMax,
            wind = WindInfo(speed = this.wind.speed, degree = this.wind.degree)
        )
    }

    private fun Geocoding.toLocationInfo(): LocationInfo {
        return LocationInfo(
            cityName = this.cityName,
            cityState = this.cityState,
            cityCountry = this.cityCountry
        )
    }

    private fun ForecastItem.toForecastHomeDetails(): ForecastHomeDetails {
        val weather =
            this.forecastWeather.firstOrNull() ?: throw Exception("Forecast weather list is empty")
        return ForecastHomeDetails(
            weatherType = weather.weatherType,
            description = weather.description,
            icon = weather.icon,
            temperature = this.forecastMain.temp,
            date = this.dtText
        )
    }

    private fun ForecastItem.toForecastMainDetails(): ForecastMainDetails {
        val weather =
            this.forecastWeather.firstOrNull() ?: throw Exception("Forecast weather list is empty")
        return ForecastMainDetails(
            date = this.dtText,
            highTemp = this.forecastMain.tempMax,
            lowTemp = this.forecastMain.tempMin,
            icon = weather.icon,
            weatherType = weather.description,
            wind = WindInfo(this.wind.speed, this.wind.deg)
        )
    }
}