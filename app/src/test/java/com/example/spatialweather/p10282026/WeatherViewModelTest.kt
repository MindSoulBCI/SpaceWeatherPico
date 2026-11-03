package com.example.spatialweather.p10282026
import com.example.spatialweather.p10282026.ui.weather.*
import com.example.spatialweather.p10282026.domain.model.*
import com.example.spatialweather.p10282026.domain.usecase.*
import com.example.spatialweather.p10282026.data.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.*
@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {
 private val dispatcher=StandardTestDispatcher()
 private val weather=Weather(23.0,5.0,3,true,"2026-10-28T12:00")
 private var fail=false
 private val repository=object: WeatherRepository {override suspend fun fetch(city: City): Weather {if(fail) error("network"); return weather}}
 @Before fun setup(){Dispatchers.setMain(dispatcher)}
 @After fun teardown(){Dispatchers.resetMain()}
 @Test fun refreshPopulatesSharedState()=runTest(dispatcher){val vm=WeatherViewModel(repository);vm.onEvent(WeatherEvent.Refresh);advanceUntilIdle();assertEquals(weather,vm.state.value.weather);assertFalse(vm.state.value.loading)}
 @Test fun failureIsVisibleWithoutInventedWeather()=runTest(dispatcher){fail=true;val vm=WeatherViewModel(repository);vm.onEvent(WeatherEvent.Refresh);advanceUntilIdle();assertNotNull(vm.state.value.error);assertNull(vm.state.value.weather)}
 @Test fun manualSceneAndResetKeepRealTemperature()=runTest(dispatcher){val vm=WeatherViewModel(repository);vm.onEvent(WeatherEvent.Refresh);advanceUntilIdle();vm.onEvent(WeatherEvent.SelectScene(Scene.SNOW));assertEquals(Scene.SNOW,vm.state.value.scene);assertEquals(23.0,vm.state.value.weather!!.temperature,0.0);vm.onEvent(WeatherEvent.FollowLive);assertEquals(Scene.CLOUDY,vm.state.value.scene)}
 @Test fun citySwitchRefreshesAndNightAffectsRecommendations()=runTest(dispatcher){val vm=WeatherViewModel(repository);vm.onEvent(WeatherEvent.SelectCity(cities[1]));advanceUntilIdle();assertEquals("北京",vm.state.value.city.name);vm.onEvent(WeatherEvent.Night(true));assertEquals("肩部横向伸展",vm.exercises(vm.state.value).first().name)}
 @Test fun stormRecommendsGentleMovement(){assertEquals("肩部横向伸展",RecommendMovement()(Scene.RAIN,false,weather.copy(code=95)).first().name)}
 @Test fun sunnyDayOffersActiveMovement(){assertEquals("徒手深蹲",RecommendMovement()(Scene.SUNNY,false,weather).first().name)}

 @Test fun everyWeatherModeCanBeSelected() {val vm=WeatherViewModel(repository);Scene.entries.forEach {scene->vm.onEvent(WeatherEvent.SelectScene(scene));assertEquals(scene,vm.state.value.scene)};assertEquals(8,Scene.entries.size)}
 @Test fun intensityClampsAndComfortControlsAreIndependent() {val vm=WeatherViewModel(repository);vm.onEvent(WeatherEvent.Intensity(2f));assertEquals(1f,vm.state.value.intensity,0f);vm.onEvent(WeatherEvent.Intensity(-1f));assertEquals(0f,vm.state.value.intensity,0f);vm.onEvent(WeatherEvent.Pause(true));assertTrue(vm.state.value.paused);assertFalse(vm.state.value.lightning);assertFalse(vm.state.value.sound);vm.onEvent(WeatherEvent.Lightning(true));assertTrue(vm.state.value.lightning);assertFalse(vm.state.value.sound)}
 @Test fun stormCodesResolveToThunderMode() {assertEquals(Scene.THUNDER,weather.copy(code=95).scene);assertEquals(Scene.SNOW,weather.copy(code=73).scene);assertEquals(Scene.RAIN,weather.copy(code=63).scene)}
 @Test fun stageErrorAndExitStateAreVisible() {val vm=WeatherViewModel(repository);vm.onEvent(WeatherEvent.Immersive(false,"blocked"));assertEquals("blocked",vm.state.value.stageError);vm.onEvent(WeatherEvent.Immersive(true));assertTrue(vm.state.value.immersive);assertNull(vm.state.value.stageError);vm.onEvent(WeatherEvent.Immersive(false));assertFalse(vm.state.value.immersive)}
}
