package org.sereinfish.cat.frame.timer

import org.sereinfish.cat.frame.utils.*
import java.text.SimpleDateFormat
import java.util.*

data class TimerData(
    val year: Int?= null,
    val month: Int?= null,
    val day: Int?= null,
    val hour: Int?= null,
    val minute: Int?= null,
    val second: Int?= null,
    val millisecond: Int?= null,
){

    /**
     * 计算下一次时间，返回下一次时间与当前时间的间隔 ms
     *
     * 如果设定值为 null，取当前值
     *
     * @return 下一次时间与当前时间的间隔 ms
     */
    fun delayNext(): Long {
        val startTime = System.currentTimeMillis()
        val calendar = calender()

        if (calendar.timeInMillis < System.currentTimeMillis()) {
            if (millisecond.isNull()){
                calendar.set(Calendar.MILLISECOND, calendar.nowMillisecond() + 1)
            }else if (second.isNull()){
                calendar.set(Calendar.SECOND, calendar.nowSecond() + 1)
            }else if (minute.isNull()){
                calendar.set(Calendar.MINUTE, calendar.nowMinute() + 1)
            }else if (hour.isNull()){
                calendar.set(Calendar.HOUR_OF_DAY, calendar.nowHour() + 1)
            }else if (day.isNull()){
                calendar.set(Calendar.DAY_OF_MONTH,calendar.nowDayOfMonth() + 1)
            }else if (month.isNull()){
                calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1)
            }else if (year.isNull()){
                calendar.set(Calendar.YEAR, calendar.nowYear() + 1)
            }
        }

        println("目标：${SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.timeInMillis)}")

        val endTime = System.currentTimeMillis()
        return calendar.timeInMillis - endTime - (endTime - startTime)
    }

    private fun calender() = Calendar.getInstance().apply {
        year?.let { set(Calendar.YEAR, year) }
        month?.let { set(Calendar.MONTH, month) }
        day?.let { set(Calendar.DAY_OF_MONTH, day) }
        hour?.let { set(Calendar.HOUR_OF_DAY, hour) }
        minute?.let { set(Calendar.MINUTE, minute) }
        second?.let { set(Calendar.SECOND, second) }
        millisecond?.let { set(Calendar.MILLISECOND, millisecond) }
    }
}