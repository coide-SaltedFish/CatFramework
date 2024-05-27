package org.sereinfish.cat.frame.context.property

import org.sereinfish.cat.frame.context.Context
import org.sereinfish.cat.frame.context.TypeParser
import org.sereinfish.cat.frame.context._getOrNull
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.jvmErasure

class ContextOrNullProperty<T>(
    val context: Context
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T? {
        return context._getOrNull(property.returnType.jvmErasure.java, property.name)
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        context[property.name] = value
    }
}

inline fun <T> Context.valueOrNull() = contextOrNullProperty<T>()