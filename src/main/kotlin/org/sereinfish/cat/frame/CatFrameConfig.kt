package org.sereinfish.cat.frame

import org.sereinfish.cat.frame.config.Config
import org.sereinfish.cat.frame.config.property.configValueOrElse
import org.sereinfish.cat.frame.context.TypeParser
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * 框架配置
 */
object CatFrameConfig: Config {
    override val configPath: String = File("config.yml").absolutePath
    override val data = ConcurrentHashMap<String, Any?>()
    override var typeParser: Vector<TypeParser<*>> = Vector()

    // 插件文件路径配置
    val pluginsPath: String by configValueOrElse("frame.plugins", "path") { "./plugin/" }
    // 插件配置文件路径配置
    val pluginConfigPath: String by configValueOrElse("frame.plugins.config", "path") { "./data/config/" }
    // 依赖文件根目录
    val libsPath: String by configValueOrElse("frame.libs.path") { "./libs/" }

    init {
        load() // 加载配置
    }
}