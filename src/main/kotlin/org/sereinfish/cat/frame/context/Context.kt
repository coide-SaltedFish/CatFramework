package org.sereinfish.cat.frame.context

import org.sereinfish.cat.frame.context.property.*
import java.util.Vector
import java.util.concurrent.ConcurrentHashMap

interface Context {
    // 配置文件数据
    val data: ConcurrentHashMap<String, Any?>
    var typeParser: Vector<TypeParser<*>>

    operator fun get(key: String): Any? = data[key]
    operator fun set(key: String, value: Any?): Any? {
        data[key] = value
        return value
    }

    fun <T> contextOrNullProperty() = ContextOrNullProperty<T>(this)
    fun <T> contextOrElseProperty(default: (String) -> T) = ContextOrElseProperty<T>(this, default)
    fun <T> contextOrPutProperty(default: (String) -> T) = ContextOrPutProperty(this, default)
}

inline fun <reified T> Context.getOrNull(name: String): T? {
    return getOrNull(T::class.java, name)
}

inline fun <reified T> Context.getOrElse(name: String, default: (String) -> T): T {
    return getOrNull(name) ?: default(name)
}

inline fun <reified T> Context.getOrPut(name: String, default: (String) -> T): T {
    return getOrNull(name) ?: default(name).also {
        set(name, it)
    }
}

/**
 * 获取指定类型或者返回Null
 */
fun <T> Context.getOrNull(
    type: Class<*>,
    name: String
): T? {
    return get(name)?.let {
        if (type.isAssignableFrom(it::class.java)) it
        else {
            var value: Any = it
            for (parser in typeParser){
                if (parser.match(value, type)){
                    parser.cast(value)?.let {
                        value = it
                    }
                }
            }
            value
        }
    } as? T
}