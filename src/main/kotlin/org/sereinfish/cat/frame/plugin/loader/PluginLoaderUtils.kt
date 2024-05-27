package org.sereinfish.cat.frame.plugin.loader

object PluginLoaderUtils {
    val blackList: List<(String) -> Boolean> = listOf(
        { it.startsWith("kotlin.") },
        { it.startsWith("java.") },
        { it == "org.sereinfish.cat.frame.plugin.Plugin" },
        { it.startsWith("org.slf4j.") }
    )

    fun isBlackList(name: String): Boolean {
        for (item in blackList){
            if (item(name))
                return true
        }

        return false
    }
}