package me.joshmelgar.weatherapp.hilt

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.joshmelgar.weatherapp.network.WeatherApi
import me.joshmelgar.weatherapp.network.WeatherApiService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SingletonModules {
    @Singleton
    @Provides
    fun provideWeatherApiService(): WeatherApiService {
        return WeatherApi().getService()
    }
}