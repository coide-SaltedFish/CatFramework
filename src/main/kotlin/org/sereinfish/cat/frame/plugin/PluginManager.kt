package org.sereinfish.cat.frame.plugin

import org.sereinfish.cat.frame.CatFrameConfig
import org.sereinfish.cat.frame.PluginInfo
import org.sereinfish.cat.frame.config.SimplePluginConfig
import org.sereinfish.cat.frame.context.getOrElse
import org.sereinfish.cat.frame.plugin.dependencie.DependenciesUtils
import org.sereinfish.cat.frame.plugin.loader.PluginClassloader
import org.sereinfish.cat.frame.utils.isNull
import org.sereinfish.cat.frame.utils.logger
import org.sereinfish.cat.frame.utils.nonNull
import java.nio.file.Files
import java.nio.file.Paths
import java.util.Vector
import java.util.concurrent.ConcurrentHashMap
import java.util.jar.JarFile
import kotlin.io.path.notExists
import kotlin.math.log

/**
 * 插件管理器
 */
object PluginManager {
    private val logger = logger()

    private val _pluginsReal = ConcurrentHashMap<String, PluginInfo>()
    private val _levelPlugins: Vector<PluginInfo> get() {
        val list = Vector<PluginInfo>()
        _pluginsReal.values.forEach {
            list.add(it)
        }
        list.sortByDescending {
            it.config.getOrElse("plugin.level"){ 0 }
        }
        return list
    }
    val plugins: Map<String, PluginInfo> get() = _pluginsReal

    /**
     * 初始化插件管理器
     *
     * 1. 扫描jar包列表
     * 2. 读取插件配置
     * 3. 加载插件类加载器
     * 4. 对需要进行类扫描的插件进行扫描
     * 5. 依次加载插件主类
     */
    fun init(){
        val pluginsPath = Paths.get(CatFrameConfig.getOrElse("frame.plugins.path"){ "plugin/" })
        if (pluginsPath.notExists()) Files.createDirectories(pluginsPath)

        pluginsPath.toFile().walk().filter { it.extension == "jar" }.filter { file ->
            logger.debug("读取jar文件：{}", file)
            // 过滤没有id的jar包
            JarFile(file).use {
                logger.debug("读取插件ID：{}", file)
                it.manifest.mainAttributes.getValue("CatPluginId").nonNull()
            }
        }.forEach {
            // 获取id
            val pluginId = JarFile(it).use { it.manifest.mainAttributes.getValue("CatPluginId") }
            // 如果插件已加载
            if (_pluginsReal.contains(pluginId)){
                logger.warn("插件已加载，将会覆盖已加载插件：$pluginId")
            }else {
                logger.info("加载插件：[$pluginId] file=$it")
            }

            logger.debug("加载插件[{}]：{}", pluginId, it)
            // 加载配置

            logger.debug("读取插件配置: {}", it)
            val config = SimplePluginConfig(it)
            // 获取依赖信息
            logger.debug("获取插件依赖: {}", it)
//            val dependenciesBuilder = DependenciesBuilder(config)
            val dependenciesFiles = DependenciesUtils.loadDependencies(config)
            // 初始化类加载器
            logger.debug("初始化插件类加载器: {}", it)
//            val pluginClassloader = PluginClassloader(pluginId, it, dependenciesBuilder.initImplementations())
            val pluginClassloader = PluginClassloader(pluginId, it, dependenciesFiles)

            val pluginInfo = PluginInfo(
                pluginId,
                config.getOrElse("plugin.name"){ "默认名称" },
                config.getOrElse("plugin.description"){ "默认插件描述" },
                config.getOrElse("plugin.author"){ "作者名称" },
                JarFile(it),
                pluginClassloader,
                config
            )
            _pluginsReal[pluginId] = pluginInfo
        }
        // 所有插件类加载器初始化完成后，对插件依赖进行分配
//        _pluginsReal.values.forEach { pluginInfo ->
//            val dependenciesBuilder = DependenciesBuilder(pluginInfo.config)
//            dependenciesBuilder.plugins.forEach {
//                _pluginsReal[it.id]?.classLoader?.let {
//                    pluginInfo.classLoader.addParentPlugin(it)
//                } ?: run {
//                    if (it.optional.not())
//                        error("无法找到插件[${pluginInfo.id}]所依赖的插件[${it.id}]")
//                }
//            }
//        }
        _pluginsReal.values.forEach { pluginInfo ->
            DependenciesUtils.getPluginDependencies(pluginInfo.config).forEach {
                _pluginsReal[it.id]?.classLoader?.let {
                    pluginInfo.classLoader.addParentPlugin(it)
                } ?: run {
                    if (it.optional.not())
                        error("无法找到插件[${pluginInfo.id}]所依赖的插件[${it.id}]")
                }
            }
        }

        logger.info("已加载 ${plugins.size} 个插件")
        logger.info("插件管理器初始化完成")
    }

    /**
     * 插件启动
     */
    fun pluginEnable() {
        var n = 0
        // 对所有插件类主类进行初始化
        _levelPlugins.forEach {
            if (it.needEnable()){
                logger.info("启动插件[${it.id}]")
                it.pluginEnable()
                n ++
            }
        }

        logger.info("已启动 $n 个插件")
    }

    /**
     * 插件关闭
     */
    fun pluginDisable() {
        _levelPlugins.forEach {
            it.close()
        }
    }
}