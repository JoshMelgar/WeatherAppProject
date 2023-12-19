package me.joshmelgar.weatherapp.helpers

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DateTimeHelper {

    companion object {
        fun convertDateString(input: String): String {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

            val date = inputFormat.parse(input)
            return outputFormat.format(date as Date)
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
    }
}
