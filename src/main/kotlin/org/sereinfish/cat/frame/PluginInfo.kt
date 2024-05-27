package org.sereinfish.cat.frame

import org.sereinfish.cat.frame.config.PluginConfig
import org.sereinfish.cat.frame.context.getOrNull
import org.sereinfish.cat.frame.plugin.Plugin
import org.sereinfish.cat.frame.plugin.loader.PluginClassloader
import org.sereinfish.cat.frame.utils.nonNull
import org.sereinfish.cat.frame.utils.toJson
import java.io.File
import java.util.jar.JarFile

/**
 * 插件的信息
 */
data class PluginInfo(
    val id: String,
    val name: String = "默认插件名称",
    val description: String = "默认插件描述",
    val author: String = "作者名称",

    val jarFile: JarFile,
    val classLoader: PluginClassloader, // 插件类加载器
    val config: PluginConfig, // 插件配置信息
){
    private var plugin: Plugin? = null

    fun needEnable(): Boolean = config.getOrNull<String>("plugin.mainClass").nonNull()

    /**
     * 对插件类进行初始化
     */
    fun pluginEnable() {
        config.getOrNull<String>("plugin.mainClass")?.let { mainClassName ->
            val mainClass = classLoader.loadClass(mainClassName)
            // 判断是否继承Plugin类
            if (mainClass.interfaces.contains(Plugin::class.java)) {
                // 是否object类
                val obj = runCatching {
                    val kClass = mainClass.kotlin
                    kClass.objectInstance
                }.getOrNull() ?: run {
                    // 判断是否实现无参构造函数
                    mainClass.constructors.find {
                        it.parameterCount == 0
                    } ?: error("插件[$id]指定主类[$mainClassName]无法找到无参构造函数，无法完成初始化")
                    mainClass.getConstructor().newInstance()
                }
                (obj as? Plugin)?.let {
                    it.start()
                    plugin = it
                } ?: error("插件[$id]指定主类[$mainClassName]转为 Plugin 对象失败，无法完成初始化")

            }else error("插件[$id]指定主类[$mainClassName]未实现接口 Plugin，无法完成初始化")
        }
    }

    fun close(){
        plugin?.close()
    }
}