package me.joshmelgar.weatherapp.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import me.joshmelgar.weatherapp.models.domain.DailyForecast
import me.joshmelgar.weatherapp.models.domain.ViewModelState
import me.joshmelgar.weatherapp.models.domain.WindInfo

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Rule

@RunWith(AndroidJUnit4::class)
class ForecastScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun forecastScreen_loadingState_displaysCircularProgressIndicator() {
        composeTestRule.setContent {
            ForecastScreenPreviewLoading()
        }
        composeTestRule.onNodeWithTag("ProgressIndicator").assertIsDisplayed()
    }

    @Test
    fun fiveDayForecastColumn_withData_displaysCorrectly() {
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

        composeTestRule.setContent {
            FiveDayForecastColumn(dailyForecasts = sampleForecastList)
        }

        composeTestRule.onNodeWithText("Monday").assertIsDisplayed()
        composeTestRule.onNodeWithText("Hi Temp: 80").assertIsDisplayed()
        composeTestRule.onNodeWithText("Low Temp: 10").assertIsDisplayed()
    }

    @Test
    fun forecastScreenWrapper_errorState_displaysErrorMessage() {
        val testViewModelState = ViewModelState(
            isLoading = false,
            null,
            null,
            null,
            null,
            Exception("Mock Error")
        )
        composeTestRule.setContent {
            ForecastScreenWrapper(
                state = testViewModelState,
                innerPadding = PaddingValues(all = 16.dp)
            )
        }
        composeTestRule.onNodeWithText("Error: Mock Error").assertIsDisplayed()
    }
}