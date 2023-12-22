package me.joshmelgar.weatherapp.helpers

class WindHelper {

    fun getWindDirection(degree: Int): String {
        val directions = arrayOf("N", "NE", "E", "SE", "S", "SW", "W", "NW")
        val index = ((degree + 22.5) / 45.0).toInt() % 8
        return directions[index]
    }
}
