package me.joshmelgar.weatherapp.repositories

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import me.joshmelgar.weatherapp.models.domain.ForecastHomeDetails
import me.joshmelgar.weatherapp.models.domain.ForecastMainDetails
import me.joshmelgar.weatherapp.models.dto.CurrentWeather
import me.joshmelgar.weatherapp.models.dto.Forecast
import me.joshmelgar.weatherapp.models.dto.ForecastItem
import me.joshmelgar.weatherapp.models.dto.ForecastMain
import me.joshmelgar.weatherapp.models.dto.ForecastWeather
import me.joshmelgar.weatherapp.models.dto.ForecastWind
import me.joshmelgar.weatherapp.models.dto.Geocoding
import me.joshmelgar.weatherapp.models.dto.Main
import me.joshmelgar.weatherapp.models.dto.Wind
import me.joshmelgar.weatherapp.network.WeatherApiService
import me.joshmelgar.weatherapp.respositories.WeatherRepository
import me.joshmelgar.weatherapp.utils.Result
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class WeatherRepositoryTest {

    private lateinit var repository: WeatherRepository
    private val mockApiService = mockk<WeatherApiService>()

    @Before
    fun setup() {
        repository = WeatherRepository(mockApiService)
    }

    @Test
    fun getGeocoding_returns_success() = runTest {
        // GIVEN
        val fakeResponse = listOf(Geocoding(
            cityName = "CityLand",
            cityState = "Georgia",
            cityCountry = "US"
        ))
        coEvery { mockApiService.getGeocoding(any(), any(), any(), any()) } returns fakeResponse

        // WHEN
        val result = repository.getGeocoding(0.0, 0.0, 1, "apiKey")

        // THEN
        assertTrue(result is Result.Success)
    }

    @Test
    fun getGeocoding_returns_error_on_exception() = runTest {
        // GIVEN
        coEvery { mockApiService.getGeocoding(any(), any(), any(), any()) } throws Exception("Network error")

        // WHEN
        val result = repository.getGeocoding(0.0, 0.0, 1, "apiKey")

        // THEN
        assertTrue(result is Result.Error)
        assertEquals(null, (result as Result.Error).exception.message)
    }

    @Test
    fun getWeather_returns_success() = runTest {
        // GIVEN
        val fakeResponse = CurrentWeather(
            Main(
                temp = 50.0,
                feelsLike = 55.5,
                tempMin = 45.9,
                tempMax = 61.1
            ),
            Wind(
                speed = 3.84,
                degree = 56
            )
        )
        coEvery { mockApiService.getWeather(any(), any(), any(), any()) } returns fakeResponse

        // WHEN
        val result = repository.getWeather(0.0, 0.0, "imperial", "apiKey")

        // THEN
        assertTrue(result is Result.Success)
    }

    @Test
    fun getWeather_returns_error_on_exception() = runTest {
        // GIVEN
        coEvery { mockApiService.getWeather(any(), any(), any(), any()) } throws Exception("Network error")

        // WHEN
        val result = repository.getWeather(0.0, 0.0, "imperial", "apiKey")

        // THEN
        assertTrue(result is Result.Error)
        assertEquals(null, (result as Result.Error).exception.message)
    }

    @Test
    fun getForecastHomeScreenWeatherList_returns_transformed_data_on_success() = runTest {
        // GIVEN a successful response with data
        val fakeForecastItem = ForecastItem(
            dtText = "2023-12-28 00:00:00",
            ForecastMain(
                temp = 60.3,
                tempMin = 30.5,
                tempMax = 67.5
            ),
            listOf(
                ForecastWeather(
                    weatherType = "Sunny",
                    description = "sunny",
                    icon = "10d"
                ),
            ),
            ForecastWind(
                speed = 3.75,
                deg = 40
            )
        )
        val fakeForecastDto = Forecast(listOf(fakeForecastItem))
        coEvery { mockApiService.getForecast(any(), any(), any(), any()) } returns fakeForecastDto

        // WHEN
        val result = repository.getForecastHomeScreenWeatherList(0.0, 0.0, "imperial", "apiKey")

        // THEN ensure the data is transformed correctly
        assertTrue(result is Result.Success)
        val details = (result as Result.Success).data.first()
        assertThat(details).isInstanceOf(ForecastHomeDetails::class.java)
    }

    @Test
    fun getForecastHomeScreenWeatherList_returns_error_when_transformation_fails() = runTest {
        // Given a response that will cause the extension function to throw an exception
        val fakeForecastItem = ForecastItem(
            dtText = "2023-12-28 00:00:00",
            ForecastMain(
                temp = 60.3,
                tempMin = 30.44,
                tempMax = 67.5
            ),
            forecastWeather = listOf(),
            ForecastWind(
                speed = 3.75,
                deg = 40
            )
        )
        val fakeForecastDto = Forecast(listOf(fakeForecastItem))
        coEvery { mockApiService.getForecast(any(), any(), any(), any()) } returns fakeForecastDto

        // When
        val result = repository.getForecastHomeScreenWeatherList(0.0, 0.0, "imperial", "apiKey")

        // THEN ensure it returns an error
        assertTrue(result is Result.Error)
        assertThat((result as Result.Error).exception.message).contains("Forecast weather list is empty")
    }

    @Test
    fun getForecastScreenWeatherList_returns_transformed_data_on_success() = runTest {
        // GIVEN a successful response with data
        val fakeForecastItem = ForecastItem(
            dtText = "2023-12-28 00:00:00",
            ForecastMain(
                temp = 60.3,
                tempMin = 30.5,
                tempMax = 67.5
            ),
            listOf(
                ForecastWeather(
                    weatherType = "Sunny",
                    description = "sunny",
                    icon = "10d"
                ),
            ),
            ForecastWind(
                speed = 3.75,
                deg = 40
            )
        )
        val fakeForecastDto = Forecast(listOf(fakeForecastItem))
        coEvery { mockApiService.getForecast(any(), any(), any(), any()) } returns fakeForecastDto

        // WHEN
        val result = repository.getForecastScreenWeatherList(0.0, 0.0, "imperial", "apiKey")

        // THEN ensure the data is transformed correctly
        assertTrue(result is Result.Success)
        val details = (result as Result.Success).data.first()
        assertThat(details).isInstanceOf(ForecastMainDetails::class.java)
    }

    @Test
    fun getForecastScreenWeatherList_returns_error_when_transformation_fails() = runTest {
        // Given a response that will cause the extension function to throw an exception
        val fakeForecastItem = ForecastItem(
            dtText = "2023-12-28 00:00:00",
            ForecastMain(
                temp = 60.3,
                tempMin = 30.44,
                tempMax = 67.5
            ),
            forecastWeather = listOf(),
            ForecastWind(
                speed = 3.75,
                deg = 40
            )
        )
        val fakeForecastDto = Forecast(listOf(fakeForecastItem))
        coEvery { mockApiService.getForecast(any(), any(), any(), any()) } returns fakeForecastDto

        // When
        val result = repository.getForecastScreenWeatherList(0.0, 0.0, "imperial", "apiKey")

        // THEN ensure it returns an error
        assertTrue(result is Result.Error)
        assertThat((result as Result.Error).exception.message).contains("Forecast weather list is empty")
    }
}