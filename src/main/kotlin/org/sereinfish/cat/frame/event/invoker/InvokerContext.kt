package org.sereinfish.cat.frame.event.invoker

import org.sereinfish.cat.frame.context.Context
import org.sereinfish.cat.frame.context.TypeParser
import org.sereinfish.cat.frame.context.property.valueOrNull
import java.util.*
import java.util.concurrent.ConcurrentHashMap

open class InvokerContext: Context {
    override val data: ConcurrentHashMap<String, Any?> = ConcurrentHashMap()
    override var typeParser: Vector<TypeParser<*>> = Vector()

    /**
     * 结果是不可控的
     * 但以下结果是固定的：
     * 1. 事件过滤器执行后，如果结果不为true，这说明不匹配
     */
    var result: Any? by valueOrNull()
    var exception: Throwable? by valueOrNull()
}