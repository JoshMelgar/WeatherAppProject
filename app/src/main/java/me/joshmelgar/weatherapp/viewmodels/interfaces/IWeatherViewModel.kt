package me.joshmelgar.weatherapp.viewmodels.interfaces

import kotlinx.coroutines.flow.StateFlow
import me.joshmelgar.weatherapp.viewmodels.WeatherViewModel

interface IWeatherViewModel {
    val locationPermissionGranted: StateFlow<Boolean>
    val state: StateFlow<WeatherViewModel.State>
    fun requestCurrentLocation()
    fun updateLocationPermissionStatus(granted: Boolean)
}
