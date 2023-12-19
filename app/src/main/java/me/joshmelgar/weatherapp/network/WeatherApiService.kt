package me.joshmelgar.weatherapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val WEATHER_URL =
    "https://api.openweathermap.org/data/2.5/"

private const val GEOCODING_URL =
    "https://api.openweathermap.org/geo/1.0/"

private val retrofitWeather = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(WEATHER_URL)
    .build()

private val retrofitGeocoding = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(GEOCODING_URL)
    .build()

interface WeatherApiService {

    @GET("weather")
    suspend fun getWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("units") units: String,
        @Query("appid") apiKey: String
    ): CurrentWeather

    @GET("reverse")
    suspend fun getGeocoding(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("limit") callLimit: Int,
        @Query("appid") apiKey: String
    ): List<Geocoding>

    @GET("forecast")
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
    val retrofitWeatherService: WeatherApiService by lazy {
        retrofitWeather.create(WeatherApiService::class.java)
    }

    val retrofitGeocodingService: WeatherApiService by lazy {
        retrofitGeocoding.create(WeatherApiService::class.java)
    }
}