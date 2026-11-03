package com.example.spatialweather.p10282026.domain.usecase

import com.example.spatialweather.p10282026.domain.model.*
class RecommendMovement {
 operator fun invoke(scene: Scene, night: Boolean, weather: Weather?): List<Exercise> {
  val gentle = scene==Scene.TORNADO || scene==Scene.THUNDER || scene==Scene.WIND || night || weather?.let { it.temperature>=32 || it.temperature<=0 || it.wind>=35 || it.code>=95 } == true
  return if(gentle) listOf(Exercise("肩部横向伸展","每侧 20 秒","cross-body-shoulder-stretch"),Exercise("猫牛式伸展","2 组 · 每组 6 次","cat-cow-stretch"))
  else if(scene==Scene.RAIN || scene==Scene.SNOW) listOf(Exercise("靠墙俯卧撑","2 组 · 每组 8 次","wall-push-up"),Exercise("猫牛式伸展","2 组 · 每组 6 次","cat-cow-stretch"))
  else listOf(Exercise("徒手深蹲","2 组 · 每组 8 次","bodyweight-squat"),Exercise("站姿股四头肌伸展","每侧 20 秒","standing-quad-stretch"))
 }
}
