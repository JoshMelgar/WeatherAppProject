package me.joshmelgar.weatherapp.network

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherApiServiceTest {

    private lateinit var service: WeatherApiService
    private lateinit var mockWebServer: MockWebServer

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        service = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/")) // Use mock server
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApiService::class.java)
    }


    @After
    fun shutdown() {
        mockWebServer.shutdown()
    }

    @Test
    fun getWeather_returns_expected_data() = runBlocking {
        val response = MockResponse()
            .setResponseCode(200)
            .setBody("""
            {
              "coord": {"lon": -122.084, "lat": 37.422},
              "weather": [{"id": 804, "main": "Clouds", "description": "overcast clouds", "icon": "04n"}],
              "base": "stations",
              "main": {
                "temp": 281.19,
                "feels_like": 280.01,
                "temp_min": 279.5,
                "temp_max": 283.4,
                "pressure": 1018,
                "humidity": 93
              },
              "visibility": 10000,
              "wind": {"speed": 2.06, "deg": 140},
              "clouds": {"all": 100},
              "dt": 1704290586,
              "sys": {
                "type": 2,
                "id": 2010364,
                "country": "US",
                "sunrise": 1704295377,
                "sunset": 1704330124
              },
              "timezone": -28800,
              "id": 5375480,
              "name": "Mountain View",
              "cod": 200
            }
        """.trimIndent())
        mockWebServer.enqueue(response)

        val result = service.getWeather(0.0, 0.0, "imperial", "api_key")

        // validate the request made to the server
        val request = mockWebServer.takeRequest()
        assertThat(request.path).isEqualTo("/data/2.5/weather?lat=0.0&lon=0.0&units=imperial&appid=api_key")

        // validate the response
        assertThat(result).isNotNull()
        assertThat(result.main.temp).isEqualTo(281.19)
    }

    @Test
    fun getGeocoding_returns_expected_data() = runBlocking {
        val response = MockResponse()
            .setResponseCode(200)
            .setBody("[{\"name\":\"Mountain View\",\"local_names\":{\"ar\":\"مونتن فيو\",\"en\":\"Mountain View\",\"zh\":\"山景城\",\"ru\":\"Маунтин-Вью\"},\"lat\":37.3893889,\"lon\":-122.0832101,\"country\":\"US\",\"state\":\"California\"}]")
        mockWebServer.enqueue(response)

        val result = service.getGeocoding(0.0, 0.0, 1, "api_key")

        // validate the request made to the server
        val request = mockWebServer.takeRequest()
        assertThat(request.path).isEqualTo("/geo/1.0/reverse?lat=0.0&lon=0.0&limit=1&appid=api_key")

        // validate the response
        assertThat(result).isNotNull()
        assertThat(result).isNotEmpty()
        assertThat(result[0].cityName).isEqualTo("Mountain View")
    }

    @Test
    fun getForecast_returns_expected_data() = runBlocking {

        val jsonString = this.javaClass.classLoader?.getResourceAsStream("forecast_response.json")?.bufferedReader().use { it?.readText() } ?: ""

        val response = MockResponse()
            .setResponseCode(200)
            .setBody(jsonString)
        mockWebServer.enqueue(response)

        val result = service.getForecast(0.0, 0.0,  "imperial","api_key")

        // validate the request made to the server
        val request = mockWebServer.takeRequest()
        assertThat(request.path).isEqualTo("/data/2.5/forecast?lat=0.0&lon=0.0&units=imperial&appid=api_key")

        // validate the response
        assertThat(result).isNotNull()
    }
}