package org.sereinfish.cat.frame.event.events

import org.sereinfish.cat.frame.event.Event
import java.lang.management.ManagementFactory

/**
 * 框架事件
 */
abstract class CatFrameEvent: Event

class CatFrameStartEvent private constructor(): CatFrameEvent() {

    internal companion object {
        fun build() = CatFrameStartEvent()
    }

    override fun toLogString(): String {
        val time = System.currentTimeMillis() - ManagementFactory.getRuntimeMXBean().startTime
        return "CatFrameStartEvent[${String.format("%.2f", (time.toFloat() / 1000))}s]"
    }
}

class CatFrameCloseEvent private constructor(): CatFrameEvent() {

    internal companion object {
        fun build() = CatFrameCloseEvent()
    }

    override fun toLogString(): String {
        val time = System.currentTimeMillis() - ManagementFactory.getRuntimeMXBean().startTime
        return "CatFrameCloseEvent[${String.format("%.2f", (time.toFloat() / 1000))}s]"
    }
}