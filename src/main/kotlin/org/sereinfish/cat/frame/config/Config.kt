package org.sereinfish.cat.frame.config

import com.google.gson.reflect.TypeToken
import org.sereinfish.cat.frame.context.Context
import org.sereinfish.cat.frame.utils.toClass
import org.sereinfish.cat.frame.utils.toJson
import org.sereinfish.cat.frame.utils.toYaml
import org.yaml.snakeyaml.Yaml
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.nio.file.StandardOpenOption
import java.util.concurrent.ConcurrentHashMap

/**
 * 配置文件
 */
interface Config: Context {
    // 配置文件路径
    val configPath: String

    val configType: ConfigType get() = when {
        configPath.endsWith(".json", true) -> ConfigType.JSON
        configPath.endsWith(".yml", true) || configPath.endsWith(".yaml", true) -> ConfigType.YAML
        else -> error("Unknown configuration file format, suffix: ${configPath.substringAfterLast(".")}")
    }

    override operator fun get(path: String): Any? {
        val paths = path.split(".")
        var d: Any? = data
        for (p in paths){
            (d as? Map<String, Any?>)?.let {
                d = it[p]
            } ?: return null
        }
        return d
    }


    override operator fun set(path: String, value: Any?): Any? {
        val paths = path.split(".")
        var d: MutableMap<String, Any?> = data

        for (p in paths.subList(0, paths.size - 1)){
            (d[p] as? MutableMap<String, Any?>)?.let {
                d = it
            } ?: run {
                val v = mutableMapOf<String, Any?>()
                d[p] = v
                d = v
            }
        }

        d[paths.last()] = value

        return value
    }

    fun putAll(values: Map<String, Any?>){
        data.putAll(values)
    }

    fun save() = save(data)

    /**
     * 保存配置
     */
    private fun save(data: Map<String, Any?>) {
        val filePath = Paths.get(configPath)
        // 检查路径是否正确
        if (Files.notExists(filePath.parent)){
            Files.createDirectories(filePath.parent)
        }
        // 备份原有配置
        if (Files.exists(filePath)){
            val backupPath = Paths.get("$configPath.backup")
            Files.copy(filePath, backupPath, StandardCopyOption.REPLACE_EXISTING)
        }
        // 写入新配置
        Files.write(filePath, encoded(data).toByteArray(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
    }

    /**
     * 加载配置
     */
    fun load() {
        data.clear()
        data.putAll(decode())
    }

    /**
     * 如果为json文件，写入json
     * 如果为yml或yaml文件，写入yml
     */
    private fun encoded(data: Map<String, Any?>): String {
        return configType.encode(data)
    }

    private fun decode(): Map<String, Any?> {
        var data = ""

        val filePath = Paths.get(configPath)
        if (Files.exists(filePath)){
            data = Files.readString(filePath)
        }else {
            save(mapOf())
        }

        return configType.decode(data)
    }
}

inline fun <reified T> Config.getClassOrNull(name: String): T? {
    return get(name)?.toJson()?.toClass()
}

inline fun <reified T> Config.getClassOrElse(name: String, default: (String) -> T): T =
    getClassOrNull(name) ?: default(name)

inline fun <reified T> Config.getClassOrPut(name: String, default: (String) -> T): T =
    getClassOrNull(name) ?: default(name).also {
        set(name, it)
    }