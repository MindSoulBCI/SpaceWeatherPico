package com.example.spatialweather.p10282026.ui.scene

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pico.spatial.ui.foundation.content.SpatialView
import com.example.spatialweather.p10282026.ui.weather.*
import kotlinx.coroutines.*

@Composable fun WeatherStage(model: WeatherViewModel = viewModel(viewModelStoreOwner=WeatherSession,factory=WeatherViewModelFactory())) {
 val scene=remember {WeatherScene()}
 var ready by remember {mutableStateOf(false)}
 DisposableEffect(scene) { onDispose { scene.close(); model.onEvent(WeatherEvent.Immersive(false)) } }
 SpatialView(initial={content,_ ->
  scene.create()
  content.addEntity(scene.root)
  scene.measure()
  ready=true
 })
 LaunchedEffect(ready) {
  if(ready) {
   model.onEvent(WeatherEvent.Immersive(true))
   var previous=withFrameNanos {it}
   while(isActive) {
    val now=withFrameNanos {it}
    scene.update(model.state.value,((now-previous)/1_000_000_000f).coerceIn(0f,0.05f))
    previous=now
   }
  }
 }
}
