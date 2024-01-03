package me.joshmelgar.weatherapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.joshmelgar.weatherapp.BuildConfig
import me.joshmelgar.weatherapp.managers.LocationManager
import me.joshmelgar.weatherapp.models.domain.ForecastHomeDetails
import me.joshmelgar.weatherapp.models.domain.ForecastMainDetails
import me.joshmelgar.weatherapp.models.domain.LocationInfo
import me.joshmelgar.weatherapp.models.domain.WeatherDetails
import me.joshmelgar.weatherapp.respositories.WeatherRepository
import me.joshmelgar.weatherapp.utils.Result
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val locationManager: LocationManager
) : ViewModel() {
    // Property to hold the permission status
    private val _locationPermissionGranted = MutableStateFlow(false)
    val locationPermissionGranted = _locationPermissionGranted.asStateFlow()

    fun updateLocationPermissionStatus(granted: Boolean) {
        _locationPermissionGranted.value = granted
    }

    sealed class State {
        data object Loading : State()

        data class Error(val error: Exception) : State()
        data class Data(
            val locationInfo: LocationInfo,
            val weatherDetails: WeatherDetails,
            val forecastHomeScreenDetails: List<ForecastHomeDetails>,
            val forecastScreenDetails: List<ForecastMainDetails>
        ) : State()
    }

    //initialize initial state as "Loading"
    private var _state = MutableStateFlow<State>(State.Loading)
    val state = _state.asStateFlow()

    private val apiKey = BuildConfig.API_KEY_WEATHER

    fun updateLocation(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            try {
                val locationResult = repository.getGeocoding(latitude, longitude, 1, apiKey)
                val weatherResult = repository.getWeather(latitude, longitude, "imperial", apiKey)
                val forecastHomeResult = repository.getForecastHomeScreenWeatherList(latitude, longitude, "imperial", apiKey)
                val forecastDetailResult = repository.getForecastScreenWeatherList(latitude, longitude, "imperial", apiKey)

                // if all checks are successful
                if (locationResult is Result.Success && weatherResult is Result.Success &&
                    forecastHomeResult is Result.Success && forecastDetailResult is Result.Success) {

                    _state.value = State.Data(
                        locationInfo = locationResult.data,
                        weatherDetails = weatherResult.data,
                        forecastHomeScreenDetails = forecastHomeResult.data,
                        forecastScreenDetails = forecastDetailResult.data
                    )
                } else {
                    //if there are any errors
                    val exception = (locationResult as? Result.Error)?.exception
                        ?: (weatherResult as? Result.Error)?.exception
                        ?: (forecastHomeResult as? Result.Error)?.exception
                        ?: (forecastDetailResult as? Result.Error)?.exception
                        ?: Exception("Unknown error occurred")
                    _state.value = State.Error(exception)
                }
            } catch (e: Exception) {
                _state.value = State.Error(e)
            }
        }
    }

    fun updateLocation() {
        viewModelScope.launch {
            try {
                val (latitude, longitude) = locationManager.getCurrentLocation()
                updateLocation(latitude, longitude)
            } catch (e: Exception) {
                _state.value = State.Error(e)
            }
        }
    }
}