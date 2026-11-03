package com.example.spatialweather.p10282026.ui.scene

import android.util.Log
import com.pico.spatial.core.ecs.*
import com.pico.spatial.core.ecs.resource.*
import com.pico.spatial.core.math.*
import com.example.spatialweather.p10282026.domain.model.Scene
import com.example.spatialweather.p10282026.domain.model.WindMotion
import com.example.spatialweather.p10282026.ui.weather.WeatherUiState
import kotlinx.coroutines.*
import kotlin.math.*
import kotlin.random.Random

/** Unbounded Mixed Stage; all positions use meters in identity placementOwner space. */
class WeatherScene {
 val root=Entity().apply {setName("WeatherPlacementOwner")}
 private val random=Random(1028)
 private val meshes=mutableListOf<MeshResource>()
 private val materials=mutableListOf<Material>()
 private val particles=mutableListOf<Entity>()
 private val locations=mutableListOf<Vector3>()
 private val clouds=mutableListOf<Entity>()
 private val cloudLocations=mutableListOf<Vector3>()
 private val windTree=Entity()
 private val treeLeaves=mutableListOf<Entity>()
 private val boltVariants=mutableListOf<Entity>()
 private var tornado:Entity?=null
 private var tornadoRibbons:Entity?=null
 private var tornadoWind:Entity?=null
 private var sourceSky:Entity?=null
 private var rainLayer:Entity?=null
 private var skyLayer:Entity?=null
 private var dayClouds:Entity?=null
 private var overcastClouds:Entity?=null
 private lateinit var sun: Entity
 private lateinit var lightning: Entity
 private lateinit var cloudMaterial: UnlitMaterial
 private lateinit var light: DirectionalLightComponent
 private var audio: WeatherAudio?=null
 private var elapsed=0f
 private var stormClock=0f
 private var flashTime=0f
 private var lightningWasEnabled=false
 private var previous: Scene?=null
 private var closed=false
 private lateinit var box: MeshResource
 private lateinit var sphere: MeshResource
 private lateinit var rainMaterial: UnlitMaterial
 private lateinit var snowMaterial: UnlitMaterial
 private fun transform(entity: Entity)=entity.components[TransformComponent::class.java]!!
 private fun part(name: String,mesh: MeshResource,material: Material,position: Vector3,scale: Vector3): Entity = ModelEntity(mesh,material).apply {
  setName(name);transform(this).setPosition(position);transform(this).setScaleVector(scale);root.addChild(this)
 }
 suspend fun create() {
  // Allocate each resource once and share across all weather instances.
  withContext(Dispatchers.IO) {
   box=MeshResource.createBox(Vector3(1f));sphere=MeshResource.createSphere(1f)
   meshes.addAll(listOf(box,sphere))
   rainMaterial=UnlitMaterial.create().apply {setBaseColor(Color4(0.52f,0.76f,0.94f,1f))}
   snowMaterial=UnlitMaterial.create().apply {setBaseColor(Color4(0.94f,0.98f,1f,1f))}
   cloudMaterial=UnlitMaterial.create().apply {setBaseColor(Color4(0.67f,0.72f,0.78f,1f))}
   materials.addAll(listOf(rainMaterial,snowMaterial,cloudMaterial))
   audio=WeatherAudio()
  }
  dayClouds=withContext(Dispatchers.IO){Entity.load("asset://weather/cloudsky_day.glb")}.also{root.addChild(it)}
  overcastClouds=withContext(Dispatchers.IO){Entity.load("asset://weather/cloudsky_overcast.glb")}.also{root.addChild(it)}
  skyLayer=withContext(Dispatchers.IO){Entity.load("asset://weather/cloud_layer.glb")}.also{root.addChild(it)}
  rainLayer=withContext(Dispatchers.IO){Entity.load("asset://weather/rain_layer.glb")}.also{transform(it).setPosition(Vector3(0f,0f,-8f));root.addChild(it)}
  sourceSky=withContext(Dispatchers.IO){Entity.load("asset://weather/source_skysphere.glb")}.also{transform(it).setScaleVector(Vector3(25f));root.addChild(it)}
  tornado=withContext(Dispatchers.IO){Entity.load("asset://weather/tornado.glb")}.also{transform(it).setScaleVector(Vector3(.35f));transform(it).setPosition(Vector3(0f,0f,-9f));root.addChild(it)}
  tornadoRibbons=withContext(Dispatchers.IO){Entity.load("asset://weather/tornado_ribbons.glb")}.also{root.addChild(it)}
  tornadoWind=withContext(Dispatchers.IO){Entity.load("asset://weather/tornado_wind.glb")}.also{root.addChild(it)}
  windTree.setName("StylizedTreesWindTree");root.addChild(windTree);transform(windTree).setPosition(Vector3(-3.3f,0f,-5.5f));transform(windTree).setScaleVector(Vector3(1.3f))
  withContext(Dispatchers.IO){Entity.load("asset://weather/tree/leaf_0.glb")}.also{windTree.addChild(it);transform(it).setPosition(Vector3(0.032920800149440765f,1.7657033205032349f,-0.3382050096988678f));treeLeaves.add(it)}
  withContext(Dispatchers.IO){Entity.load("asset://weather/tree/leaf_1.glb")}.also{windTree.addChild(it);transform(it).setPosition(Vector3(0.6760131120681763f,1.5293132066726685f,-0.219017893075943f));treeLeaves.add(it)}
  withContext(Dispatchers.IO){Entity.load("asset://weather/tree/leaf_2.glb")}.also{windTree.addChild(it);transform(it).setPosition(Vector3(-0.5948307514190674f,1.6934460401535034f,0.21685408055782318f));treeLeaves.add(it)}
  withContext(Dispatchers.IO){Entity.load("asset://weather/tree/leaf_3.glb")}.also{windTree.addChild(it);transform(it).setPosition(Vector3(-0.2922181487083435f,2.087348699569702f,0.10528171807527542f));treeLeaves.add(it)}
  withContext(Dispatchers.IO){Entity.load("asset://weather/tree/leaf_4.glb")}.also{windTree.addChild(it);transform(it).setPosition(Vector3(0.22385868430137634f,1.8539741039276123f,0.4297342896461487f));treeLeaves.add(it)}
  withContext(Dispatchers.IO){Entity.load("asset://weather/tree/leaf_5.glb")}.also{windTree.addChild(it);transform(it).setPosition(Vector3(0.07139639556407928f,2.0597946643829346f,0.2129746973514557f));treeLeaves.add(it)}
  withContext(Dispatchers.IO){Entity.load("asset://weather/tree/trunk.glb")}.also{windTree.addChild(it);transform(it).setPosition(Vector3(0.0f,0.0f,-0.0f));}
  val warm=UnlitMaterial.create().apply {setBaseColor(Color4(1f,0.78f,0.30f,1f))}.also{materials.add(it)}
  sun=part("Sun",sphere,warm,Vector3(-3f,4.8f,-7f),Vector3(0.7f))
  val lightEntity=Entity().apply{setName("WeatherLight")}
  light=DirectionalLightComponent(Color4(1f,0.95f,0.85f,1f),1200f,castsShadowEnabled=false)
  lightEntity.components.set(light);transform(lightEntity).setEulerAngles(EulerAngles(-65f,25f,0f));root.addChild(lightEntity)
  repeat(18) { i ->
   val angle=i*2*PI.toFloat()/18
   val position=Vector3(cos(angle)*6f,4.6f+(i%3)*0.35f,sin(angle)*6f)
   cloudLocations.add(position)
   clouds.add(part("Cloud_$i",sphere,cloudMaterial,position,Vector3(.9f,0.22f+(i%3)*0.06f,.65f)))
  }
  repeat(120) { i ->
   val position=spawn(i)
   locations.add(position)
   particles.add(part("Precipitation_$i",box,rainMaterial,position,Vector3(0.012f,0.20f,0.012f)))
  }
  lightning=Entity().apply{setName("Lightning");root.addChild(this)}
  repeat(4){index->
   val bolt=withContext(Dispatchers.IO){Entity.load("asset://weather/spektr_bolt_$index.glb")}
   transform(bolt).setPosition(Vector3(2.2f,2.4f,-6f));transform(bolt).setScaleVector(Vector3(.7f));lightning.addChild(bolt);bolt.enabled=false;boltVariants.add(bolt)
  }
  lightning.enabled=false
 }
 private fun spawn(index: Int): Vector3 {
  // An annular volume guarantees particles do not cross the 2.5 m interaction zone.
  val a=random.nextFloat()*2*PI.toFloat()
  val r=3.2f+random.nextFloat()*4.0f
  return Vector3(cos(a)*r,random.nextFloat()*6f-0.4f,sin(a)*r)
 }
 fun measure() {
  val bounds=root.getVisualBounds(root)
  Log.i("WeatherScene","owner=WeatherPlacementOwner units=meters boundsMin=${bounds.min} boundsMax=${bounds.max} resources=2_meshes particles=120 clouds=18")
 }
 fun update(s: WeatherUiState,dt: Float) {
  if(closed) return
  if(!s.paused) {elapsed+=dt;stormClock+=dt}
  val weather=s.scene
  transform(windTree).setEulerAngles(EulerAngles(0f,0f,WindMotion.trunk(weather,s.intensity,elapsed)))
  treeLeaves.forEachIndexed{i,e->val angle=WindMotion.leaf(weather,s.intensity,elapsed,i);transform(e).setEulerAngles(EulerAngles(angle*.45f,angle*.3f,angle))}
  rainLayer?.enabled=false // Unity rain texture needs a particle shader; avoid an opaque billboard in Mixed space.
  rainLayer?.components?.get(TransformComponent::class.java)?.setPosition(Vector3(0f,-(elapsed*.5f%1f),-8f))
  skyLayer?.enabled=false // Superseded by the CloudSkybox volume-noise adaptation.
  dayClouds?.enabled=weather in listOf(Scene.SUNNY,Scene.WIND)
  overcastClouds?.enabled=weather !in listOf(Scene.SUNNY,Scene.WIND,Scene.TORNADO)
  listOf(dayClouds,overcastClouds).forEach{it?.let{e->transform(e).setEulerAngles(EulerAngles(0f,elapsed*.45f,0f))}}
  skyLayer?.components?.get(TransformComponent::class.java)?.setEulerAngles(EulerAngles(0f,elapsed*.35f,0f))
  tornado?.enabled=weather==Scene.TORNADO&&s.intensity>0f
  tornado?.let{transform(it).setScaleVector(Vector3(.12f+s.intensity*.10f));transform(it).setPosition(Vector3(sin(elapsed*.2f)*.35f,2.8f,-6f));transform(it).setEulerAngles(EulerAngles(0f,elapsed*(35f+s.intensity*65f),0f))}
  listOf(tornadoRibbons,tornadoWind).forEachIndexed{index,e->e?.let{it.enabled=weather==Scene.TORNADO&&s.intensity>0f;transform(it).setScaleVector(Vector3(.12f+s.intensity*.10f));transform(it).setPosition(Vector3(sin(elapsed*.2f)*.35f,2.8f,-6f));transform(it).setEulerAngles(EulerAngles(0f,elapsed*(if(index==0) 65f else -100f),0f))}}
  sourceSky?.enabled=false // Preserve the PICO system room in Mixed space.
  val dark=weather==Scene.DARK_CLOUDS || weather==Scene.THUNDER || weather==Scene.TORNADO
  sun.enabled=weather==Scene.SUNNY && !s.night
  clouds.forEachIndexed { i,e ->
   e.enabled=false // Replaced flat ellipsoid clouds with the baked volume canopy.
   val p=cloudLocations[i]
   transform(e).setPosition(Vector3(p.x+sin(elapsed*0.05f+i)*0.35f,p.y,p.z))
  }
  if(previous!=weather) {
   cloudMaterial.setBaseColor(if(dark) Color4(0.35f,0.40f,0.48f,1f) else Color4(0.68f,0.73f,0.80f,1f))
   // Keep model ownership stable: replacing shared components releases their scoped resources.
   rainMaterial.setBaseColor(if(weather==Scene.SNOW) Color4(.94f,.98f,1f,1f) else Color4(.52f,.76f,.94f,1f))
   particles.forEach { e ->
    transform(e).setScaleVector(when(weather) {Scene.SNOW->Vector3(0.025f);Scene.WIND->Vector3(0.35f,0.008f,0.008f);else->Vector3(0.012f,0.20f,0.012f)})
   }
   previous=weather;stormClock=0f;flashTime=0f
   Log.i("WeatherScene","mode=$weather entityCount=${particles.size+clouds.size+9}")
  }
  val precipitation=weather in listOf(Scene.RAIN,Scene.SNOW,Scene.THUNDER,Scene.WIND,Scene.TORNADO)
  val count=(120*s.intensity).toInt()
  particles.forEachIndexed { i,e ->
   e.enabled=precipitation && i<count
   if(e.enabled && !s.paused) {
    if(weather==Scene.TORNADO){
     val angle=elapsed*(1f+s.intensity*3f)+i*.55f
     val height=(i%20)/20f*2f
     val radius=.10f+height*.25f
     transform(e).setPosition(Vector3(sin(elapsed*.2f)*.35f+cos(angle)*radius,height+2.8f,-6f+sin(angle)*radius))
     transform(e).setScaleVector(Vector3(.015f,.008f,.015f))
     return@forEachIndexed
    }
    val old=locations[i]
    val speed=when(weather){Scene.SNOW->0.45f;Scene.WIND->0.06f;else->3.8f}
    val drift=if(weather==Scene.WIND) 2.4f else if(weather==Scene.THUNDER) 1.1f else 0.25f
    var x=old.x+drift*dt; var y=old.y-speed*dt; var z=old.z
    if(y < -0.5f || x>7.8f || x*x+z*z<9f) {
     val fresh=spawn(i);x=fresh.x;y=if(weather==Scene.WIND) fresh.y else 5.5f;z=fresh.z
    }
    val position=Vector3(x,y,z);locations[i]=position;transform(e).setPosition(position)
   }
  }
  val lightningEnabled=s.lightning && !s.paused && s.intensity>0f
  if(lightningEnabled && (!lightningWasEnabled || stormClock>=2f)) {
   stormClock=0f;flashTime=.36f;if(s.sound)audio?.thunder()
  }
  if(!lightningEnabled) flashTime=0f
  else flashTime=(flashTime-dt).coerceAtLeast(0f)
  lightningWasEnabled=lightningEnabled
  val flash=lightningEnabled && flashTime>0f
  lightning.enabled=flash
  val boltFrame=((.36f-flashTime)/.09f).toInt().coerceIn(0,3)
  boltVariants.forEachIndexed{i,e->e.enabled=flash&&i==boltFrame}
  light.intensity=if(flash) 2400f else if(s.night) 80f else if(dark) 180f else if(weather==Scene.SUNNY) 1400f else 550f
  audio?.update(s.sound && !s.paused,weather,s.intensity)
 }
 fun close() {
  if(closed)return
  closed=true; root.destroy();audio?.close();audio=null
  meshes.forEach{it.close()};materials.forEach{it.close()}
 }
}
