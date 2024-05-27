package org.sereinfish.cat.frame.config.property

import org.sereinfish.cat.frame.config.Config
import org.sereinfish.cat.frame.context.getOrElse
import kotlin.reflect.KProperty

class ConfigPropertyElse<T> (
    val config: Config,
    val path: String? = null,
    val name: String? = null,
    val default: (String) -> T
) {
    inline operator fun <reified T> getValue(thisRef: Any?, property: KProperty<*>): T {
        val p = name?.let {
            path?.let { "$it.${name}" } ?: name
        } ?: run {
            path?.let { "$it.${property.name}" } ?: property.name
        }
        return config.getOrElse<T>(p){ default(p) as T }
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        val p = name?.let {
            path?.let { "$it.${name}" } ?: name
        } ?: run {
            path?.let { "$it.${property.name}" } ?: property.name
        }
        config[p] = value
    }
}

inline fun <T> Config.configValueOrElse(path: String? = null, name: String? = null, noinline default: (String) -> T) = ConfigPropertyElse(
    this, path, name, default
)