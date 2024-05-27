package org.sereinfish.cat.frame.context.property

import org.sereinfish.cat.frame.context.Context
import org.sereinfish.cat.frame.context.TypeParser
import org.sereinfish.cat.frame.context._getOrNull
import org.sereinfish.cat.frame.context.getOrPut
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.jvmErasure

class ContextOrPutProperty<T>(
    val context: Context,
    val default: (String) -> T
) {

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return context._getOrNull(
            property.returnType.jvmErasure.java,
            property.name
        ) ?: default(property.name).also {
            context[property.name] = it
        }
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        context[property.name] = value
    }
}

inline fun <T> Context.valueOrPut(noinline default: (String) -> T) =
    contextOrPutProperty<T>(default)