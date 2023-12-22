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
import me.joshmelgar.weatherapp.viewmodels.interfaces.IWeatherViewModel
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val locationManager: LocationManager
) : ViewModel(), IWeatherViewModel {
    // Property to hold the permission status
    private val _locationPermissionGranted = MutableStateFlow(false)
    override val locationPermissionGranted = _locationPermissionGranted.asStateFlow()

    override fun updateLocationPermissionStatus(granted: Boolean) {
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
    override val state = _state.asStateFlow()

    private val apiKey = BuildConfig.API_KEY_WEATHER

    private fun updateLocation(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            try {
                val weatherDetails = repository.getWeather(latitude, longitude, "imperial", apiKey)
                val locationInfo = repository.getGeocoding(latitude, longitude, 1, apiKey)
                val forecastHomeScreenDetails = repository.getForecastHomeScreenWeatherList(
                    latitude,
                    longitude,
                    "imperial",
                    apiKey
                )
                val forecastScreenDetails =
                    repository.getForecastScreenWeatherList(latitude, longitude, "imperial", apiKey)
                _state.value = State.Data(
                    locationInfo,
                    weatherDetails,
                    forecastHomeScreenDetails,
                    forecastScreenDetails
                )
            } catch (e: Exception) {
                _state.value = State.Error(e)
            }
        }
    }

    override fun requestCurrentLocation() {
        locationManager.getCurrentLocation { latitude, longitude ->
            updateLocation(latitude, longitude)
        }
    }
}