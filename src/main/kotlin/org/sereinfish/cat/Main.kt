package org.sereinfish.cat

import kotlinx.coroutines.launch
import org.sereinfish.cat.frame.event.EventManager
import org.sereinfish.cat.frame.event.events.CatFrameCloseEvent
import org.sereinfish.cat.frame.event.events.CatFrameStartEvent
import org.sereinfish.cat.frame.log.CatLogger
import org.sereinfish.cat.frame.plugin.PluginManager
import org.sereinfish.cat.frame.utils.creatContextScope
import org.sereinfish.cat.frame.utils.logger
import java.util.*
import kotlin.concurrent.thread


private val logger = logger()
/**
 * 引导框架启动
 *
 * 框架启动路径
 * org.sereinfish.cat.frame
 */
fun main() {
    logger.info("框架启动")
    CatLogger.init()

    logger.info("初始化插件")
    PluginManager.init()
    PluginManager.pluginEnable()

    // 广播框架启动完成事件
    EventManager.broadcast(CatFrameStartEvent.build())

    Runtime.getRuntime().addShutdownHook(thread(false) {

        EventManager.broadcast(CatFrameCloseEvent.build())

        logger.info("插件关闭中")
        PluginManager.pluginDisable()
        logger.info("插件关闭完成")

        // 等待事件队列处理完成
        EventManager.close()

        logger.info("框架关闭")
    })

    creatContextScope().launch {
        val scanner = Scanner(System.`in`)
        while (true) {
            val command = scanner.nextLine()
            when (command) {
                "stop" -> System.exit(0)
                else -> logger.info("未知命令：$command")
            }
        }
    }

    logger.info("初始化事件管理系统")
    logger.info("框架启动完成")
    EventManager.init()
}