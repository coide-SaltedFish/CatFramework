package org.sereinfish.cat.frame.event

import org.sereinfish.cat.frame.event.handler.EventHandler
import org.sereinfish.cat.frame.event.invoker.InvokerChain

/**
 * 事件处理链
 */
class EventHandlerChain<E: Event, C: EventHandlerContext<E>>: InvokerChain<EventHandler<E, C>, C>() {

    /**
     * 执行执行链
     */
    override suspend fun invoke(context: C) {
        forEach { it.invoke(context) }
    }

    override fun add(element: EventHandler<E, C>): Boolean {
        return super.add(element)
    }

    override fun addAll(elements: Collection<EventHandler<E, C>>): Boolean {
        return super.addAll(elements)
    }
}