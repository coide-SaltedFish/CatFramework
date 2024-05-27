package org.sereinfish.cat.frame.plugin.loader

import java.io.File
import java.net.URLClassLoader
import java.util.Vector

/**
 * 依赖类加载器
 * 仅进行依赖类的加载
 *
 */
open class DependencyClassloader(
    jarFiles: List<File> = listOf(),
    private val parent: ClassLoader? = getSystemClassLoader()
): URLClassLoader(jarFiles.map { it.toURI().toURL() }.toTypedArray(), null) {
    private val dependencyPlugins: Vector<PluginClassloader> = Vector()
    /**
     * 1. 首先加载依赖类
     * 2. 其次向依赖插件请求
     * 3. 其次向父类请求
     */
    override fun loadClass(name: String, resolve: Boolean): Class<*>? {
        return synchronized(getClassLoadingLock(name)){
            findLoadedClass(name)
                ?: run {
                    if (PluginLoaderUtils.isBlackList(name)) {
                        parent?.loadClass(name)
                    } else null
                }
                ?: runCatching {
                    findClass(name)
                }.getOrNull()
                ?: loadParentPluginClass(name)
                ?: runCatching {
                    parent?.loadClass(name)
                }.getOrNull()
                ?: runCatching {
                    getPlatformClassLoader().loadClass(name)
                }.getOrNull()
        }
    }

    override fun loadClass(name: String): Class<*>? {
        return loadClass(name, false)
    }

    /**
     * 添加依赖插件信息
     */
    fun addParentPlugin(pluginClassloader: PluginClassloader) {
        dependencyPlugins.add(pluginClassloader)
    }

    private fun loadParentPluginClass(name: String): Class<*>? {
        dependencyPlugins.forEach {
            kotlin.runCatching {
                it.loadClass(name)
            }.getOrNull()?.let {
                return it
            }
        }

        return null
    }
}