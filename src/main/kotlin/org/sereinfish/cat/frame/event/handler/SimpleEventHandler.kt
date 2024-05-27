package org.sereinfish.cat.frame.event.handler

import org.sereinfish.cat.frame.event.Event
import org.sereinfish.cat.frame.event.EventHandlerContext

abstract class SimpleEventHandler(
    val eventType: Class<out Event>, // 事件类型
): AbstractEventHandler<Event, EventHandlerContext<Event>>() {

    init {
        filter.add(object : SimpleEventHandler(eventType) {
            override suspend fun process(context: EventHandlerContext<Event>) {
                context.result = eventType.isAssignableFrom(context.event::class.java)
            }
        })
    }
}