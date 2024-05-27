package org.sereinfish.cat.frame.plugin.dependencie

import org.sereinfish.cat.frame.plugin.dependencie.entity.DependenciesInfo
import java.io.File

/**
 * 依赖解析器
 *
 * 1. 传入一个依赖项
 * 2. 依次向上下载pom，生成列表
 * 3. 根据解析列表依次下载jar
 */
class DependenciesParser(info: DependenciesInfo) {
    // 依赖的父级依赖
    private val parent: ArrayList<DependenciesParser> = ArrayList()

    /**
     * 依次向上解析pom
     *
     * 下载自己的pom，解析后生成 DependenciesInfo，放入父级依赖列表最开始
     * 所有父级依赖解析完成后，解析父级依赖项
     */
    fun parser(): DependenciesParser {

        return this
    }

    /**
     * 获取解析完成后依赖的文件目录
     *
     * 目录需要进行去重
     */
    fun files(): List<File> {
        return listOf()
    }

    /**
     * 获取依赖保存文件的路径
     */
    private fun filePath(): String {
        TODO()
    }

    /**
     * 进行下载
     *
     * 调用父级依赖项的下载
     * 下载依赖自身
     * 下载自身前先进行缓存验证
     */
    fun download(): DependenciesParser {
        return this
    }
}