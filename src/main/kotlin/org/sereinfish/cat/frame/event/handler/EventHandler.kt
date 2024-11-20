package org.sereinfish.cat.frame.event.handler

import org.sereinfish.cat.frame.context.Context
import org.sereinfish.cat.frame.event.Event
import org.sereinfish.cat.frame.event.EventHandlerContext
import org.sereinfish.cat.frame.event.invoker.Invoker
import org.sereinfish.cat.frame.event.invoker.InvokerChain

/**
 * 事件处理器
 */
interface EventHandler<E: Event, C: EventHandlerContext<E>> : Invoker<EventHandler<E, C>, C> {
    // 处理器自带的上下文
    val context: Context

    // 过滤器
    val filter: FilterInvokerChain<EventHandler<E, C>, C>

    override val preProcess: InvokerChain<EventHandler<E, C>, C>
    override val postProcess: InvokerChain<EventHandler<E, C>, C>
    override val exceptionHandle: InvokerChain<EventHandler<E, C>, C>

    /**
     * 匹配处理器
     */
    suspend fun filter(context: C): Boolean {
        filter.invoke(context)
        return context.result == true
    }

    /**
     * 获取上下文
     */
    fun getContext(event: E): EventHandlerContext<E> = EventHandlerContext(event)

    fun handlerContext() = context
}