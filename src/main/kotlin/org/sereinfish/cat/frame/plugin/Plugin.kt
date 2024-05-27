package org.sereinfish.cat.frame.plugin

import org.sereinfish.cat.frame.config.Config
import org.sereinfish.cat.frame.plugin.loader.PluginClassloader
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * 插件类
 */
interface Plugin {
    val id: String get() =
        (this::class.java.classLoader as? PluginClassloader)?.pluginId
            ?: error("插件主类初始化失败，错误的类加载器 ${this::class.java.classLoader::class.java}")

    val config: Config get() = PluginManager.plugins[id]?.config ?: error("插件尚未被加载：${id}")

    val logger: Logger get() =  LoggerFactory.getLogger("Plugin.$id")

    /**
     * 插件启动
     */
    fun start()

    /**
     * 插件关闭
     */
    fun close() {}
}