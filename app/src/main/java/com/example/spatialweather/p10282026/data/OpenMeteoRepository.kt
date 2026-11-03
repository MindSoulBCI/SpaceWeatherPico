package com.example.spatialweather.p10282026.data

import com.example.spatialweather.p10282026.domain.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import javax.net.ssl.HttpsURLConnection
class OpenMeteoRepository : WeatherRepository {
 override suspend fun fetch(city: City): Weather = withContext(Dispatchers.IO) {
  val url="https://api.open-meteo.com/v1/forecast?latitude=${city.lat}&longitude=${city.lon}&current=temperature_2m,weather_code,is_day,wind_speed_10m&timezone=auto"
  val connection=URL(url).openConnection() as HttpsURLConnection
  connection.connectTimeout=12000; connection.readTimeout=12000
  try { check(connection.responseCode == 200) { "天气服务暂时不可用" }
   val c=JSONObject(connection.inputStream.bufferedReader().use { it.readText() }).getJSONObject("current")
   Weather(c.getDouble("temperature_2m"),c.getDouble("wind_speed_10m"),c.getInt("weather_code"),c.getInt("is_day")==1,c.getString("time"))
  } finally { connection.disconnect() }
 }
}
