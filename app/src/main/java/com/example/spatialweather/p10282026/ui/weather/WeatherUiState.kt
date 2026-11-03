package com.example.spatialweather.p10282026.ui.weather

import com.example.spatialweather.p10282026.domain.model.*
data class WeatherUiState(val emotion:com.example.spatialweather.p10282026.bci.Emotion?=null,val emotionLinked:Boolean=false,val city: City=cities.first(),val weather: Weather?=null,val loading: Boolean=false,val error: String?=null,val manualScene: Scene?=null,val manualNight: Boolean?=null,val intensity: Float=0.55f,val paused: Boolean=false,val lightning: Boolean=false,val sound: Boolean=false,val immersive: Boolean=false,val stageError: String?=null) {
 val scene get()=manualScene ?: weather?.scene ?: Scene.SUNNY
 val night get()=manualNight ?: (weather?.isDay == false)
}


sealed interface WeatherEvent {
 data class EmotionExperience(val emotion:com.example.spatialweather.p10282026.bci.Emotion,val linked:Boolean):WeatherEvent
 data class SelectCity(val city: City): WeatherEvent
 data class SelectScene(val scene: Scene): WeatherEvent
 data class Night(val enabled: Boolean): WeatherEvent
 data object FollowLive: WeatherEvent
 data class Intensity(val value: Float): WeatherEvent
 data class Pause(val enabled: Boolean): WeatherEvent
 data class Lightning(val enabled: Boolean): WeatherEvent
 data class Sound(val enabled: Boolean): WeatherEvent
 data class Immersive(val enabled: Boolean, val error: String?=null): WeatherEvent
 data object Refresh: WeatherEvent
}
