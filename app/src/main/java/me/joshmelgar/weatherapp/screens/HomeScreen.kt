package me.joshmelgar.weatherapp.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.joshmelgar.weatherapp.BuildConfig
import android.Manifest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale
import me.joshmelgar.weatherapp.helpers.LocationHelper
import me.joshmelgar.weatherapp.helpers.WindHelper
import me.joshmelgar.weatherapp.network.CurrentWeather
import me.joshmelgar.weatherapp.network.Forecast
import me.joshmelgar.weatherapp.network.Geocoding
import me.joshmelgar.weatherapp.network.WeatherApi
import kotlin.math.roundToInt

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val locationPermissionState =
        rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)
    val viewModel = viewModel<HomeScreenViewModel>()

    Scaffold(
        bottomBar = {
            NavigationBar{}
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            when {
                locationPermissionState.status.isGranted -> {

                    LocationHelper.getCurrentLocation(context) { latitude, longitude ->
                        viewModel.updateLocation(latitude, longitude)
                    }

                    when (val state = viewModel.state.collectAsState().value) {
                        HomeScreenViewModel.State.Loading -> {
                            Text("Loading...")
                        }

                        is HomeScreenViewModel.State.Data -> {
                            Column(
                                modifier = Modifier.fillMaxSize()
                            ){
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ){
                                        Text(
                                            text = state.geocodingData[0].cityName,
                                            fontSize = 25.sp,
                                            fontWeight = FontWeight.Bold,
                                        )

                                        Text(
                                            text = "${state.geocodingData[0].cityState}, ${state.geocodingData[0].cityCountry}",
                                            fontSize = 14.sp
                                        )
                                    }
                                }

                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ){
                                    Column(
                                        modifier = Modifier.padding(10.dp)
                                    ) {
                                        Text(
                                            text = "Wind",
                                            fontWeight = FontWeight.Bold
                                        )

                                        Text(
                                            text = WindHelper.getWindDirection(state.weatherData.wind.degree)
                                        )

                                        Text(
                                            text = "${state.weatherData.wind.speed} mph"
                                        )
                                    }

                                    Column(
                                        modifier = Modifier.padding(10.dp)
                                    ) {
                                        Text(
                                            text = "Feels like ${state.weatherData.main.feelsLike.roundToInt()}"
                                        )

                                        Text(
                                            text = "${state.weatherData.main.tempMax.roundToInt()} " +
                                                    "/ ${state.weatherData.main.tempMin.roundToInt()}"
                                        )
                                    }
                                }
                                Text("Weather: ${state.weatherData}")
                                Text("Location: ${state.geocodingData}")
                                Text("Forecast: ${state.forecastData}")
                            }
                        }

                        is HomeScreenViewModel.State.Error -> {
                            Text("Error: ${state.error}")
                        }
                    }
                }

                //if location is denied or requested
                locationPermissionState.status.shouldShowRationale ||
                        !locationPermissionState.status.isGranted -> {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            "This app needs your location to display the weather."
                        )
                        Button(onClick = { locationPermissionState.launchPermissionRequest() }) {
                            Text("Grant Permission")
                        }
                    }
                }
                else -> {
                    Text("Location permission denied")
                }
            }
        }
    }
}

class HomeScreenViewModel : ViewModel() {
    sealed class State {
        data object Loading : State()

        data class Error(val error: Exception) : State()
        data class Data(val weatherData: CurrentWeather,
                        val geocodingData: List<Geocoding>,
                        val forecastData: Forecast) : State()
    }

    //initialize initial state as "Loading"
    private var _state = MutableStateFlow<State>(State.Loading)
    val state = _state.asStateFlow()

    private val apiKey = BuildConfig.API_KEY_WEATHER

    fun updateLocation(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            try {
                val weatherData = WeatherApi.retrofitWeatherService.getWeather(latitude, longitude, "imperial", apiKey)
                val forecastData = WeatherApi.retrofitWeatherService.getForecast(latitude, longitude, apiKey)
                val geocodingData = WeatherApi.retrofitGeocodingService.getGeocoding(latitude, longitude, 1, apiKey)
                _state.value = State.Data(weatherData, geocodingData, forecastData)
            } catch (e: Exception) {
                _state.value = State.Error(e)
            }
        }
    }
}