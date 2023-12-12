package me.joshmelgar.weatherapp.network

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

private const val CURRENT_WEATHER_URL =
    "https://api.openweathermap.org/data/2.5/"

private const val CURRENT_WEATHER_URL_OLD =
    "https://api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}&appid={API key}"

private const val FIVE_DAY_FORECAST_URL =
    "api.openweathermap.org/data/2.5/forecast?lat={lat}&lon={lon}&appid={API key}"

private const val GEOCODING_URL =
    "http://api.openweathermap.org/geo/1.0/direct?q={city name},{state code},{country code}&limit={limit}&appid={API key}"

private val retrofitCurrentWeather = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(CURRENT_WEATHER_URL)
    .build()

//private val retrofitFiveDayForecast = Retrofit.Builder()
//    .addConverterFactory(GsonConverterFactory.create())
//    .baseUrl(FIVE_DAY_FORECAST_URL)
//    .build()
//
//private val retrofitGeocoding = Retrofit.Builder()
//    .addConverterFactory(GsonConverterFactory.create())
//    .baseUrl(GEOCODING_URL)
//    .build()

interface WeatherApiService {

    @GET("weather")
    suspend fun getWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String
    ): CurrentWeather


//    @GET(CURRENT_WEATHER_URL)
//    suspend fun getCurrentWeather(): List<CurrentWeather>

//    @GET(FIVE_DAY_FORECAST_URL)
//    suspend fun getFiveDayForecast(): List<CurrentWeather>
//
//    @GET(GEOCODING_URL)
//    suspend fun getGeocoding(): List<Geocoding>
}

//lazy initialization of retrofitService.
//object for other classes to have access
object WeatherApi {
    val retrofitCurrentWeatherService : WeatherApiService by lazy {
        retrofitCurrentWeather.create(WeatherApiService::class.java) }

//    val retrofitFiveDayForecastService : WeatherApiService by lazy {
//        retrofitFiveDayForecast.create(WeatherApiService::class.java) }
//
//    val retrofitGeocodingService : WeatherApiService by lazy {
//        retrofitGeocoding.create(WeatherApiService::class.java) }
}