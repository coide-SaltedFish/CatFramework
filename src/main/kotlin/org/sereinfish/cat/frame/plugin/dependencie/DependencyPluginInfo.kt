package org.sereinfish.cat.frame.plugin.dependencie

data class DependencyPluginInfo(
    val id: String,
    val optional: Boolean = false, // 插件是否是必须加载的
){
    override fun toString(): String {
        return "DependencyPluginInfo(id='$id', optional=$optional)"
    }
}
