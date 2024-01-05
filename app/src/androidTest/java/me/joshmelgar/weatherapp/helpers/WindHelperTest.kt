package me.joshmelgar.weatherapp.helpers

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class WindHelperTest {

    private val windHelper = WindHelper()

    @Test
    fun getWindDirection_ReturnsCorrectDirection() {
        val testCases = mapOf(
            0 to "N",
            45 to "NE",
            90 to "E",
            135 to "SE",
            180 to "S",
            215 to "SW",
            260 to "W",
            315 to "NW",
            360 to "N"
        )

        testCases.forEach { (input, expected) ->
            val result = windHelper.getWindDirection(input)
            assertThat(result).isEqualTo(expected)
        }
    }
}