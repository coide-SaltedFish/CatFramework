package org.sereinfish.cat.frame.config

import com.google.gson.reflect.TypeToken
import org.sereinfish.cat.frame.utils.toClass
import org.sereinfish.cat.frame.utils.toJson
import org.sereinfish.cat.frame.utils.toYaml
import org.yaml.snakeyaml.Yaml

/**
 * 配置文件类型
 */
enum class ConfigType(type: Array<String>) {
    JSON(arrayOf("json")),
    YAML(arrayOf("yml", "yaml"));

    /**
     * 编码
     */
    fun encode(obj: Any): String {
        return when(this){
            JSON -> obj.toJson { setPrettyPrinting() }
            YAML -> obj.toYaml()
        }
    }

    /**
     * 解码
     */
    fun decode(str: String): Map<String, Any?> {
        return when(this){
            JSON -> str.toClass<Map<String, Any?>>(type = object : TypeToken<Map<String, Any?>>(){}.type)
            YAML -> Yaml().load(str)
        } ?: mapOf()
    }
}