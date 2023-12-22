package me.joshmelgar.weatherapp.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import me.joshmelgar.weatherapp.helpers.WindHelper
import me.joshmelgar.weatherapp.models.domain.WindInfo
import me.joshmelgar.weatherapp.models.domain.DailyForecast
import me.joshmelgar.weatherapp.models.domain.ForecastMainDetails
import me.joshmelgar.weatherapp.viewmodels.WeatherViewModel
import me.joshmelgar.weatherapp.viewmodels.interfaces.IWeatherViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun ForecastScreen(weatherViewModel: IWeatherViewModel) {
    val permissionGranted = weatherViewModel.locationPermissionGranted.collectAsState()

    Scaffold { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            when {
                permissionGranted.value -> {
                    weatherViewModel.requestCurrentLocation()

                    when (val state = weatherViewModel.state.collectAsState().value) {
                        WeatherViewModel.State.Loading -> {
                            Text("Loading...")
                        }

                        is WeatherViewModel.State.Data -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(bottom = innerPadding.calculateBottomPadding())
                                    .fillMaxSize()
                            ) {
                                FiveDayForecastColumn(forecastList = state.forecastScreenDetails)
                            }
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
fun FiveDayForecastColumn(forecastList: List<ForecastMainDetails>) {
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

                    val imageUrl = "https://openweathermap.org/img/wn/${dailyForecast.icon}@2x.png"

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(imageUrl),
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

        //look into using the weatherdetails domain model. it is missing icondesc
        //icon, and something else maybe but look into it. One more domain
    }
}

fun processForecastData(forecastList: List<ForecastMainDetails>): List<DailyForecast> {
    val dailyForecasts = mutableMapOf<String, MutableList<ForecastMainDetails>>()

    //groups s the forecast items by day
    forecastList.forEach { forecastItem ->
        val dayKey = getDayOfWeekName(forecastItem.date) ?: ""
        dailyForecasts.getOrPut(dayKey) { mutableListOf() }.add(forecastItem)
    }

    // Calculate averages and most common icon for each day
    val dailyForecastData = dailyForecasts.map { (day, forecasts) ->
        val avgHighTemp = forecasts.maxOf { it.highTemp }
        val avgLowTemp = forecasts.minOf { it.lowTemp }
        val avgWindSpeed = forecasts.map { it.wind.speed }.average()
        val mostCommonIcon = forecasts.groupBy { it.icon }
            .maxByOrNull { (_, items) -> items.size }?.key ?: "no icon?"
        val mostCommonIconDesc = forecasts.groupBy { it.weatherType }
            .maxByOrNull { (_, items) -> items.size }?.key ?: "no desc?"
        val avgWindDeg = forecasts.map { it.wind.degree }.average()

        DailyForecast(
            day, avgHighTemp, avgLowTemp,
            mostCommonIcon, mostCommonIconDesc,
            WindInfo(avgWindSpeed, avgWindDeg.roundToInt())
        )
    }

    //return only the first 5 days
    return dailyForecastData.take(5)
}

fun getDayOfWeekName(input: String): String? {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val outputFormat = SimpleDateFormat("EEEE, MMMM dd", Locale.getDefault())

    return try {
        val date = inputFormat.parse(input)
        date?.let { outputFormat.format(it) }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

// ===================================================
// == PREVIEW CODE SECTION
// ===================================================

@Preview(showBackground = true)
@Composable
fun ForecastScreenPreview() {
    val weatherViewModel = MockWeatherViewModel()

    ForecastScreen(weatherViewModel = weatherViewModel)
}

@Preview(showBackground = true)
@Composable
fun FiveDayForecastColumnPreview() {
    val sampleForecastList = listOf(
        ForecastMainDetails(
            date = "12-12-1992 00:00:00",
            highTemp = 80.4,
            lowTemp = 10.2,
            icon = "04n",
            weatherType = "snow",
            wind = WindInfo(10.4, 4)
        )
    )

    FiveDayForecastColumn(forecastList = sampleForecastList)
}