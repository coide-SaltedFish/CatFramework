package org.sereinfish.cat.frame.context.property

import org.sereinfish.cat.frame.context.Context
import org.sereinfish.cat.frame.context.getOrNull
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.jvmErasure

class ContextOrElseProperty<T>(
    val context: Context,
    val default: (String) -> T
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return context.getOrNull<T>(
            property.returnType.jvmErasure.java,
            property.name
        ) ?: default(property.name)
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        context[property.name] = value
    }
}

inline fun <T> Context.valueOrElse(noinline default: (String) -> T) =
    contextOrElseProperty(default)