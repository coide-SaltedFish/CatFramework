package org.sereinfish.cat.frame.event.invoker

import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

/**
 * 处理链
 */
open class InvokerChain<I: Invoker<I, C>, C: InvokerContext>: CopyOnWriteArrayList<I>() {
    /**
     * 执行执行链
     */
    open suspend fun invoke(context: C){
        forEach { it.invoke(context) }
    }

    override fun add(element: I): Boolean {
        return super.add(element).also {
            sortBy { it.level }
        }
    }

    override fun addAll(elements: Collection<I>): Boolean {
        return super.addAll(elements).also {
            sortBy { it.level }
        }
    }
}