package me.joshmelgar.weatherapp.screens

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Rule

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun homeScreen_loadingState_displaysCircularProgressIndicator() {
        composeTestRule.setContent {
            HomeScreenPreviewLoading()
        }

        composeTestRule.onNodeWithTag("ProgressIndicator").assertIsDisplayed()
    }

    @Test
    fun forecastColumn_displaysCorrectNumberOfItems() {
        composeTestRule.setContent {
            ForecastColumnPreview()
        }

        composeTestRule.onAllNodesWithTag("ForecastBox").assertCountEquals(1)
    }

    @Test
    fun homeScreen_errorState_displaysErrorMessage() {
        composeTestRule.setContent {
            HomeScreenPreviewError()
        }

        composeTestRule.onNodeWithText("Error: Mock Error").assertIsDisplayed()
    }
}