package org.sereinfish.cat.frame.event.handler

import org.sereinfish.cat.frame.event.invoker.Invoker
import org.sereinfish.cat.frame.event.invoker.InvokerChain
import org.sereinfish.cat.frame.event.invoker.InvokerContext

interface FilterInvoker<C: InvokerContext>: Invoker<FilterInvoker<C>, C>