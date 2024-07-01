package org.sereinfish.cat.frame.utils

import java.util.Calendar

fun Calendar.nowYear(): Int {
    return get(Calendar.YEAR)
}

fun Calendar.nowMonth(): Int {
    return get(Calendar.MONTH) + 1
}

fun Calendar.nowDayOfMonth(): Int {
    return get(Calendar.DAY_OF_MONTH)
}

fun Calendar.nowHour(): Int {
    return get(Calendar.HOUR_OF_DAY)
}

fun Calendar.nowMinute(): Int {
    return get(Calendar.MINUTE)
}

fun Calendar.nowSecond(): Int {
    return get(Calendar.SECOND)
}

fun Calendar.nowMillisecond(): Int {
    return get(Calendar.MILLISECOND)
}

fun Calendar.nowWeekOfYear(): Int {
    return get(Calendar.WEEK_OF_YEAR)
}

fun Calendar.nowWeekOfMonth(): Int {
    return get(Calendar.WEEK_OF_MONTH)
}

fun Calendar.nowDayOfWeek(): Int {
    return get(Calendar.DAY_OF_WEEK)
}