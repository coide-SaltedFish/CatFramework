package org.sereinfish.cat.frame.event.invoker

import java.util.concurrent.PriorityBlockingQueue

/**
 * 处理链
 */
open class InvokerChain<I: Invoker<I, C>, C: InvokerContext>: PriorityBlockingQueue<I>(
    11,
    Comparator<I> { e1, e2 ->
        e1.level - e2.level  // 数越大越后面
    }
) {
    /**
     * 执行执行链
     */
    open suspend fun invoke(context: C){
        forEach { it.invoke(context) }
    }

    override fun add(element: I): Boolean {
        return super.add(element)
    }

    override fun addAll(elements: Collection<I>): Boolean {
        return super.addAll(elements)
    }
}