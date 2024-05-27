package org.sereinfish.cat.frame.config

/**
 * 插件配置
 *
 * 修改默认配置的加载行为，以及对配置文件路径进行重新定义
 * 加载顺序：
 *  1. 先读取插件jar包内部配置
 *  2. 使用外部路径配置文件进行覆写
 */
interface PluginConfig: Config {
}