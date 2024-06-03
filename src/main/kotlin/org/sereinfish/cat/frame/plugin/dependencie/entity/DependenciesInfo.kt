package org.sereinfish.cat.frame.plugin.dependencie.entity

import org.apache.maven.model.io.xpp3.MavenXpp3Reader
import org.sereinfish.cat.frame.CatFrameConfig
import org.sereinfish.cat.frame.utils.logger
import java.io.*


/**
 * 一个依赖包含的信息
 */
data class DependenciesInfo(
    val groupId: String,
    val artifactId: String,
    val version: String,
    val classifier: String? = null,
    val repositories: List<String> = listOf("https://repo.maven.apache.org/maven2/")
){
    private val logger = logger()

    constructor(
        dependencyNotation: String,
        repositories: List<String> = listOf("https://repo.maven.apache.org/maven2/")
    ) : this(dependencyNotation.split(":"), repositories)

    constructor(
        dependencyNotation: List<String>,
        repositories: List<String> = listOf("https://repo.maven.apache.org/maven2/")
    ) : this(
        dependencyNotation[0],
        dependencyNotation[1],
        dependencyNotation[2],
        dependencyNotation.getOrNull(3),
        repositories
    )

    /**
     * 依赖的相对目录
     */
    val path: String
        get() = "${groupId.replace(".", "/")}/$artifactId/$version/"

    val name: String = buildString {
        append("$artifactId-$version")
        classifier?.let {
            append("-$it")
        }
    }

    val info = "$groupId:$artifactId:$version:$classifier"

    private val baseUrl = buildList {
        repositories.forEach {
            add(buildString {
                append(it)
                append("${groupId.replace(".", "/")}/$artifactId/$version/")
                append("$artifactId-$version")
                classifier?.let {
                    append("-$it")
                }
            })
        }
    }

    /**
     * jar文件的下载目录
     */
    val jarUrl = baseUrl.map {
        "$it.jar"
    }
    val pomUrl = baseUrl.map {
        "$it.pom"
    }

    /**
     * 获取依赖所有相关jar文件
     */
    fun flies(): List<String> {
        val list = ArrayList<String>()
        files(list, this)

        return list
    }

    private fun files(list: ArrayList<String>, dependenciesInfo: DependenciesInfo) {

        // 排除
        if (dependenciesInfo.groupId == "org.slf4j'" && dependenciesInfo.artifactId == "slf4j-api")
            return

        val pomFile = File(CatFrameConfig.libsPath, "${dependenciesInfo.path}${dependenciesInfo.name}.pom")
        if (pomFile.exists().not()) {
            logger.warn("依赖加载失败，文件不存在：$dependenciesInfo")
            return
        }

        val model = MavenXpp3Reader().read(InputStreamReader(FileInputStream(pomFile), getPomCharsetName(pomFile)))
        val path = File(CatFrameConfig.libsPath, "${dependenciesInfo.path}${dependenciesInfo.name}.jar").absolutePath

//        if (list.contains(path)){
//            return
//        }else {
//            list.add(path)
//
//            model.dependencies.map {
//                it.toInfo(model, repositories, logger)
//            }.forEach {
//                it?.let { it.files(list, it) }
//            }
//        }
    }

    private fun getPomCharsetName(file: File): String {
        BufferedReader(FileReader(file)).use { reader ->
            val line = reader.readLine()
            if (line.startsWith("<?xml")) {
                val start = line.indexOf("encoding=")
                if (start >= 0) {
                    val startQuote = line.indexOf("\"", start)
                    val endQuote = line.indexOf("\"", startQuote + 1)
                    if (startQuote >= 0 && endQuote >= 0) {
                        return line.substring(startQuote + 1, endQuote)
                    }
                }
            }
        }

        return "UTF-8"
    }

    override fun toString(): String {
        return "DependenciesInfo(groupId='$groupId', artifactId='$artifactId', version='$version', classifier=$classifier, repositories=$repositories)"
    }
}
