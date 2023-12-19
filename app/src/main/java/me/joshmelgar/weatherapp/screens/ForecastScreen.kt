package me.joshmelgar.weatherapp.screens

import android.text.Layout
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import me.joshmelgar.weatherapp.helpers.DateTimeHelper
import me.joshmelgar.weatherapp.helpers.ForecastHelper.Companion.processForecastData
import me.joshmelgar.weatherapp.helpers.LocationHelper
import me.joshmelgar.weatherapp.helpers.WeatherIconHelper
import me.joshmelgar.weatherapp.helpers.WindHelper
import me.joshmelgar.weatherapp.models.DailyForecast
import me.joshmelgar.weatherapp.network.ForecastItem
import me.joshmelgar.weatherapp.viewmodels.WeatherViewModel
import java.util.Calendar
import kotlin.math.roundToInt

@Composable
fun ForecastScreen(navController: NavController, weatherViewModel: WeatherViewModel) {
    val context = LocalContext.current
    val permissionGranted = weatherViewModel.locationPermissionGranted.collectAsState()

    Scaffold { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            when {
                permissionGranted.value -> {
                    LocationHelper.getCurrentLocation(context) { latitude, longitude ->
                        weatherViewModel.updateLocation(latitude, longitude)
                    }

                    when (val state = weatherViewModel.state.collectAsState().value) {
                        WeatherViewModel.State.Loading -> {
                            Text("Loading...")
                        }

                        is WeatherViewModel.State.Data -> {
                            //Text("Weather: ${state.weatherData}")
                            //Text("Location: ${state.geocodingData}")
                            //Text("Forecast: ${state.forecastData.forecastList.size}")
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(bottom = innerPadding.calculateBottomPadding())
                                    .fillMaxSize()
                            ) {
                                FiveDayForecastColumn(forecastList = state.forecastData.forecastList)
                            }

                            //GreenRectanglesRow(list = state.forecastData.forecastList)
                        }

                        is WeatherViewModel.State.Error -> {
                            Text("Error: ${state.error}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FiveDayForecastColumn(forecastList: List<ForecastItem>) {
    val scrollState = rememberScrollState()
    val dailyForecasts = processForecastData(forecastList)

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
                            text = "Hi Temp: ${dailyForecast.lowTemp.roundToInt()}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .padding(5.dp)
                        )

                        Text(
                            text = "Low Temp: ${dailyForecast.highTemp.roundToInt()}",
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
                            text = "${dailyForecast.windSpeed.roundToInt()} mph",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )

                        Text(
                            text = WindHelper.getWindDirection(dailyForecast.windDeg.roundToInt()),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    val weatherIconId = dailyForecast.icon.ifEmpty {
                        "default"
                    }

                    val imageResId = WeatherIconHelper.getWeatherIconResourceId(weatherIconId)

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = imageResId),
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