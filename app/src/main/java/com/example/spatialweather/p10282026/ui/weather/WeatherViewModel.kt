package com.example.spatialweather.p10282026.ui.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import com.example.spatialweather.p10282026.data.WeatherRepository
import com.example.spatialweather.p10282026.domain.usecase.RecommendMovement
class WeatherViewModel(private val repository: WeatherRepository): ViewModel() {
 private val mutable=MutableStateFlow(WeatherUiState())
 val state: StateFlow<WeatherUiState> = mutable.asStateFlow()
 private var fetchJob: Job?=null
 private val recommend=RecommendMovement()
 fun exercises(s: WeatherUiState)=recommend(s.scene,s.night,s.weather)
 fun onEvent(event: WeatherEvent) { when(event) {
  is WeatherEvent.EmotionExperience -> mutable.update {it.copy(emotion=event.emotion,emotionLinked=event.linked,manualScene=if(event.linked)event.emotion.weather else it.manualScene,lightning=false)}
  is WeatherEvent.SelectCity -> { mutable.update { it.copy(city=event.city,weather=null,error=null) }; refresh() }
  is WeatherEvent.SelectScene -> mutable.update { it.copy(manualScene=event.scene,emotionLinked=false) }
  is WeatherEvent.Night -> mutable.update { it.copy(manualNight=event.enabled) }
  WeatherEvent.FollowLive -> mutable.update { it.copy(manualScene=null,manualNight=null,emotionLinked=false) }
  is WeatherEvent.Intensity -> mutable.update { it.copy(intensity=event.value.coerceIn(0f,1f)) }
  is WeatherEvent.Pause -> mutable.update { it.copy(paused=event.enabled) }
  is WeatherEvent.Lightning -> mutable.update { it.copy(lightning=event.enabled) }
  is WeatherEvent.Sound -> mutable.update { it.copy(sound=event.enabled) }
  is WeatherEvent.Immersive -> mutable.update { it.copy(immersive=event.enabled,stageError=event.error) }
  WeatherEvent.Refresh -> refresh()
 } }
 private fun refresh() {
  fetchJob?.cancel()
  val city=mutable.value.city
  fetchJob=viewModelScope.launch {
   mutable.update { it.copy(loading=true,error=null) }
   try { val weather=repository.fetch(city); mutable.update { it.copy(weather=weather,loading=false) } }
   catch(e: CancellationException) { throw e }
   catch(e: Exception) { mutable.update { it.copy(loading=false,error="暂时无法获取天气，请重试") } }
  }
 }
}
