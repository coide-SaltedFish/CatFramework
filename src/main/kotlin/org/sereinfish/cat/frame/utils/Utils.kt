package org.sereinfish.cat.frame.utils

import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.*
import org.sereinfish.cat.frame.plugin.Plugin
import org.slf4j.LoggerFactory
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import java.lang.reflect.Method
import java.lang.reflect.Type
import kotlin.coroutines.CoroutineContext
import kotlin.experimental.ExperimentalTypeInference

inline fun Any?.isNull() = this == null
inline fun Any?.nonNull() = this != null

infix fun <T> Boolean.isTrue(block: Boolean.() -> T): T? {
    return if (this) block() else null
}

infix fun <T> Boolean.isFlase(block: Boolean.() -> T): T? {
    return if (this.not()) block() else null
}

@OptIn(ExperimentalTypeInference::class)
infix fun <T: Any, R> T?.isNonNull(@BuilderInference block: (T) -> R): R? {
    return this?.let {
        block(it)
    }
}

inline fun getCaller() =
    Class.forName(Thread.currentThread().stackTrace[1].className)

inline fun logger() = LoggerFactory.getLogger(getCaller())

inline fun Any.toJson(builder: GsonBuilder.() -> Unit = {}): String {
    return GsonBuilder().apply(builder).create().toJson(this)
}

inline fun <reified T> String.toClass(
    builder: GsonBuilder.() -> Unit = {}
): T? {
    return GsonBuilder().apply(builder).create().fromJson(this, T::class.java)
}

inline fun <reified T> String.toClass(
    type: Class<T> = T::class.java,
    builder: GsonBuilder.() -> Unit = {}
): T? {
    return GsonBuilder().apply(builder).create().fromJson(this, type) as? T
}

inline fun <reified T> String.toClass(
    type: Type = T::class.java,
    builder: GsonBuilder.() -> Unit = {}
): T? {
    return GsonBuilder().apply(builder).create().fromJson(this, type) as? T
}

inline fun <reified T> String.toClassOrElse(
    type: Class<T> = T::class.java,
    default: (Class<T>) -> T
): T {
    return toClass(type) ?: default(type)
}

inline fun Any.toYaml(dumperOptionsBuilder: DumperOptions.() -> Unit = {}): String {
    val dumperOptions = DumperOptions()
    dumperOptions.dumperOptionsBuilder()

    return Yaml(dumperOptions).dump(this)
}

inline fun <reified T> String.yamlToClass(
    dumperOptionsBuilder: DumperOptions.() -> Unit = {},
    type: Class<T> = T::class.java
): T? {
    return Yaml(DumperOptions().apply(dumperOptionsBuilder)).loadAs(this, type)
}

/**
 * 确保插件被导入才执行代码
 */
@OptIn(ExperimentalTypeInference::class)
inline fun <reified T, R> ensurePluginImport(
    plugin: Plugin,
    errorBlock: () -> Result<R> = { Result.failure(Exception("指定插件的类未导入：${T::class.java.name}")) },
    @BuilderInference block: () -> Result<R>
): Result<R> {
    return try {
        plugin.classloader.loadClass(T::class.java.name) ?: throw ClassNotFoundException(T::class.java.name)
        block()
    } catch (e: Exception) {
        errorBlock()
    }
}

/**
 * 创建一个协程作用域
 */
fun creatContextScope(name: String = getCaller().name, dispatcher: CoroutineDispatcher = Dispatchers.Default) =
    ContextScope(Job() + dispatcher + CoroutineName(name))

class ContextScope(context: CoroutineContext) : CoroutineScope {
    override val coroutineContext: CoroutineContext = context
    // CoroutineScope is used intentionally for user-friendly representation
    override fun toString(): String = "CoroutineScope(coroutineContext=$coroutineContext)"
}