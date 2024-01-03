package me.joshmelgar.weatherapp.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import me.joshmelgar.weatherapp.managers.LocationManager
import me.joshmelgar.weatherapp.models.domain.ForecastHomeDetails
import me.joshmelgar.weatherapp.models.domain.ForecastMainDetails
import me.joshmelgar.weatherapp.models.domain.WeatherDetails
import me.joshmelgar.weatherapp.respositories.WeatherRepository
import me.joshmelgar.weatherapp.utils.Result
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class WeatherViewmodelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    //replace the default dispatcher with a TestCoroutineDispatcher
    private val testDispatcher = TestCoroutineDispatcher()

    //mock dependencies
    private lateinit var mockRepository: WeatherRepository
    private lateinit var mockLocationManager: LocationManager

    //subject under test
    private lateinit var weatherViewModel: WeatherViewModel

    @Before
    fun setup() {
        // Initialize mocks
        mockRepository = mockk()
        mockLocationManager = mockk()

        // Initialize the ViewModel with mocked dependencies
        weatherViewModel = WeatherViewModel(mockRepository, mockLocationManager)

        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        // reset the main dispatcher to the original Main dispatcher
        Dispatchers.resetMain()
        // clean up the TestCoroutineDispatcher resources
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `update location permission status updates internal state`() = runTest {
        // GIVEN the permission status is initially false
        assertThat(weatherViewModel.locationPermissionGranted.value).isFalse()

        // WHEN permission status is updated to true
        weatherViewModel.updateLocationPermissionStatus(true)

        // THEN the internal state reflects the new permission status
        assertThat(weatherViewModel.locationPermissionGranted.value).isTrue()
    }

    @Test
    fun `update location with successful data loading updates state to Data`() = runTest {
        // GIVEN successful responses from all repository calls
        coEvery { mockRepository.getGeocoding(any(), any(), any(), any()) } returns Result.Success(mockk())
        coEvery { mockRepository.getWeather(any(), any(), any(), any()) } returns Result.Success(mockk())
        coEvery { mockRepository.getForecastHomeScreenWeatherList(any(), any(), any(), any()) } returns Result.Success(mockk())
        coEvery { mockRepository.getForecastScreenWeatherList(any(), any(), any(), any()) } returns Result.Success(mockk())

        // WHEN updateLocation is called with some latitude and longitude
        weatherViewModel.updateLocation(1.0, 1.0)

        // THEN the state should be updated to State.Data
        assertThat(weatherViewModel.state.value).isInstanceOf(WeatherViewModel.State.Data::class.java)
    }

    @Test
    fun `update location with repository error updates state to Error`() = runTest {
        // GIVEN the repository returns an error for geocoding
        coEvery { mockRepository.getGeocoding(any(), any(), any(), any()) } returns Result.Error(Exception("Network error"))

        // AND the repository returns some response (successful or error) for other calls
        coEvery { mockRepository.getWeather(any(), any(), any(), any()) } returns Result.Success(mockk<WeatherDetails>())
        coEvery { mockRepository.getForecastHomeScreenWeatherList(any(), any(), any(), any()) } returns Result.Success(mockk<List<ForecastHomeDetails>>())
        coEvery { mockRepository.getForecastScreenWeatherList(any(), any(), any(), any()) } returns Result.Success(mockk<List<ForecastMainDetails>>())

        // WHEN updateLocation is called
        weatherViewModel.updateLocation(1.0, 1.0)

        // THEN the state should be updated to State.Error with the appropriate exception
        val state = weatherViewModel.state.value as WeatherViewModel.State.Error
        assertThat(state.error).hasMessageThat().contains("Network error")
    }
}