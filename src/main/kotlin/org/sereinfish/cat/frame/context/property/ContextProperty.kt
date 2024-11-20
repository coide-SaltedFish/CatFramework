package org.sereinfish.cat.frame.context.property

import org.sereinfish.cat.frame.context.Context
import org.sereinfish.cat.frame.context.getOrNull
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.jvmErasure

class ContextProperty<T>(val context: Context) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return context.getOrNull(property.returnType.jvmErasure.java, property.name) ?: error("无法获取到[${property.name}]上下文：NULL")
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        context[property.name] = value
    }
}

inline fun <T> Context.value() = ContextProperty<T>(this)
