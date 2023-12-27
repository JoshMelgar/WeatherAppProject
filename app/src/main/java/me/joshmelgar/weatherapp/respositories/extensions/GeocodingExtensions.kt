package me.joshmelgar.weatherapp.respositories.extensions

import me.joshmelgar.weatherapp.models.domain.LocationInfo
import me.joshmelgar.weatherapp.models.dto.Geocoding

fun Geocoding.toLocationInfo(): LocationInfo {
    return LocationInfo(
        cityName = this.cityName,
        cityState = this.cityState,
        cityCountry = this.cityCountry
    )
}