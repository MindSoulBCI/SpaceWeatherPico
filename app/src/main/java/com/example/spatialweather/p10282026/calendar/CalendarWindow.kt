package com.example.spatialweather.p10282026.calendar
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pico.spatial.ui.design.*
import com.pico.spatial.ui.platform.containers.LocalSpatialNavigator
import com.example.spatialweather.p10282026.ui.weather.components.*
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.YearMonth

@Composable fun rememberToday():LocalDate {
 var today by remember{mutableStateOf(CalendarData.today())}
 LaunchedEffect(Unit){while(true){today=CalendarData.today();delay(1000)}}
 return today
}
@Composable fun TodayCalendarSummary(){
 val today=rememberToday();val day=remember(today){CalendarData.day(today)}
 Copy(day.label)
 Copy("${day.lunar} · ${day.events}")
}
@Composable fun CalendarWindow(){
 val today=rememberToday()
 var month by remember{mutableStateOf(YearMonth.from(today))}
 var selected by remember{mutableStateOf(today)}
 val cells=remember(month){CalendarData.month(month)}
 val detail=remember(selected){CalendarData.day(selected)}
 val nav=LocalSpatialNavigator.current
 Panel(Modifier){
  Heading("日历与节日")
  Copy("北京时间 · 周一为每周首日")
  Row(horizontalArrangement=Arrangement.spacedBy(12.dp)){
   Button(enabled=month>YearMonth.of(1901,1),onClick={month=month.minusMonths(1)}){Text("上一月")}
   Heading("${month.year}年${month.monthValue}月")
   Button(enabled=month<YearMonth.of(2099,12),onClick={month=month.plusMonths(1)}){Text("下一月")}
  }
  Row(Modifier.fillMaxWidth()){listOf("一","二","三","四","五","六","日").forEach{Text(it,modifier=Modifier.weight(1f))}}
  cells.chunked(7).forEach{week->Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(4.dp)){
   week.forEach{day->Box(Modifier.weight(1f).height(62.dp)){
    if(day!=null)ToggleableChip(isToggleOn=day.date==selected,onClick={selected=day.date},label={Column{
     Text(if(day.date==today)"今" else day.date.dayOfMonth.toString())
     Text((day.festivals.firstOrNull()?:day.solarTerm.takeIf{it.isNotBlank()}?:day.lunar.substringAfter("月")).take(4),style=PicoTheme.typography.bodySmall)
    }})
   }}
  }}
  Heading(detail.label)
  Copy(detail.lunar);Copy(detail.events)
  Button(onClick={selected=today;month=YearMonth.from(today)}){Text("回到今天")}
  Copy("节日与节气：lunar-java · MIT。节日名称不代表法定放假或调休安排。")
  Button(onClick={nav.closeWindowContainer()}){Text("关闭日历")}
 }
}
