package me.joshmelgar.weatherapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavigationItem(
    val label : String = "",
    val icon : ImageVector = Icons.Filled.Home,
    val route : String = ""
) {
    fun bottomNavigationItems() : List<BottomNavigationItem> {
        return listOf(
            BottomNavigationItem(
                label = "Home",
                icon = Icons.Filled.Home,
                route = Screens.HomeScreen.route
            ),
            BottomNavigationItem(
                label = "Forecast",
                icon = Icons.Filled.DateRange,
                route = Screens.ForecastScreen.route
            )
        )
    }
}
