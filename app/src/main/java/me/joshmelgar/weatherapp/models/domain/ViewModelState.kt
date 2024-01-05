package me.joshmelgar.weatherapp.models.domain

data class ViewModelState(
    val isLoading: Boolean,
    val locationInfo: LocationInfo?,
    val weatherDetails: WeatherDetails?,
    val forecastHomeScreenDetails: List<ForecastHomeDetails>?,
    val dailyForecast: List<DailyForecast>?,
    val error: Exception?
)
