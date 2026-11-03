package com.example.spatialweather.p10282026.ui.weather.components

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pico.spatial.ui.design.*
import com.pico.spatial.ui.platform.containers.*
import com.example.spatialweather.p10282026.ui.weather.*
import kotlinx.coroutines.launch

@Composable fun ImmersionControls(model: WeatherViewModel) {
 val s by model.state.collectAsState()
 val navigator=LocalSpatialNavigator.current
 val scope=rememberCoroutineScope()
 var opening by remember {mutableStateOf(false)}
 Button(enabled=!opening,onClick={scope.launch {
  opening=true
  try {
   if(s.immersive) { navigator.closeStage(); model.onEvent(WeatherEvent.Immersive(false)) }
   else when(navigator.openStage("WeatherEnvironment",StageStyle.Mixed)) {
    OpenStageResult.Allowed -> model.onEvent(WeatherEvent.Immersive(true))
    else -> model.onEvent(WeatherEvent.Immersive(false,"系统暂未允许打开空间天气，请重试"))
   }
  } catch(e: Exception) { model.onEvent(WeatherEvent.Immersive(false,"空间天气启动失败，请重试")) }
  finally { opening=false }
 }}) {Text(if(opening) "正在打开…" else if(s.immersive) "退出空间天气" else "进入空间天气")}
 s.stageError?.let {Copy(it)}
 Copy("效果强度 ${(s.intensity*100).toInt()}%")
 Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
  Button(onClick={model.onEvent(WeatherEvent.Intensity(s.intensity-0.15f))}) {Text("减弱")}
  Button(onClick={model.onEvent(WeatherEvent.Intensity(s.intensity+0.15f))}) {Text("增强")}
 }
 Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) {Copy("暂停动态");Switch(s.paused,{model.onEvent(WeatherEvent.Pause(it))})}
 Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) {Copy("雷电闪光");Switch(s.lightning,{model.onEvent(WeatherEvent.Lightning(it))})}
 Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) {Copy("环境声音");Switch(s.sound,{model.onEvent(WeatherEvent.Sound(it))})}
}
