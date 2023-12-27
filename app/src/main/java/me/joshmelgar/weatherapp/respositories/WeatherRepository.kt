package me.joshmelgar.weatherapp.respositories

import me.joshmelgar.weatherapp.models.domain.ForecastHomeDetails
import me.joshmelgar.weatherapp.models.domain.ForecastMainDetails
import me.joshmelgar.weatherapp.respositories.extensions.toForecastMainDetails
import me.joshmelgar.weatherapp.respositories.extensions.toForecastHomeDetails
import me.joshmelgar.weatherapp.respositories.extensions.toWeatherDetails
import me.joshmelgar.weatherapp.respositories.extensions.toLocationInfo
import me.joshmelgar.weatherapp.models.domain.LocationInfo
import me.joshmelgar.weatherapp.models.domain.WeatherDetails
import me.joshmelgar.weatherapp.network.WeatherApiService
import me.joshmelgar.weatherapp.utils.Result
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val weatherApiService: WeatherApiService
) {

    suspend fun getGeocoding(
        latitude: Double,
        longitude: Double,
        callLimit: Int,
        apiKey: String
    ): Result<LocationInfo> {
        return try {
            val geocodingResults = weatherApiService.getGeocoding(latitude, longitude, callLimit, apiKey)
            val locationInfo = geocodingResults.first().toLocationInfo()
            Result.Success(locationInfo)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun getWeather(
        latitude: Double,
        longitude: Double,
        units: String,
        apiKey: String
    ): Result<WeatherDetails> {
        return try {
            val currentWeatherDto = weatherApiService.getWeather(latitude, longitude, units, apiKey)
            Result.Success(currentWeatherDto.toWeatherDetails())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun getForecastHomeScreenWeatherList(
        latitude: Double,
        longitude: Double,
        units: String,
        apiKey: String
    ): Result<List<ForecastHomeDetails>> {
        return try {
            val forecastDto = weatherApiService.getForecast(latitude, longitude, units, apiKey)
            val forecastDetails = forecastDto.forecastList.map { it.toForecastHomeDetails() }
            Result.Success(forecastDetails)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun getForecastScreenWeatherList(
        latitude: Double,
        longitude: Double,
        units: String,
        apiKey: String
    ): Result<List<ForecastMainDetails>> {
        return try {
            val forecastDto = weatherApiService.getForecast(latitude, longitude, units, apiKey)
            val forecastDetails = forecastDto.forecastList.map { it.toForecastMainDetails() }
            Result.Success(forecastDetails)
        } catch (e: Exception){
            Result.Error(e)
        }
    }
}