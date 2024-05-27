package org.sereinfish.cat.frame.log

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.pattern.color.ForegroundCompositeConverterBase

class CatHighlightingCompositeConverter<E>: ForegroundCompositeConverterBase<E>() {
    override fun getForegroundColorCode(event: E): String {
        if (event is ILoggingEvent) {
            return when (event.level.toInt()) {
                Level.ERROR_INT -> "1;31" // 红色
                Level.WARN_INT -> "1;33" // 黄色
                Level.INFO_INT -> "1;32" // 绿色
                Level.DEBUG_INT -> "1;34" // 蓝色
                else -> "1;37" // 默认为白色
            }
        }
        return "1;37"
    }
}