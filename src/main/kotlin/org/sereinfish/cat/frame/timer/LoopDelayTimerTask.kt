package org.sereinfish.cat.frame.timer

import org.sereinfish.cat.frame.utils.logger
import java.util.UUID

class LoopDelayTimerTask(
    override val id: String = UUID.randomUUID().toString(),
    val startDelay: Long,
    val delay: Long,
    val loopCount: Int = 0, // 小于1表示无限循环
    val catchFunc: suspend (e: Exception) -> Unit = { e -> logger().error("定时任务异常", e) },
    val startFunc: () -> Unit = {},
    val runFunc: () -> Unit
): CatTimerTask {

        private var execCount = 0

    override suspend fun catch(e: Exception) {
        catchFunc(e)
    }

    override suspend fun start() {
        startFunc()
    }

    override suspend fun startDelay(): Long {
        return startDelay
    }

    override suspend fun run() {
        runFunc()
        execCount ++
    }

    override suspend fun delay(): Long {
        return delay
    }

    override suspend fun loop(): Boolean {
        return if (loopCount < 1) true else {
            execCount < loopCount
        }
    }
}