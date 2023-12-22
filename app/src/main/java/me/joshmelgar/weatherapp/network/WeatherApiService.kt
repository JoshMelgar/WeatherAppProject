package me.joshmelgar.weatherapp.network

import me.joshmelgar.weatherapp.models.dto.CurrentWeather
import me.joshmelgar.weatherapp.models.dto.Forecast
import me.joshmelgar.weatherapp.models.dto.Geocoding
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val OPEN_WEATHER_URL =
    "https://api.openweathermap.org/"

interface WeatherApiService {

    @GET("data/2.5/weather")
    suspend fun getWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("units") units: String,
        @Query("appid") apiKey: String
    ): CurrentWeather

    @GET("geo/1.0/reverse")
    suspend fun getGeocoding(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("limit") callLimit: Int,
        @Query("appid") apiKey: String
    ): List<Geocoding>

    @GET("data/2.5/forecast")
    suspend fun getForecast(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("units") units: String,
        @Query("appid") apiKey: String
    ): Forecast
}

//lazy initialization of retrofitService.
//object for other classes to have access
object WeatherApi {

    private val retrofitService: WeatherApiService by lazy {
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(OPEN_WEATHER_URL)
            .build()
            .create(WeatherApiService::class.java)
    }

    fun getService(): WeatherApiService = retrofitService
}