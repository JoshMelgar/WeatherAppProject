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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.flow.MutableStateFlow
import me.joshmelgar.weatherapp.helpers.WindHelper
import me.joshmelgar.weatherapp.models.domain.ForecastHomeDetails
import me.joshmelgar.weatherapp.models.domain.ForecastMainDetails
import me.joshmelgar.weatherapp.models.domain.LocationInfo
import me.joshmelgar.weatherapp.models.domain.WeatherDetails
import me.joshmelgar.weatherapp.models.domain.WindInfo
import me.joshmelgar.weatherapp.viewmodels.WeatherViewModel
import me.joshmelgar.weatherapp.viewmodels.interfaces.IWeatherViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(weatherViewModel: IWeatherViewModel) {
    val locationPermissionState =
        rememberPermissionStateSafe(permission = Manifest.permission.ACCESS_FINE_LOCATION)

    Scaffold { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            when {
                locationPermissionState.status.isGranted -> {

                    weatherViewModel.updateLocationPermissionStatus(locationPermissionState.status.isGranted)

                    weatherViewModel.requestCurrentLocation()

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
                                            text = state.locationInfo.cityName,
                                            fontSize = 25.sp,
                                            fontWeight = FontWeight.Bold,
                                        )

                                        Text(
                                            text = "${state.locationInfo.cityState}, ${state.locationInfo.cityCountry}",
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
                                            text = WindHelper().getWindDirection(state.weatherDetails.wind.degree)
                                        )

                                        Text(
                                            text = "${state.weatherDetails.wind.speed} mph"
                                        )
                                    }

                                    Column(
                                        modifier = Modifier.padding(10.dp)
                                    ) {
                                        Text(
                                            text = "Feels like ${state.weatherDetails.feelsLike.roundToInt()}°"
                                        )

                                        Text(
                                            text = "${state.weatherDetails.highTemp.roundToInt()}°" +
                                                    " / ${state.weatherDetails.lowTemp.roundToInt()}°"
                                        )
                                    }
                                }
                                ForecastColumn(forecastList = state.forecastHomeScreenDetails)
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
fun ForecastColumn(forecastList: List<ForecastHomeDetails>) {
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
                        text = convertDateString(item.date),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(16.dp)
                    )

                    val imageUrl = "https://openweathermap.org/img/wn/${item.icon}@2x.png"

                    Row {
                        Text(
                            text = "${item.temperature.roundToInt()}° F",
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

fun convertDateString(input: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val outputFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

    val date = inputFormat.parse(input)
    return outputFormat.format(date as Date)
}

@ExperimentalPermissionsApi
@Composable
fun rememberPermissionStateSafe(permission: String) = when {
    LocalInspectionMode.current -> remember {
        object : PermissionState {
            override val permission = permission
            override val status = PermissionStatus.Granted
            override fun launchPermissionRequest() = Unit
        }
    }

    else -> rememberPermissionState(permission)
}

// ===================================================
// == PREVIEW CODE SECTION
// ===================================================

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val weatherViewModel = MockWeatherViewModel()

    HomeScreen(weatherViewModel = weatherViewModel)
}

@Preview(showBackground = true)
@Composable
fun ForecastColumnPreview() {
    val sampleForecastList = listOf(
        ForecastHomeDetails(
            weatherType = "sun is out",
            description = "very sunny",
            icon = "04n",
            temperature = 88.3,
            date = "12-12-1992 00:00:00"
        )
    )

    ForecastColumn(forecastList = sampleForecastList)
}

class MockWeatherViewModel : IWeatherViewModel {
    override val locationPermissionGranted: MutableStateFlow<Boolean>
        get() = MutableStateFlow(true)

    override val state: MutableStateFlow<WeatherViewModel.State>
        get() = MutableStateFlow(
            WeatherViewModel.State.Data(
                locationInfo = LocationInfo(
                    cityName = "The Best City",
                    cityState = "The Best State",
                    cityCountry = "The Best Country"
                ),
                weatherDetails = WeatherDetails(
                    temperature = 80.5,
                    feelsLike = 900.0,
                    lowTemp = 1.1,
                    highTemp = 18927.4,
                    wind = WindInfo(3.2, 2)

                ),
                forecastHomeScreenDetails = listOf(
                    ForecastHomeDetails(
                        weatherType = "snow",
                        description = "snowing hard",
                        icon = "04n",
                        temperature = 9.4,
                        date = "12-12-2009 00:00:00"
                    ),
                ),
                forecastScreenDetails = listOf(
                    ForecastMainDetails(
                        date = "12-12-2009 00:00:00",
                        highTemp = 100.4,
                        lowTemp = 44.6,
                        icon = "04n",
                        weatherType = "very hot",
                        wind = WindInfo(10.2, 4)
                    )
                )
            )
        )

    override fun requestCurrentLocation() {

    }

    override fun updateLocationPermissionStatus(granted: Boolean) {
    }
}