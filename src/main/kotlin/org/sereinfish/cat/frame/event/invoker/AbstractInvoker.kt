package org.sereinfish.cat.frame.event.invoker

abstract class AbstractInvoker<I: Invoker<I, C>, C: InvokerContext>(
    override val level: Int = 0,
    override val preProcess: InvokerChain<I, C> = InvokerChain(),
    override val postProcess: InvokerChain<I, C> = InvokerChain(),
    override val exceptionHandle: InvokerChain<I, C> = InvokerChain(),
): Invoker<I, C>