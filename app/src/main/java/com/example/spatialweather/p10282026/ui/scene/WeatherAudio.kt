package com.example.spatialweather.p10282026.ui.scene

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlin.math.*
import kotlin.random.Random
import com.example.spatialweather.p10282026.domain.model.Scene

/** Scene-owned, locally synthesized ambience. No recordings or network downloads. */
class WeatherAudio {
 private val rate=22050
 private fun track(thunder: Boolean): AudioTrack {
  val count=rate*4
  val samples=ShortArray(count)
  val random=Random(if(thunder) 71 else 17)
  var low=0f
  repeat(count) { i ->
   val noise=random.nextFloat()*2-1
   low=low*0.97f+noise*0.03f
   val t=i.toFloat()/rate
   val envelope=if(thunder) (1-exp(-t*12))*exp(-t*0.95f) else min(1f,min(t*8,(4-t)*8))
   samples[i]=((if(thunder) low*3 else low+noise*0.12f)*envelope*12000).toInt().coerceIn(-32767,32767).toShort()
  }
  return AudioTrack.Builder().setAudioAttributes(AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build())
   .setAudioFormat(AudioFormat.Builder().setSampleRate(rate).setEncoding(AudioFormat.ENCODING_PCM_16BIT).setChannelMask(AudioFormat.CHANNEL_OUT_MONO).build())
   .setTransferMode(AudioTrack.MODE_STATIC).setBufferSizeInBytes(count*2).build().apply {write(samples,0,count); if(!thunder) setLoopPoints(0,count,-1)}
 }
 private val ambience=track(false)
 private val rumble=track(true)
 private var playing=false
 fun update(enabled: Boolean,scene: Scene,intensity: Float) {
  val shouldPlay=enabled && scene in listOf(Scene.RAIN,Scene.WIND,Scene.THUNDER)
  if(shouldPlay && !playing) ambience.play()
  if(!shouldPlay && playing) ambience.pause()
  playing=shouldPlay
  ambience.setVolume((intensity*0.24f).coerceIn(0f,0.24f))
  if(!enabled) rumble.pause()
 }
 fun thunder() {rumble.stop();rumble.reloadStaticData();rumble.setVolume(0.38f);rumble.play()}
 fun close(){ambience.release();rumble.release()}
}
