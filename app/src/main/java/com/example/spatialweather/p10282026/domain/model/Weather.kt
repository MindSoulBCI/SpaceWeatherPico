package com.example.spatialweather.p10282026.domain.model

enum class Scene(val label: String) { TORNADO("龙卷风"), SUNNY("晴天"), CLOUDY("阴天"), RAIN("雨天"), SNOW("雪天"), WIND("风"), DARK_CLOUDS("乌云"), THUNDER("雷暴") }
data class City(val name: String, val lat: Double, val lon: Double)
val cities = listOf(City("成都",30.5728,104.0668),City("北京",39.9042,116.4074),City("上海",31.2304,121.4737),City("深圳",22.5431,114.0579),City("哈尔滨",45.8038,126.535))
data class Weather(val temperature: Double, val wind: Double, val code: Int, val isDay: Boolean, val time: String) {
 val scene: Scene get() = when(code) { 0,1 -> Scene.SUNNY; 95,96,99 -> Scene.THUNDER; 71,73,75,77,85,86 -> Scene.SNOW; 51,53,55,56,57,61,63,65,66,67,80,81,82 -> Scene.RAIN; else -> Scene.CLOUDY }
}
data class Exercise(val name: String, val dose: String, val asset: String)
