package me.joshmelgar.weatherapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import me.joshmelgar.weatherapp.helpers.WindHelper
import me.joshmelgar.weatherapp.models.domain.WindInfo
import me.joshmelgar.weatherapp.models.domain.DailyForecast
import me.joshmelgar.weatherapp.models.domain.LocationInfo
import me.joshmelgar.weatherapp.models.domain.ViewModelState
import me.joshmelgar.weatherapp.models.domain.WeatherDetails
import me.joshmelgar.weatherapp.state.State
import me.joshmelgar.weatherapp.viewmodels.WeatherViewModel
import kotlin.math.roundToInt

@Composable
fun ForecastScreen(weatherViewModel: WeatherViewModel) {
    val permissionGranted = weatherViewModel.locationPermissionGranted.collectAsState()
    val state = weatherViewModel.state.collectAsState().value

    Scaffold { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            when {
                permissionGranted.value -> {
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
                            null,
                            state.dailyForecast,
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
                    ForecastScreenWrapper(state = uiState, innerPadding = innerPadding)
                }
            }
        }
    }
}

@Composable
fun FiveDayForecastColumn(dailyForecasts: List<DailyForecast>) {
    val scrollState = rememberScrollState()

    Column(modifier = Modifier.verticalScroll(scrollState)) {
        dailyForecasts.forEach { dailyForecast ->
            Box(
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxSize()
                    .padding(3.dp)
                    .background(Color(0xFF008D75))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Hi Temp: ${dailyForecast.highTemp.roundToInt()}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .padding(5.dp)
                        )

                        Text(
                            text = "Low Temp: ${dailyForecast.lowTemp.roundToInt()}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .padding(5.dp)
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = dailyForecast.day,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )

                        Text(
                            text = "Wind Speed",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )

                        Text(
                            text = "${dailyForecast.wind.speed.roundToInt()} mph",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )

                        Text(
                            text = WindHelper().getWindDirection(dailyForecast.wind.degree),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(dailyForecast.iconImageUrl),
                            contentDescription = "Weather Icon",
                            modifier = Modifier.size(48.dp)
                        )

                        Text(
                            text = dailyForecast.iconDesc,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ForecastScreenWrapper(state: ViewModelState, innerPadding: PaddingValues) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when {
            state.isLoading -> {
                // Display a loading indicator
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(modifier = Modifier
                        .semantics {
                            this.testTagsAsResourceId = true
                        }
                        .testTag("ProgressIndicator"))
                }
            }

            state.error != null -> {
                // Display an error message
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text("Error: ${state.error.message}", color = Color.Red)
                }
            }

            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = innerPadding.calculateBottomPadding())
                        .fillMaxSize()
                ) {
                    state.dailyForecast?.let { FiveDayForecastColumn(dailyForecasts = it) }
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
fun ForecastScreenPreviewLoading() {
    Scaffold { innerPadding ->
        ForecastScreenWrapper(
            state = ViewModelState(isLoading = true, null, null, null, null, null),
            innerPadding = PaddingValues(all = 16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ForecastScreenPreviewError() {
    Scaffold { innerPadding ->
        ForecastScreenWrapper(
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
fun ForecastScreenPreviewDataState() {
    Scaffold { innerPadding ->
        ForecastScreenWrapper(
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
                forecastHomeScreenDetails = null,
                dailyForecast = null,
                error = null,
            ),
            innerPadding = PaddingValues(all = 16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FiveDayForecastColumnPreview() {
    val sampleForecastList = listOf(
        DailyForecast(
            day = "Monday",
            highTemp = 80.4,
            lowTemp = 10.2,
            iconImageUrl = "https://openweathermap.org/img/wn/01d@2x.png",
            iconDesc = "snow",
            wind = WindInfo(10.4, 4)
        )
    )

    FiveDayForecastColumn(dailyForecasts = sampleForecastList)
}