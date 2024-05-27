package org.sereinfish.cat.frame.config.property

import org.sereinfish.cat.frame.context.Context
import kotlin.reflect.KProperty

class ConfigProperty<T> (
    val context: Context,
    val path: String? = null
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        val p = path?.let { "$it.${property.name}" } ?: property.name
        return context[p] as T ?: throw NullPointerException(p)
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        val p = path?.let { "$it.${property.name}" } ?: property.name
        context[p] = value
    }
}