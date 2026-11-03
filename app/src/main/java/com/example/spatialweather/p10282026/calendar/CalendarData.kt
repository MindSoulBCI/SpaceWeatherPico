package com.example.spatialweather.p10282026.calendar
import com.nlf.calendar.Solar
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

data class CalendarDay(val date:LocalDate,val lunar:String,val festivals:List<String>,val solarTerm:String){
 val label:String get()=date.format(DateTimeFormatter.ofPattern("yyyy年M月d日 EEEE",Locale.SIMPLIFIED_CHINESE))
 val events:String get()=(festivals+solarTerm.takeIf{it.isNotBlank()}.let{listOfNotNull(it)}).joinToString(" · ").ifEmpty{"今日无主要节日 / 节气"}
}
object CalendarData {
 val zone:ZoneId=ZoneId.of("Asia/Shanghai")
 fun today():LocalDate=LocalDate.now(zone)
 fun day(date:LocalDate):CalendarDay {
  val solar=Solar.fromYmd(date.year,date.monthValue,date.dayOfMonth)
  val lunar=solar.lunar
  return CalendarDay(date,"农历${lunar.monthInChinese}月${lunar.dayInChinese}",(solar.festivals+lunar.festivals).distinct(),lunar.jieQi)
 }
 fun month(month:YearMonth):List<CalendarDay?> {
  val before=month.atDay(1).dayOfWeek.value-1
  val days=List<CalendarDay?>(before){null}+(1..month.lengthOfMonth()).map{day(month.atDay(it))}
  return days+List((7-days.size%7)%7){null}
 }
}
