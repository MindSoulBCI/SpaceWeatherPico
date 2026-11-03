package com.example.spatialweather.p10282026.bci
import com.example.spatialweather.p10282026.domain.model.Scene
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class Emotion(val title:String,val weather:Scene,val explanation:String){
 LOW("抑郁 / 低落",Scene.CLOUDY,"你选择了低落体验，配合柔和阴天；这不是抑郁症判断。"),
 HAPPY("开心",Scene.SUNNY,"你选择了开心体验，配合明亮晴天。"),
 SAD("难过",Scene.RAIN,"你选择了难过体验，配合轻雨；可随时改为晴天。"),
 ANXIOUS("焦虑",Scene.SNOW,"你选择了焦虑体验，配合缓慢飘雪；不自动开启雷电。")
}
data class EegFrame(val channels:List<Float>,val receivedAt:Long)
object BciSession {
 private val frames=MutableStateFlow<EegFrame?>(null)
 val frame=frames.asStateFlow()
 /** Device bridge: 32 microvolt values, monotonic reception timestamp. No emotion diagnosis. */
 @Synchronized fun submit(channels:List<Float>,receivedAt:Long):Boolean {
  if(channels.size!=32||channels.any{!it.isFinite()||kotlin.math.abs(it)>10000f}||receivedAt<0||(frames.value?.receivedAt?:-1)>receivedAt)return false
  frames.value=EegFrame(channels.toList(),receivedAt);return true
 }
 fun disconnect(){frames.value=null}
}
