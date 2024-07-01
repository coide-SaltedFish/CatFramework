package org.sereinfish.cat.frame.event

import org.sereinfish.cat.frame.context.property.value
import org.sereinfish.cat.frame.context.property.valueOrElse
import org.sereinfish.cat.frame.event.invoker.InvokerContext

/**
 * 事件处理器上下文
 */
open class EventHandlerContext<out E: Event>(
    event: E
): InvokerContext() {

    val event: E by value()

    // 是否终止事件处理
    var stopHandler: Boolean by valueOrElse { true }

    init {
        set("event", event)
    }
}