package com.example.spatialweather.p10282026

import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pico.spatial.ui.design.PicoTheme
import com.pico.spatial.ui.foundation.dsl.*
import com.example.spatialweather.p10282026.ui.weather.*
import com.example.spatialweather.p10282026.ui.navigation.Windows
import com.example.spatialweather.p10282026.ui.scene.WeatherStage
import com.pico.spatial.ui.platform.containers.StageStyle

fun mainApp(scope: SpatialAppScope) = with(scope) {
 Stage(id="WeatherEnvironment",immersion=Immersion.Default) { PicoTheme { WeatherStage() } }
 WindowContainer(id="BrainChannels",defaultSize=WindowContainerSize(880.dp,840.dp),placement={containers.firstOrNull{it.id==Windows.MAIN}?.let{Placement.top(it,96.dp)}?:Placement.none()}) {PicoTheme{com.example.spatialweather.p10282026.bci.BciScreen()}}
 WindowContainer(id="MindSoulLogin",defaultSize=WindowContainerSize(560.dp,720.dp),placement={containers.firstOrNull{it.id==Windows.MAIN}?.let{Placement.top(it,48.dp)}?:Placement.none()}) {PicoTheme{com.example.spatialweather.p10282026.account.AccountWindow()}}
 WindowContainer(id="MindSoulCalendar",defaultSize=WindowContainerSize(720.dp,760.dp),placement={containers.firstOrNull{it.id==Windows.CONTROLS}?.let{Placement.left(it,32.dp)}?:Placement.none()}) {PicoTheme{com.example.spatialweather.p10282026.calendar.CalendarWindow()}}
 DefaultWindowContainer {
  PicoTheme { WeatherScreen(Modifier.windowConstraints(minWidth=440.dp,minHeight=620.dp)) }
 }
 WindowContainer(id=Windows.CONTROLS, defaultSize=WindowContainerSize(420.dp,680.dp),
  placement={ containers.firstOrNull { it.id==Windows.MAIN }?.let { Placement.left(it,24.dp) } ?: Placement.none() }) {
  PicoTheme { ControlsScreen(Modifier.windowConstraints(minWidth=400.dp,minHeight=580.dp)) }
 }
 WindowContainer(id=Windows.MOVEMENT, defaultSize=WindowContainerSize(440.dp,680.dp),
  placement={ containers.firstOrNull { it.id==Windows.MAIN }?.let { Placement.right(it,24.dp) } ?: Placement.none() }) {
  PicoTheme { MovementScreen(Modifier.windowConstraints(minWidth=400.dp,minHeight=580.dp)) }
 }
}
