package com.example.spatialweather.p10282026.ui.weather

import androidx.compose.runtime.*
import com.example.spatialweather.p10282026.ui.weather.components.ImmersionControls
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spatialweather.p10282026.ui.weather.components.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.pico.spatial.ui.design.*
import com.pico.spatial.ui.platform.containers.LocalSpatialNavigator
import com.example.spatialweather.p10282026.R
import com.example.spatialweather.p10282026.domain.model.*
import com.example.spatialweather.p10282026.ui.navigation.Windows

@Composable fun WeatherScreen(modifier: Modifier=Modifier, model: WeatherViewModel = viewModel(viewModelStoreOwner=WeatherSession, factory=WeatherViewModelFactory())) {
 val s by model.state.collectAsState()
 val navigator=LocalSpatialNavigator.current
 LaunchedEffect(Unit) {
  model.onEvent(WeatherEvent.Refresh)
  model.onEvent(WeatherEvent.SelectScene(Scene.CLOUDY))
  val opened=navigator.openStage("WeatherEnvironment",com.pico.spatial.ui.platform.containers.StageStyle.Mixed)
  kotlinx.coroutines.delay(1200)
  navigator.openWindowContainer(Windows.CONTROLS,Windows.TAG)
  kotlinx.coroutines.delay(300)
  navigator.openWindowContainer(Windows.MOVEMENT,Windows.TAG)
  kotlinx.coroutines.delay(600)
  if(com.example.spatialweather.p10282026.account.AccountStore.session==null) navigator.openWindowContainer("MindSoulAccount","account")
  if(opened !is com.pico.spatial.ui.platform.containers.OpenStageResult.Allowed) model.onEvent(WeatherEvent.Immersive(false,"空间天气未打开，可在场景控制中重试"))
 }
 Panel(modifier) {
  Heading("MindSoul 空间天气")
  com.example.spatialweather.p10282026.calendar.TodayCalendarSummary()
  Text(s.weather?.let { "${it.temperature.toInt()}°C · ${s.city.name}" } ?: "—°C · ${s.city.name}",style=PicoTheme.typography.displaySmall,color=PicoTheme.colorScheme.labelPrimary)
  Button(onClick={navigator.openWindowContainer("MindSoulCalendar","calendar")}){Text("查看日历 / 节日")}
  Copy("成都市脑云矩阵科技有限公司 · MindSoul 提供")
  Button(onClick={navigator.openWindowContainer("MindSoulAccount","account")}){Text(if(com.example.spatialweather.p10282026.account.AccountStore.session==null)"登录 / 注册" else "我的账户")}
  Button(onClick={navigator.openWindowContainer("BrainChannels","bci")}){Text("脑机接口 · 32 通道看板")}
  s.emotion?.let{Copy("${it.title} · ${if(s.emotionLinked)"情绪体验联动" else "手动天气优先"}")}
  Copy("${s.city.name} · ${if(s.manualScene!=null || s.manualNight!=null) "手动场景预览" else "跟随实时天气"}")
  val picture=if(s.night) R.drawable.room_night else when(s.scene) {Scene.SUNNY->R.drawable.room_pure; Scene.CLOUDY->R.drawable.room_overcast; else->R.drawable.room_storm}
  Image(painterResource(picture),"${s.scene.label}房间场景",Modifier.fillMaxWidth().height(130.dp),contentScale=ContentScale.Crop)

  Copy("${s.scene.label} · ${if(s.night) "夜间" else "日间"}")
  s.weather?.let { Copy("实时风速 ${it.wind} km/h"); Copy("更新于 ${it.time.replace('T',' ')}") }
  if(s.loading) Copy("正在获取天气…")
  s.error?.let { Copy(it); Button(onClick={model.onEvent(WeatherEvent.Refresh)}) { Text("重新获取") } }
  Button(onClick={navigator.openWindowContainer(Windows.CONTROLS,Windows.TAG)}) { Text("打开场景控制窗口") }
  Button(onClick={navigator.openWindowContainer(Windows.MOVEMENT,Windows.TAG)}) { Text("打开运动推荐窗口") }
  Copy("拖动各窗口底部的系统手柄，可独立摆放。")
 }
}
@Composable fun ControlsScreen(modifier: Modifier=Modifier, model: WeatherViewModel = viewModel(viewModelStoreOwner=WeatherSession, factory=WeatherViewModelFactory())) {
 val s by model.state.collectAsState()
 val navigator=LocalSpatialNavigator.current
 Panel(modifier) {
  Heading("场景控制")
  Copy("城市 · 联动天气与运动窗口")
  cities.chunked(3).forEach { group -> Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
   group.forEach { city -> ToggleableChip(label={Text(city.name)},isToggleOn=city==s.city,onClick={model.onEvent(WeatherEvent.SelectCity(city))}) }
  } }
  ImmersionControls(model)
  Copy("空间天气 · 八种模式")
  Scene.entries.chunked(2).forEach { group -> Row(horizontalArrangement=Arrangement.spacedBy(12.dp)) {
   group.forEach { scene -> ToggleableChip(label={Text(scene.label)},isToggleOn=scene==s.scene,onClick={model.onEvent(WeatherEvent.SelectScene(scene))}) }
  } }
  Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) {
   Copy("夜间场景"); Switch(checked=s.night,onCheckedChange={model.onEvent(WeatherEvent.Night(it))})
  }
  Copy("天气场景与空间效果同步；温度与风速仍来自实时数据。")
  Button(onClick={model.onEvent(WeatherEvent.FollowLive)}) {Text("恢复跟随实时天气")}
  Button(onClick={model.onEvent(WeatherEvent.Refresh)}) {Text(if(s.loading) "正在更新…" else "刷新天气")}
  Button(onClick={navigator.closeWindowContainer()}) {Text("关闭控制窗口")}
 }
}
@Composable fun MovementScreen(modifier: Modifier=Modifier, model: WeatherViewModel = viewModel(viewModelStoreOwner=WeatherSession, factory=WeatherViewModelFactory())) {
 val s by model.state.collectAsState()
 val exercises=model.exercises(s)
 var selected by remember(exercises) {mutableIntStateOf(0)}
 var frame by remember(exercises,selected) {mutableIntStateOf(1)}
 val exercise=exercises[selected]
 val navigator=LocalSpatialNavigator.current
 val context=androidx.compose.ui.platform.LocalContext.current
 val resource=context.resources.getIdentifier(exercise.asset.replace('-','_')+"_"+frame,"drawable",context.packageName)
 Panel(modifier) {
  Heading("轻运动")
  Copy("${s.city.name} · ${s.scene.label} · ${if(s.night) "晚间舒缓" else "适量活动"}")
  exercises.forEachIndexed { index,item -> ToggleableChip(label={Text(item.name)},isToggleOn=index==selected,onClick={selected=index}) }
  if(resource!=0) Image(painterResource(resource),"${exercise.name}示范第 $frame 步",Modifier.fillMaxWidth().height(190.dp),contentScale=ContentScale.Fit)
  Heading(exercise.name); Copy(exercise.dose)
  Row(horizontalArrangement=Arrangement.spacedBy(12.dp)) {
   Button(onClick={frame=if(frame==1) 3 else frame-1}) {Text("上一步")}
   Button(onClick={frame=frame%3+1}) {Text("下一步 $frame/3")}
  }
  Copy("保持舒适幅度，如有不适请停止。建议仅供一般活动参考。")
  Copy("动作图：Everkinetic / Bryl Lim · CC BY-SA 4.0")
  Button(onClick={navigator.closeWindowContainer()}) {Text("关闭运动窗口")}
 }
}
