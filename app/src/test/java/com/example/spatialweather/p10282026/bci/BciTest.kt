package com.example.spatialweather.p10282026.bci
import org.junit.Test
import org.junit.Assert.*
class BciTest {
 @Test fun rejectsMalformedFrames(){BciSession.disconnect();assertFalse(BciSession.submit(List(31){0f},0));assertFalse(BciSession.submit(List(32){Float.NaN},0));assertFalse(BciSession.submit(List(32){20000f},0))}
 @Test fun acceptsCopiesAndRejectsOlderFrames(){BciSession.disconnect();val x=MutableList(32){1f};assertTrue(BciSession.submit(x,20));x[0]=9f;assertEquals(1f,BciSession.frame.value!!.channels[0]);assertFalse(BciSession.submit(x,19));BciSession.disconnect();assertNull(BciSession.frame.value)}
 @Test fun emotionMappingNeverAutoEnablesStorm(){assertTrue(Emotion.entries.none{it.weather.name=="THUNDER"})}
}
