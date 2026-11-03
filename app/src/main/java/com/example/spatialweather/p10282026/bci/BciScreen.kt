package com.example.spatialweather.p10282026.bci
import android.os.SystemClock
import androidx.compose.runtime.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pico.spatial.ui.design.*
import com.pico.spatial.ui.platform.containers.LocalSpatialNavigator
import com.example.spatialweather.p10282026.ui.weather.*
import com.example.spatialweather.p10282026.ui.weather.components.*
import kotlinx.coroutines.delay
import kotlin.math.sin

@Composable fun BciScreen(model:WeatherViewModel=viewModel(viewModelStoreOwner=WeatherSession,factory=WeatherViewModelFactory())){
 val s by model.state.collectAsState()
 val frame by BciSession.frame.collectAsState()
 var simulated by remember{mutableStateOf(true)}
 var paused by remember{mutableStateOf(false)}
 var now by remember{mutableLongStateOf(SystemClock.elapsedRealtime())}
 var phase by remember{mutableFloatStateOf(0f)}
 var history by remember{mutableStateOf(List(32){List(120){0f}})}
 var last by remember{mutableLongStateOf(-1)}
 LaunchedEffect(simulated,paused){while(true){delay(50);now=SystemClock.elapsedRealtime();if(!paused){phase+=.05f
  val current=BciSession.frame.value
  val values=if(simulated)List(32){i->(sin(phase*(8+i*.13f))+sin(phase*2.2f+i)*.4f)*18f} else current?.takeIf{now-it.receivedAt in 0..2000&&it.receivedAt!=last}?.channels
  if(values!=null){history=history.mapIndexed{i,points->points.drop(1)+values[i]};last=current?.receivedAt?:-1}
 }}}
 val valid=simulated||(frame!=null&&now-frame!!.receivedAt in 0..2000)
 val color=PicoTheme.colorScheme.labelPrimary
 val nav=LocalSpatialNavigator.current
 Panel(Modifier){
  Heading("MindSoul · 32 通道脑机看板")
  Copy(if(simulated)"模拟波形 · 非真实 EEG · 20 Hz 可视化" else if(valid)"外部数据 · µV · 接收正常" else "设备未连接 / 数据过期 · 无有效信号")
  Row(horizontalArrangement=Arrangement.spacedBy(12.dp)){
   Button(onClick={simulated=!simulated;history=List(32){List(120){0f}};last=-1}){Text(if(simulated)"切换真实接入口" else "返回模拟")}
   Button(onClick={paused=!paused}){Text(if(paused)"继续波形" else "暂停波形")}
  }
  Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(20.dp)){
   repeat(2){column->Column(Modifier.weight(1f)){
    repeat(16){row->val index=column*16+row
     Row(Modifier.height(22.dp),horizontalArrangement=Arrangement.spacedBy(8.dp)){
      Text("CH${(index+1).toString().padStart(2,'0')}",modifier=Modifier.width(52.dp),style=PicoTheme.typography.bodySmall)
      Canvas(Modifier.weight(1f).fillMaxHeight()){
       if(valid){val points=history[index];val path=Path();points.forEachIndexed{j,v->val x=j*size.width/(points.size-1);val y=size.height/2-v.coerceIn(-100f,100f)/100*size.height*.45f;if(j==0)path.moveTo(x,y)else path.lineTo(x,y)};drawPath(path,color,style=androidx.compose.ui.graphics.drawscope.Stroke(1.5f))}
       else drawLine(color.copy(alpha=.25f),Offset(0f,size.height/2),Offset(size.width,size.height/2))
      }
     }
    }
   }}
  }
  Copy("显示量程 ±100 µV · 超范围仅裁切显示，不修改原始输入。暂停仅冻结波形。")
  Heading("情绪体验 · 手动选择")
  Copy("情绪解码模型未接入，当前没有推断结果或置信度。下列标签是自述体验，不是抑郁症、焦虑症诊断。")
  Emotion.entries.chunked(2).forEach{group->Row(horizontalArrangement=Arrangement.spacedBy(12.dp)){
   group.forEach{emotion->ToggleableChip(label={Text(emotion.title)},isToggleOn=s.emotion==emotion,onClick={model.onEvent(WeatherEvent.EmotionExperience(emotion,s.emotionLinked))})}
  }}
  Row(horizontalArrangement=Arrangement.spacedBy(12.dp)){
   Text("按情绪体验切换天气")
   Switch(checked=s.emotionLinked,onCheckedChange={model.onEvent(WeatherEvent.EmotionExperience(s.emotion?:Emotion.HAPPY,it))})
  }
  s.emotion?.let{Copy(it.explanation)}
  Copy("手动选择天气会退出情绪联动，雷电默认关闭。")
  Button(onClick={nav.openWindowContainer("WeatherControls","singleton")}){Text("手动切换天气")}
  Copy("成都市脑云矩阵科技有限公司 · MindSoul 提供")
  Button(onClick={nav.closeWindowContainer()}){Text("关闭看板")}
 }
}
