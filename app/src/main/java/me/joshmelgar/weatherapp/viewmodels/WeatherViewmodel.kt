package me.joshmelgar.weatherapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.joshmelgar.weatherapp.BuildConfig
import me.joshmelgar.weatherapp.network.CurrentWeather
import me.joshmelgar.weatherapp.network.Forecast
import me.joshmelgar.weatherapp.network.Geocoding
import me.joshmelgar.weatherapp.respositories.WeatherRepository

class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {
    // Property to hold the permission status
    private val _locationPermissionGranted = MutableStateFlow(false)
    val locationPermissionGranted = _locationPermissionGranted.asStateFlow()

    fun updateLocationPermissionStatus(isGranted: Boolean) {
        _locationPermissionGranted.value = isGranted
    }

    sealed class State {
        data object Loading : State()

        data class Error(val error: Exception) : State()
        data class Data(
            val weatherData: CurrentWeather,
            val geocodingData: List<Geocoding>,
            val forecastData: Forecast
        ) : State()
    }

    //initialize initial state as "Loading"
    private var _state = MutableStateFlow<State>(State.Loading)
    val state = _state.asStateFlow()

    private val apiKey = BuildConfig.API_KEY_WEATHER

    fun updateLocation(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            try {
                val weatherData = repository.getWeather(
                    latitude, longitude, "imperial", apiKey
                )
                val forecastData = repository.getForecast(
                    latitude, longitude, "imperial", apiKey
                )
                val geocodingData = repository.getGeocoding(
                    latitude, longitude, 1, apiKey
                )
                _state.value = State.Data(weatherData, geocodingData, forecastData)
            } catch (e: Exception) {
                _state.value = State.Error(e)
            }
        }
    }
}