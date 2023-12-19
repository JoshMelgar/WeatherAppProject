package me.joshmelgar.weatherapp.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import android.Manifest
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale
import me.joshmelgar.weatherapp.factories.WeatherViewModelFactory
import me.joshmelgar.weatherapp.helpers.DateTimeHelper
import me.joshmelgar.weatherapp.helpers.LocationHelper
import me.joshmelgar.weatherapp.helpers.WindHelper
import me.joshmelgar.weatherapp.network.ForecastItem
import me.joshmelgar.weatherapp.network.WeatherApi
import me.joshmelgar.weatherapp.respositories.WeatherRepository
import me.joshmelgar.weatherapp.viewmodels.WeatherViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(navController: NavController, weatherViewModel: WeatherViewModel) {
   // val weatherApi = WeatherApi.retrofitWeatherService
    //val viewModel: WeatherViewModel = viewModel(factory = WeatherViewModelFactory(WeatherRepository(weatherApi)))
    val context = LocalContext.current
    val locationPermissionState =
        rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)

    Scaffold { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            when {
                locationPermissionState.status.isGranted -> {

                    weatherViewModel.updateLocationPermissionStatus(locationPermissionState.status.isGranted)

                    LocationHelper.getCurrentLocation(context) { latitude, longitude ->
                        weatherViewModel.updateLocation(latitude, longitude)
                    }

                    when (val state = weatherViewModel.state.collectAsState().value) {
                        WeatherViewModel.State.Loading -> {
                            Text("Loading...")
                        }

                        is WeatherViewModel.State.Data -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(bottom = innerPadding.calculateBottomPadding())
                                    .fillMaxSize()
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
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
                                ) {
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
                                            text = "Feels like ${state.weatherData.main.feelsLike.roundToInt()}°"
                                        )

                                        Text(
                                            text = "${state.weatherData.main.tempMax.roundToInt()}°" +
                                                    " / ${state.weatherData.main.tempMin.roundToInt()}°"
                                        )
                                    }
                                }

                                //Text("Weather: ${state.weatherData}")
                                //Text("Location: ${state.geocodingData}")
                                //Text("Forecast: ${state.forecastData.forecastList.size}")

                                ForecastColumn(forecastList = state.forecastData.forecastList)

                                //GreenRectanglesRow(list = state.forecastData.forecastList)
                            }
                        }

                        is WeatherViewModel.State.Error -> {
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

@Composable
fun ForecastColumn(forecastList: List<ForecastItem>) {
    val scrollState = rememberScrollState()

    Column(modifier = Modifier.verticalScroll(scrollState)) {
        forecastList.take(9).forEach { item ->
            Box(
                modifier = Modifier
                    .height(58.dp)
                    .fillMaxWidth()
                    .padding(3.dp)
                    .background(Color(0xFF008D75))
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = DateTimeHelper.convertDateString(item.dtText),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(16.dp)
                    )

//                    val weatherIconId = if (item.forecastWeather.isNotEmpty()) {
//                        item.forecastWeather[0].icon
//                    } else {
//                        "default"
//                    }

                    val imageUrl = "https://openweathermap.org/img/wn/${item.forecastWeather[0].icon}@2x.png"

                    //val imageResId = WeatherIconHelper.getWeatherIconResourceId(weatherIconId)

                    Row {
                        Text(
                            text = "${item.forecastMain.temp.roundToInt()}° F",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(16.dp)
                        )

                        Image(
                            painter = rememberAsyncImagePainter(imageUrl),
                            contentDescription = "Weather Icon",
                            modifier = Modifier,
                            // Add other modifiers as needed
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    }
}