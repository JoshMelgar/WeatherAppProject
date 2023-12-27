package me.joshmelgar.weatherapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherApi @Inject constructor() {

    private val openWeatherUrl =
        "https://api.openweathermap.org/"

    private val retrofitService: WeatherApiService by lazy {
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(openWeatherUrl)
            .build()
            .create(WeatherApiService::class.java)
    }

    fun getService(): WeatherApiService = retrofitService
}