package com.example.spatialweather.p10282026.ui.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.spatialweather.p10282026.data.OpenMeteoRepository
class WeatherViewModelFactory: ViewModelProvider.Factory {
 @Suppress("UNCHECKED_CAST")
 override fun <T: ViewModel> create(modelClass: Class<T>): T {
  require(modelClass.isAssignableFrom(WeatherViewModel::class.java))
  return WeatherViewModel(OpenMeteoRepository()) as T
 }
}

object WeatherSession : androidx.lifecycle.ViewModelStoreOwner {
 override val viewModelStore=androidx.lifecycle.ViewModelStore()
}
