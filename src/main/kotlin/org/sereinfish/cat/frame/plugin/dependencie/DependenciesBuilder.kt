package org.sereinfish.cat.frame.plugin.dependencie

import okhttp3.OkHttpClient
import okhttp3.Request
import org.apache.maven.model.Dependency
import org.apache.maven.model.Model
import org.apache.maven.model.io.xpp3.MavenXpp3Reader
import org.sereinfish.cat.frame.CatFrameConfig
import org.sereinfish.cat.frame.config.Config
import org.sereinfish.cat.frame.config.getClassOrElse
import org.sereinfish.cat.frame.context.getOrNull
import org.sereinfish.cat.frame.plugin.dependencie.entity.DependenciesInfo
import org.sereinfish.cat.frame.utils.isNull
import org.sereinfish.cat.frame.utils.logger
import org.sereinfish.cat.frame.utils.nonNull
import org.slf4j.Logger
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.net.InetSocketAddress
import java.net.Proxy
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.log

/**
 * 传入配置文件，构建依赖信息
 */
class DependenciesBuilder(
    config: Config
) {
    private val logger = logger()
    // 读取代理地址
    private val proxyHost = CatFrameConfig.getOrNull<String>("dependencies.host")
    private val proxyPort = CatFrameConfig.getOrNull<Int>("dependencies.port")
    private val proxy = if (proxyHost.nonNull() && proxyPort.nonNull()){
        Proxy(Proxy.Type.HTTP, InetSocketAddress(proxyHost!!, proxyPort!!))
    }else null

    private val client = OkHttpClient.Builder()
        .proxy(proxy?.also {
            logger.info("Proxy $it")
        })
        .build()

    // 仓库地址列表
    val repositories: List<String> = config.getClassOrElse<MutableList<String>>("dependencies.repositories"){
        mutableListOf()
    }.apply {
        if (!contains("https://repo.maven.apache.org/maven2/"))
            add("https://repo.maven.apache.org/maven2/")
    }
    // 依赖地址列表
    val implementations: List<String> = config.getClassOrElse("dependencies.implementations") { listOf() }

    // 依赖插件列表
    val plugins: List<DependencyPluginInfo> = config.getOrNull<List<Any>>("dependencies.plugins")?.let {
        it.map {
            when(it){
                is String -> DependencyPluginInfo(it)
                is Map<*, *> -> {
                    var optional = false
                    var id = ""
                    it.forEach { key, value ->
                        when(key) {
                            "optional" -> optional = value as Boolean
                            "id" -> id = value as String
                        }
                    }
                    if (id.isEmpty())
                        error("依赖的插件id不能为空")
                    DependencyPluginInfo(id, optional)
                }

                else -> {
                    error("未知的配置类型：${it::class.java}")
                }
            }
        }
    } ?: listOf()

    /**
     * 初始化依赖文件列表
     */
    fun initImplementations(): List<File> {
        logger.debug("依赖构建代理：{}", proxy)
        val dependenciesInfos = implementations.map {
            logger.debug("读取配置依赖信息：{}", it)
            DependenciesInfo(it)
        }
        dependenciesInfos.forEach {
            download(it)
            logger.info("依赖加载完成：{}", it.info)
        }

        return buildSet {
            dependenciesInfos.forEach {
                addAll(it.flies())
            }
        }.toList().map { File(it) }
    }

    /**
     * 下载
     */
    private fun download(dependenciesInfo: DependenciesInfo) {
        logger.debug("开始解析下载依赖：{}", dependenciesInfo)
        // 下载jar文件
        val file = File(CatFrameConfig.libsPath, "${dependenciesInfo.path}${dependenciesInfo.name}.jar")
        if (file.exists() && file.isFile){
            logger.debug("依赖缓存已存在：{}", dependenciesInfo)
            return
        }

        // 排除
        if (dependenciesInfo.groupId == "org.slf4j'" && dependenciesInfo.artifactId == "slf4j-api")
            return

        // 下载pom文件
        val pomFile = downloadPom(dependenciesInfo)

        logger.debug("开始下载依赖文件：{}, file={}", dependenciesInfo, file)
        if ((file.exists() && file.isFile).not()){
            // 构建文件夹
            file.parentFile.mkdirs()
            for (url in dependenciesInfo.jarUrl){
                logger.debug("下载依赖文件：{}", url)
                val request = Request.Builder()
                    .url(url)
                    .build()
                val response = client.newCall(request).execute()
                if (response.isSuccessful){
                    // 写入文件
                    if (file.createNewFile()) {
                        FileOutputStream(file).use {
                            it.write(response.body?.bytes() ?: error("请求文件流解析失败：$dependenciesInfo"))
                        }
                    } else error("文件创建失败：$file")

                    // 下载完成
                    break
                }else {
                    // 继续下一个链接
                    continue
                }
            }
        }
        logger.debug("依赖下载完成：{}", dependenciesInfo)

        // 递归解析父依赖
        val model = MavenXpp3Reader().read(FileReader(pomFile))

        val repositories: List<String> = model.repositories.map { it.url }.let {
            if (it.contains("https://repo.maven.apache.org/maven2/").not())
                it.toMutableList().apply {
                    add("https://repo.maven.apache.org/maven2/")
                }
            else it
        }

        model.dependencies.map { dependency ->
            dependency.toInfo(model, repositories, logger)
        }.forEach {
            it?.let { download(it) }
        }
    }

    private fun downloadPom(dependenciesInfo: DependenciesInfo): File {
        val file = File(CatFrameConfig.libsPath, "${dependenciesInfo.path}${dependenciesInfo.name}.pom")
        logger.debug("开始解析下载pom文件：{}, file={}", dependenciesInfo, file)
        if (file.exists() && file.isFile)
            return file
        // 构建文件夹
        file.parentFile.mkdirs()

        for (url in dependenciesInfo.pomUrl){
            logger.debug("尝试下载pom文件：{}", url)
            val request = Request.Builder()
                .url(url)
                .build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful){
                // 写入文件
                if (file.createNewFile()) {
                    FileOutputStream(file).use {
                        it.write(response.body?.bytes() ?: error("请求文件流解析失败：$dependenciesInfo"))
                    }
                } else error("文件创建失败：$file")

                // 下载完成
                logger.debug("pom文件下载完成：{}", dependenciesInfo)
                return file
            }else {
                logger.warn("下载失败：$url")
                // 继续下一个链接
                continue
            }
        }
        error("所有POM文件解析均失败：${dependenciesInfo}")
    }
}

fun Dependency.toInfo(model: Model, repositories: List<String>, logger: Logger = logger()): DependenciesInfo? {
    val dependency = this

    var version = dependency.version
    var groupId = dependency.groupId

    if (groupId == "\${project.groupId}"){
        groupId = model.groupId
    }

    if (dependency.version.isNull()) {
        model.dependencyManagement?.dependencies?.forEach {
            println("${it.groupId}:${it.artifactId}:${it.version}")
            if (it.groupId == dependency.groupId && it.artifactId == dependency.artifactId)
                version = it.version
        }
    }

    if (version == "\${project.version}"){
        version = model.version
    }

    return if (version.nonNull()){
        runCatching {
            DependenciesInfo(
                dependency.groupId,
                dependency.artifactId,
                version,
                dependency.classifier,
                repositories
            ).also {
                logger.debug("解析传递依赖：{}", it)
            }
        }.getOrElse {
            logger.error("""
                    ${dependency.groupId}
                    ${dependency.artifactId}
                    ${dependency.version}
                    ${dependency.classifier}
                """.trimIndent())
            throw it
        }
    }else null
}