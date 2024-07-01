package org.sereinfish.cat.frame.config

import org.sereinfish.cat.frame.CatFrameConfig
import org.sereinfish.cat.frame.context.TypeParser
import org.sereinfish.cat.frame.utils.nonNull
import java.io.BufferedReader
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.nio.file.StandardOpenOption
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.jar.JarFile
import kotlin.io.path.extension


class SimplePluginConfig(
    val file: File
): PluginConfig {
    private val jarFile = JarFile(file)
    override var typeParser: Vector<TypeParser<*>> = Vector()

    var pluginId: String = jarFile.manifest.mainAttributes.getValue("CatPluginId") ?: error("无法获取插件ID，属性值为 NULL: $file")

    override val configPath: String = "${CatFrameConfig.pluginConfigPath}/$pluginId"
    override val configFile: String = "$configPath/config.yml"
    override val data: ConcurrentHashMap<String, Any?> = ConcurrentHashMap()

    /**
     * 依次读取覆盖
     * 1. 读取jar包内配置文件
     * 2. 读取插件数据目录下配置文件
     */
    init {
        var jarConfigType: ConfigType? = null
        val configEntry = jarFile.getEntry("config.json")?.also { jarConfigType = ConfigType.JSON }
                ?: jarFile.getEntry("config.yml")?.also { jarConfigType = ConfigType.YAML }
                ?: jarFile.getEntry("config.yaml")?.also { jarConfigType = ConfigType.YAML }
                // TODO 打印警告日志

        configEntry?.let {
            val dataString = jarFile.getInputStream(configEntry).use {
                BufferedReader(InputStreamReader(it)).readText()
            }
            // 解析文件
            val data = jarConfigType?.decode(dataString) ?: error("未知的配置文件解析格式：null")
            putAll(data)
        }

        // 读取外部配置文件
        loadExternalConfig()
    }

    /**
     * 加载外部配置文件
     */
    private fun loadExternalConfig() {
        File(configPath).mkdirs()

        // 查找是否已存在
        val configDirPath = Paths.get(configPath)

        Files.list(configDirPath).filter {
            it.fileName.toString().startsWith("config")
        }.map {
            when(it.extension){
                "json" -> ConfigType.JSON
                "yml", "yaml" -> ConfigType.YAML
                else -> null
            } to it
        }.filter {
            it.first.nonNull()
        }.toList().sortedBy {
            it.first!!.ordinal
        }.forEach {
            putAll(it.first!!.decode(Files.readString(it.second)))
        }
    }

    override fun set(path: String, value: Any?): Any? {
        return super.set(path, value).also {
            save()
        }
    }
}