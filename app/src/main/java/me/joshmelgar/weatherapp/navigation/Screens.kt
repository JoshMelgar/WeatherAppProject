package me.joshmelgar.weatherapp.navigation

sealed class Screens(val route : String) {
    object HomeScreen : Screens("home")
    object ForecastScreen : Screens("forecast")
}