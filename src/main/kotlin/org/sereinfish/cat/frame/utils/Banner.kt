package org.sereinfish.cat.frame.utils

import org.sereinfish.cat.frame.CatFrameConfig
import org.sereinfish.cat.frame.context.getOrElse

object Banner {
    private val banner = CatFrameConfig.getOrElse("banner"){
        Banner::class.java.classLoader.getResourceAsStream("banner.txt")?.use {
            it.readBytes().decodeToString()
        }
    }

    /**
     * 输出 Banner
     */
    fun print() {
        banner?.let {
            println(it)
        }
    }
}