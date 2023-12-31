package me.joshmelgar.weatherapp.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import me.joshmelgar.weatherapp.BuildConfig
import me.joshmelgar.weatherapp.managers.LocationManager
import me.joshmelgar.weatherapp.models.domain.ForecastHomeDetails
import me.joshmelgar.weatherapp.models.domain.ForecastMainDetails
import me.joshmelgar.weatherapp.models.domain.LocationInfo
import me.joshmelgar.weatherapp.models.domain.WeatherDetails
import me.joshmelgar.weatherapp.respositories.WeatherRepository
import me.joshmelgar.weatherapp.state.State
import me.joshmelgar.weatherapp.utils.Result
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class WeatherViewmodelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    //mock dependencies
    private lateinit var mockRepository: WeatherRepository
    private lateinit var mockLocationManager: LocationManager

    //subject under test
    private lateinit var weatherViewModel: WeatherViewModel

    private val apiKey = BuildConfig.API_KEY_WEATHER

    @Before
    fun setup() {
        mockRepository = mockk()
        mockLocationManager = mockk()

        // Initialize the ViewModel with mocked dependencies
        weatherViewModel = WeatherViewModel(mockRepository, mockLocationManager)

        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun update_location_permission_status_updates_internal_state() = runTest {
        // GIVEN the permission status is initially false
        assertThat(weatherViewModel.locationPermissionGranted.value).isFalse()

        // WHEN permission status is updated to true
        weatherViewModel.updateLocationPermissionStatus(true)

        // THEN the internal state reflects the new permission status
        assertThat(weatherViewModel.locationPermissionGranted.value).isTrue()
    }

    @Test
    fun update_location_with_successful_data_loading_updates_state_to_Data() = runTest {
        // GIVEN successful responses from all repository calls
        val latitude = 37.4219983
        val longitude = -122.084
        coEvery { mockRepository.getGeocoding(eq(latitude), eq(longitude), eq(1), eq(apiKey)) } returns Result.Success(mockk<LocationInfo>())
        coEvery { mockRepository.getWeather(eq(latitude), eq(longitude), eq("imperial"), eq(apiKey)) } returns Result.Success(mockk<WeatherDetails>())

        //conversion is done in the viewModel
        val mockForecastHomeDetails = mockk<ForecastHomeDetails>(relaxed = true)
        every { mockForecastHomeDetails.date } returns "2023-01-03 00:00:00"
        coEvery { mockRepository.getForecastHomeScreenWeatherList(eq(latitude), eq(longitude), eq("imperial"), eq(apiKey)) } returns Result.Success(listOf(mockForecastHomeDetails))

        val mockForecastMainDetails = mockk<ForecastMainDetails>(relaxed = true)
        every { mockForecastMainDetails.date } returns "2023-01-03 00:00:00"
        coEvery { mockRepository.getForecastScreenWeatherList(eq(latitude), eq(longitude), eq("imperial"), eq(apiKey)) } returns Result.Success(listOf(mockForecastMainDetails))

        // WHEN updateLocation is called with some latitude and longitude
        weatherViewModel.updateLocation(latitude, longitude)

        // THEN the state should be updated to State.Data
        assertThat(weatherViewModel.state.value).isInstanceOf(State.Data::class.java)
    }

    @Test
    fun update_location_with_repository_error_updates_state_to_Error() = runTest {
        // GIVEN the repository returns an error for geocoding
        coEvery { mockRepository.getGeocoding(any(), any(), any(), any()) } returns Result.Error(Exception("Network error"))

        // AND the repository returns some response (successful or error) for other calls
        coEvery { mockRepository.getWeather(any(), any(), any(), any()) } returns Result.Success(mockk<WeatherDetails>())
        coEvery { mockRepository.getForecastHomeScreenWeatherList(any(), any(), any(), any()) } returns Result.Success(mockk<List<ForecastHomeDetails>>())
        coEvery { mockRepository.getForecastScreenWeatherList(any(), any(), any(), any()) } returns Result.Success(mockk<List<ForecastMainDetails>>())

        // WHEN updateLocation is called
        weatherViewModel.updateLocation(1.0, 1.0)

        // THEN the state should be updated to State.Error with the appropriate exception
        val state = weatherViewModel.state.value as State.Error
        assertThat(state.error).hasMessageThat().contains("Network error")
    }
}