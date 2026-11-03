package com.example.spatialweather.p10282026.calendar
import org.junit.Test
import org.junit.Assert.*
import java.time.LocalDate
import java.time.YearMonth
class CalendarDataTest {
 @Test fun springFestivalUsesLunarDate(){assertTrue(CalendarData.day(LocalDate.of(2025,1,29)).festivals.contains("春节"))}
 @Test fun solarFestivalAndLeapDay(){assertTrue(CalendarData.day(LocalDate.of(2026,10,1)).festivals.contains("国庆节"));assertEquals(29,CalendarData.month(YearMonth.of(2024,2)).count{it!=null})}
 @Test fun gridStartsOnMondayAndNeverDropsDays(){val grid=CalendarData.month(YearMonth.of(2026,11));assertEquals(42,grid.size);assertTrue(grid.take(6).all{it==null});assertEquals(1,grid[6]!!.date.dayOfMonth);assertEquals(30,grid.filterNotNull().last().date.dayOfMonth)}
}
