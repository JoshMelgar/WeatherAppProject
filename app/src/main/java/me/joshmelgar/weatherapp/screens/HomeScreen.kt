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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale
import me.joshmelgar.weatherapp.helpers.WindHelper
import me.joshmelgar.weatherapp.models.domain.DailyForecast
import me.joshmelgar.weatherapp.models.domain.ForecastHomeDetails
import me.joshmelgar.weatherapp.models.domain.IconInfo
import me.joshmelgar.weatherapp.models.domain.ViewModelState
import me.joshmelgar.weatherapp.models.domain.LocationInfo
import me.joshmelgar.weatherapp.models.domain.WeatherDetails
import me.joshmelgar.weatherapp.models.domain.WindInfo
import me.joshmelgar.weatherapp.state.State
import me.joshmelgar.weatherapp.viewmodels.WeatherViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(weatherViewModel: WeatherViewModel) {
    val locationPermissionState =
        rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)
    val state = weatherViewModel.state.collectAsState().value

    Scaffold { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            when {
                locationPermissionState.status.isGranted -> {
                    weatherViewModel.updateLocationPermissionStatus(locationPermissionState.status.isGranted)
                    weatherViewModel.updateLocation()

                    val uiState = when (state) {
                        is State.Loading -> ViewModelState(
                            isLoading = true,
                            null,
                            null,
                            null,
                            null,
                            null
                        )

                        is State.Data -> ViewModelState(
                            isLoading = false,
                            state.locationInfo,
                            state.weatherDetails,
                            state.forecastHomeScreenDetails,
                            null,
                            null
                        )

                        is State.Error -> ViewModelState(
                            isLoading = false,
                            null,
                            null,
                            null,
                            null,
                            state.error
                        )
                    }
                    HomeScreenWrapper(uiState, innerPadding)
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

@OptIn(ExperimentalComposeUiApi::class)
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
                    .semantics { this.testTagsAsResourceId = true }
                    .testTag("ForecastBox")
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item.date?.let {
                        Text(
                            text = it,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    Row {
                        Text(
                            text = "${item.temperature?.roundToInt()}° F",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(16.dp)
                        )

                        Image(
                            painter = rememberAsyncImagePainter(item.iconImageUrl),
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HomeScreenWrapper(state: ViewModelState, innerPadding: PaddingValues) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when {
            state.isLoading == true -> {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(modifier = Modifier
                        .semantics {
                            this.testTagsAsResourceId = true
                        }
                        .testTag("ProgressIndicator"))
                }
            }

            state.error != null -> {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text("Error: ${state.error.message}", color = Color.Red)
                }
            }

            else -> {
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
                            state.locationInfo?.let {
                                it.cityName?.let { it1 ->
                                    Text(
                                        text = it1,
                                        fontSize = 25.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            }

                            Text(
                                text = "${state.locationInfo?.cityState}, ${state.locationInfo?.cityCountry}",
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
                                text = WindHelper().getWindDirection(state.weatherDetails?.wind?.degree)
                            )

                            Text(
                                text = "${state.weatherDetails?.wind?.speed} mph"
                            )
                        }

                        Column(
                            modifier = Modifier.padding(10.dp)
                        ) {
                            Text(
                                text = "Feels like ${state.weatherDetails?.feelsLike?.roundToInt()}°"
                            )

                            Text(
                                text = "${state.weatherDetails?.highTemp?.roundToInt()}°" +
                                        " / ${state.weatherDetails?.lowTemp?.roundToInt()}°"
                            )
                        }
                    }
                    state.forecastHomeScreenDetails?.let { ForecastColumn(forecastList = it) }
                }
            }
        }
    }
}

// ===================================================
// == PREVIEW CODE SECTION
// ===================================================

@Preview(showBackground = true)
@Composable
fun HomeScreenPreviewLoading() {
    Scaffold { innerPadding ->
        HomeScreenWrapper(
            state = ViewModelState(isLoading = true, null, null, null, null, null),
            innerPadding = PaddingValues(all = 16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreviewError() {
    Scaffold { innerPadding ->
        HomeScreenWrapper(
            state = ViewModelState(
                isLoading = false,
                null,
                null,
                null,
                null,
                Exception("Mock Error")
            ),
            innerPadding = PaddingValues(all = 16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreviewDataState() {
    Scaffold { innerPadding ->
        HomeScreenWrapper(
            state = ViewModelState(
                isLoading = false,
                locationInfo = LocationInfo(
                    cityName = "Sample City",
                    cityState = "Sample State",
                    cityCountry = "Sample Country"
                ),
                weatherDetails = WeatherDetails(
                    temperature = 70.0,
                    feelsLike = 68.0,
                    lowTemp = 10.0,
                    highTemp = 77.4,
                    wind = WindInfo(speed = 2.0, degree = 2)
                ),
                forecastHomeScreenDetails = listOf(
                    ForecastHomeDetails(
                        weatherType = "Sunny",
                        description = "Clear sky",
                        iconImageUrl = "https://openweathermap.org/img/wn/01d@2x.png",
                        temperature = 75.0,
                        date = "2023-07-21 12:00:00"
                    ),
                ),
                dailyForecast = listOf(
                    DailyForecast(
                        day = "Monday",
                        highTemp = 40.0,
                        lowTemp = 22.44,
                        icon = IconInfo("https://openweathermap.org/img/wn/01d@2x.png", "sunny"),
                        wind = WindInfo(4.5, 180)
                    )
                ),
                error = null,
            ),
            innerPadding = PaddingValues(all = 16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ForecastColumnPreview() {
    val sampleForecastList = listOf(
        ForecastHomeDetails(
            weatherType = "sun is out",
            description = "very sunny",
            iconImageUrl = "https://openweathermap.org/img/wn/04n@2x.png",
            temperature = 88.3,
            date = "12-12-1992 00:00:00"
        )
    )

    ForecastColumn(forecastList = sampleForecastList)
}