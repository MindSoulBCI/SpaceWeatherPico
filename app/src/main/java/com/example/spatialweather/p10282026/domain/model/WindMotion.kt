package com.example.spatialweather.p10282026.domain.model
import kotlin.math.sin
/** Native adaptation of StylizedTrees direction/speed/strength controls. Angles in degrees. */
object WindMotion {
 fun strength(scene:Scene)=when(scene){Scene.SUNNY->.08f;Scene.CLOUDY->.15f;Scene.SNOW->.20f;Scene.RAIN->.35f;Scene.DARK_CLOUDS->.45f;Scene.WIND->.65f;Scene.THUNDER->.8f;Scene.TORNADO->1f}
 fun trunk(scene:Scene,intensity:Float,time:Float):Float {
  val wind=strength(scene)*intensity.coerceIn(0f,1f)
  return wind*(5f+sin(time*(.7f+wind*1.5f))*9f+sin(time*2.7f)*2f)
 }
 fun leaf(scene:Scene,intensity:Float,time:Float,index:Int):Float {
  val wind=strength(scene)*intensity.coerceIn(0f,1f)
  return sin(time*(1.2f+wind*4f)+index*1.7f)*wind*12f
 }
}
