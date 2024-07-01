package org.sereinfish.cat.frame.config

import org.sereinfish.cat.frame.context.Context
import org.sereinfish.cat.frame.utils.toClass
import org.sereinfish.cat.frame.utils.toJson
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.nio.file.StandardOpenOption

/**
 * 配置文件
 */
interface Config: Context {
    // 配置文件路径
    val configPath: String
    val saveBlackListKeys: List<String> get() = listOf("plugin", "dependencies")
    val configFile: String get() = configPath

    val configType: ConfigType get() = when {
        configFile.endsWith(".json", true) -> ConfigType.JSON
        configFile.endsWith(".yml", true) || configFile.endsWith(".yaml", true) -> ConfigType.YAML
        else -> error("Unknown configuration file format, suffix: ${configFile.substringAfterLast(".")}")
    }

    override operator fun get(key: String): Any? {
        val paths = key.split(".")
        var d: Any? = data
        for (p in paths){
            (d as? Map<String, Any?>)?.let {
                d = it[p]
            } ?: return null
        }
        return d
    }


    override operator fun set(key: String, value: Any?): Any? {
        val paths = key.split(".")
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
        val saveData = HashMap<String, Any?>().apply {
            // TODO 黑名单支持 key path
            data.forEach { (key, value) ->
                if (saveBlackListKeys.contains(key).not()){
                    put(key, value)
                }
            }
        }

        val filePath = Paths.get(configPath)
        val file = Paths.get(configFile)
        // 检查路径是否正确
        if (Files.notExists(filePath)){
            Files.createDirectories(filePath)
        }
        // 备份原有配置
        if (Files.exists(file)){
            val backupPath = Paths.get("$configFile.backup")
            Files.copy(file, backupPath, StandardCopyOption.REPLACE_EXISTING)
        }
        // 写入新配置
        Files.write(file, encoded(saveData).toByteArray(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
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

        val filePath = Paths.get(configFile)
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