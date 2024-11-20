package org.sereinfish.cat.frame.plugin.loader

import java.io.File
import java.net.InetAddress
import java.net.URLClassLoader

/**
 * 插件类加载器
 */
class PluginClassloader(
    val pluginId: String,
    file: File, // 插件jar文件
    dependencyJarFiles: List<File> = listOf(),
    parent: ClassLoader? = getSystemClassLoader()
): URLClassLoader(arrayOf(file.toURI().toURL()), null) {

    val dependencyClassloader = DependencyClassloader(dependencyJarFiles, parent)

    /**
     * 先加载自己的类
     * 然后对依赖进行类加载
     */
    override fun loadClass(name: String, resolve: Boolean): Class<*> {
        return synchronized(getClassLoadingLock(name)) {
            findLoadedClass(name)
                ?: run {
                    if (PluginLoaderUtils.isBlackList(name)) {
                        dependencyClassloader.loadClass(name)
                    } else if (name.startsWith("kotlin.")) {
                        runCatching { ClassLoader.getSystemClassLoader().loadClass(name) }.getOrNull()
                    } else null
                }
                ?: runCatching {
                    findClass(name)
                }.getOrNull()
                ?: dependencyClassloader.loadClass(name)
                ?: throw ClassNotFoundException(name)
        }
    }

    /**
     * 仅加载插件类
     */
    fun loadPluginClass(name: String): Class<*>? {
        return synchronized(getClassLoadingLock(name)) {
            findLoadedClass(name) ?: run {
                if (PluginLoaderUtils.isBlackList(name)) {
                    null
                } else runCatching {
                    findClass(name)
                }.getOrNull()
            }
        }
    }

    /**
     * 添加依赖插件信息
     */
    fun addParentPlugin(pluginClassloader: PluginClassloader) {
        dependencyClassloader.addParentPlugin(pluginClassloader)
    }

    override fun toString(): String {
        return "PluginClassloader(pluginId='$pluginId')"
    }
}