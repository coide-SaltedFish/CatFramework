package org.sereinfish.cat.frame.event.handler

import org.sereinfish.cat.frame.context.Context
import org.sereinfish.cat.frame.context.SimpleContext
import org.sereinfish.cat.frame.event.Event
import org.sereinfish.cat.frame.event.EventHandlerChain
import org.sereinfish.cat.frame.event.EventHandlerContext
import org.sereinfish.cat.frame.event.invoker.AbstractInvoker
import org.sereinfish.cat.frame.event.invoker.Invoker
import org.sereinfish.cat.frame.event.invoker.InvokerChain

abstract class AbstractEventHandler<E: Event, C: EventHandlerContext<E>>(
    level: Int = 0,
    final override val filter: FilterInvokerChain<EventHandler<E, C>, C> = FilterInvokerChain(),
    preProcess: EventHandlerChain<E, C> = EventHandlerChain(),
    postProcess:EventHandlerChain<E, C> = EventHandlerChain(),
    exceptionHandle:EventHandlerChain<E, C> = EventHandlerChain(),
): EventHandler<E, C>, AbstractInvoker<EventHandler<E, C>, C>(level, preProcess, postProcess, exceptionHandle) {
    override val context: Context = SimpleContext()
}