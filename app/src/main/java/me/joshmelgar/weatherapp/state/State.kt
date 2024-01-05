package me.joshmelgar.weatherapp.state

import me.joshmelgar.weatherapp.models.domain.DailyForecast
import me.joshmelgar.weatherapp.models.domain.ForecastHomeDetails
import me.joshmelgar.weatherapp.models.domain.LocationInfo
import me.joshmelgar.weatherapp.models.domain.WeatherDetails

sealed class State {
    data object Loading : State()

    data class Error(val error: Exception) : State()
    data class Data(
        val locationInfo: LocationInfo,
        val weatherDetails: WeatherDetails,
        val forecastHomeScreenDetails: List<ForecastHomeDetails>,
        val dailyForecast: List<DailyForecast>
    ) : State()
}