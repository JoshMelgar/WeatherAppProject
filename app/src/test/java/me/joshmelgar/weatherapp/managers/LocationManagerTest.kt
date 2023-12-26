//package me.joshmelgar.weatherapp.managers
//
//import android.content.Context
//import android.content.pm.PackageManager
//import android.text.TextUtils
//import androidx.core.content.ContextCompat
//import com.google.android.gms.location.FusedLocationProviderClient
//import com.google.android.gms.tasks.CancellationToken
//import io.mockk.every
//import io.mockk.mockk
//import io.mockk.verify
//import org.junit.Before
//import org.junit.Test
//
//class LocationManagerTest {
//
//    private lateinit var context: Context
//    private lateinit var locationManager: LocationManager
//    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
//
//    @Before
//    fun setUp() {
//        context = mockk(relaxed = true)
//        fusedLocationProviderClient = mockk(relaxed = true)
//
//        // mock context and FusedLocationProviderClient inside LocationManager
//        locationManager = LocationManager(context)
//    }
//
//    @Test
//    fun getCurrentLocation_PermissionNotGranted() {
//        every { ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) } returns PackageManager.PERMISSION_DENIED
//
//        locationManager.getCurrentLocation { _, _ -> }
//
//        // Verify getCurrentLocation was never called
//        verify(exactly = 0) { fusedLocationProviderClient.getCurrentLocation(ofType<Int>(), ofType<CancellationToken>()) }
//    }
//}