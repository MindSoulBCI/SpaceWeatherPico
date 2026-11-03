package com.example.spatialweather.p10282026.domain.model
import org.junit.Test
import org.junit.Assert.*
import kotlin.math.abs
class WindMotionTest {
 @Test fun tornadoHasStrongestWind(){Scene.entries.filter{it!=Scene.TORNADO}.forEach{assertTrue(WindMotion.strength(Scene.TORNADO)>WindMotion.strength(it))}}
 @Test fun zeroIntensityStopsTreeMotion(){Scene.entries.forEach{assertEquals(0f,WindMotion.trunk(it,0f,12f),0f);assertEquals(0f,WindMotion.leaf(it,0f,12f,2),0f)}}
 @Test fun treeMotionIsBoundedAndLeavesHaveDifferentPhases(){for(i in 0..1000){assertTrue(abs(WindMotion.trunk(Scene.TORNADO,1f,i*.1f))<=16f);assertTrue(abs(WindMotion.leaf(Scene.TORNADO,1f,i*.1f,3))<=12f)};assertNotEquals(WindMotion.leaf(Scene.WIND,1f,1f,0),WindMotion.leaf(Scene.WIND,1f,1f,1))}
}
