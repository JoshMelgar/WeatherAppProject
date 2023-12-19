package me.joshmelgar.weatherapp.helpers

import me.joshmelgar.weatherapp.models.DailyForecast
import me.joshmelgar.weatherapp.network.ForecastItem

class ForecastHelper {

    companion object {
        fun processForecastData(forecastList: List<ForecastItem>): List<DailyForecast> {
            val dailyForecasts = mutableMapOf<String, MutableList<ForecastItem>>()

            //groups s the forecast items by day
            forecastList.forEach { forecastItem ->
                val dayKey = DateTimeHelper.getDayOfWeekName(forecastItem.dtText) ?: ""
                dailyForecasts.getOrPut(dayKey) { mutableListOf() }.add(forecastItem)
            }

            // Calculate averages and most common icon for each day
            val dailyForecastData = dailyForecasts.map { (day, forecasts) ->
                val avgHighTemp = forecasts.maxOf { it.forecastMain.tempMax }
                val avgLowTemp = forecasts.minOf { it.forecastMain.tempMin }
                val avgWindSpeed = forecasts.map { it.wind.speed }.average()
                val mostCommonIcon = forecasts.groupBy { it.forecastWeather[0].icon }
                    .maxByOrNull { (_, items) -> items.size }?.key ?: "no icon?"
                val mostCommonIconDesc = forecasts.groupBy { it.forecastWeather[0].description }
                    .maxByOrNull { (_, items) -> items.size }?.key ?: "no desc?"
                val avgWindDeg = forecasts.map { it.wind.deg }.average()

                DailyForecast(
                    day, avgHighTemp, avgLowTemp, avgWindSpeed,
                    mostCommonIcon, mostCommonIconDesc, avgWindDeg
                )
            }

            //return only the first 5 days
            return dailyForecastData.take(5)
        }
    }
}
