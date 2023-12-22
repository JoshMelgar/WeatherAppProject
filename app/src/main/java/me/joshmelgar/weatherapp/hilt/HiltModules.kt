package me.joshmelgar.weatherapp.hilt

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import me.joshmelgar.weatherapp.managers.LocationManager
import me.joshmelgar.weatherapp.network.WeatherApi
import me.joshmelgar.weatherapp.network.WeatherApiService
import me.joshmelgar.weatherapp.respositories.WeatherRepository

@Module
@InstallIn(ViewModelComponent::class)
object HiltModules {
    @Provides
    fun provideLocationManager(@ApplicationContext context: Context): LocationManager {
        return LocationManager(context)
    }

    @Provides
    fun provideWeatherRepository(
        weatherApiService: WeatherApiService
    ): WeatherRepository {
        return WeatherRepository(weatherApiService)
    }

    @Provides
    fun provideWeatherApiService(): WeatherApiService {
        return WeatherApi.getService()
    }
}