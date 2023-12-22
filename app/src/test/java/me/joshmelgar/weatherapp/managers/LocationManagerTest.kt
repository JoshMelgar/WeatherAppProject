package me.joshmelgar.weatherapp.managers

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import io.mockk.mockk
import org.junit.Before

class LocationManagerTest {

    private lateinit var context: Context
    private lateinit var locationManager: LocationManager
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @Before
    fun setUp() {
        context = mockk(relaxed = true)
        fusedLocationProviderClient = mockk(relaxed = true)

        // mock context and FusedLocationProviderClient inside LocationManager
        locationManager = LocationManager(context)
        locationManager.fusedLocationProviderClient = fusedLocationProviderClient
    }

//    @Test
//    fun getCurrentLocation_PermissionNotGranted() {
//        every { ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) } returns PackageManager.PERMISSION_DENIED
//
//        locationManager.getCurrentLocation { _, _ -> }
//
//        // Verify getCurrentLocation was never called
//        verify(exactly = 0) { fusedLocationProviderClient.getCurrentLocation(any(), any()) }
//    }
}