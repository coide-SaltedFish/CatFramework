package org.sereinfish.cat.frame.timer

import org.sereinfish.cat.frame.utils.logger

/**
 * 定时器任务接口
 *
 * 由任务执行部分，延迟计算部分，循环判断部分组成
 */
interface CatTimerTask {

    suspend fun start()

    suspend fun run()

    suspend fun catch(e: Exception){
        logger().error("定时任务异常", e)
    }

    suspend fun startDelay(): Long = 0L


    suspend fun delay(): Long

    suspend fun loop(): Boolean
}