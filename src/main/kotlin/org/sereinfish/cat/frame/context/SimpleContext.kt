package org.sereinfish.cat.frame.context

import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * 上下文简单实现
 */
class SimpleContext(
    override val data: ConcurrentHashMap<String, Any?> = ConcurrentHashMap(),
    override var typeParser: Vector<TypeParser<*>> = Vector()
) : Context