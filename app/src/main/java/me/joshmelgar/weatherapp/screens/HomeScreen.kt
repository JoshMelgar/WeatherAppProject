package me.joshmelgar.weatherapp.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.joshmelgar.weatherapp.BuildConfig
import me.joshmelgar.weatherapp.network.CurrentWeather
import me.joshmelgar.weatherapp.network.WeatherApi

@Composable
fun HomeScreen(navController: NavController) {
    NavigationBar {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val viewModel = viewModel<HomeScreenViewModel>()
            when(val state = viewModel.state.collectAsState().value) {
                HomeScreenViewModel.State.Loading -> {
                    Text("Loading...")
                }
                is HomeScreenViewModel.State.Data -> {
                    Text((state.data).toString())
                }
            }
        }
    }
}

class HomeScreenViewModel : ViewModel() {
    sealed class State {
        object Loading: State()
        data class Data(val data: CurrentWeather): State()
    }

    private var _state = MutableStateFlow<State>(State.Loading)
    val state = _state.asStateFlow()

    val apiKey = BuildConfig.API_KEY_WEATHER

    init {
        viewModelScope.launch {
            val data = WeatherApi.retrofitCurrentWeatherService.getWeather(1.1, 1.1, apiKey)
            _state.value = State.Data(data)
        }
    }
}