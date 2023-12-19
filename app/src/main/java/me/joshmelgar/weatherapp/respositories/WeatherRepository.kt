package me.joshmelgar.weatherapp.respositories

import me.joshmelgar.weatherapp.network.CurrentWeather
import me.joshmelgar.weatherapp.network.Forecast
import me.joshmelgar.weatherapp.network.Geocoding
import me.joshmelgar.weatherapp.network.WeatherApi

class WeatherRepository(private val weatherApi: WeatherApi) {

//    private val weatherService = WeatherApi.retrofitWeatherService
//    private val geocodingService = WeatherApi.retrofitGeocodingService
    suspend fun getWeather(latitude: Double, longitude: Double, units: String, apiKey: String): CurrentWeather {
        return weatherApi.retrofitWeatherService.getWeather(latitude, longitude, units, apiKey)
    }

    suspend fun getGeocoding(latitude: Double, longitude: Double, callLimit: Int, apiKey: String): List<Geocoding> {
        return weatherApi.retrofitGeocodingService.getGeocoding(latitude, longitude, callLimit, apiKey)
    }

    suspend fun getForecast(latitude: Double, longitude: Double, units: String, apiKey: String): Forecast {
        return weatherApi.retrofitWeatherService.getForecast(latitude, longitude, units, apiKey)
    }
}