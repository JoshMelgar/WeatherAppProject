package me.joshmelgar.weatherapp.helpers

class WindHelper {

    companion object {
        fun getWindDirection(degree: Int): String {
            val directions = arrayOf("North", "North-East", "East", "South-East", "South", "South-West", "West", "North-West")
            val index = ((degree + 22.5) / 45.0).toInt() % 8
            return directions[index]
        }
    }
}
