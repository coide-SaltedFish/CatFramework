package org.sereinfish.cat.frame.timer

import org.sereinfish.cat.frame.utils.logger
import java.text.SimpleDateFormat
import java.util.*

class SimpleCatTimerTask(
    override val id: String = UUID.randomUUID().toString(),
    val startDelayFunc: suspend () -> Long = { 0L },
    val timerData: TimerData,
    val catchFunc: suspend (e: Exception) -> Unit = { e -> logger().error("定时任务异常", e) },
    val loopFunc: suspend () -> Boolean = { true },
    val startFunc: suspend () -> Unit = {},
    val runFunc: suspend () -> Unit
): CatTimerTask {
    override suspend fun start() {
        startFunc()
    }

    override suspend fun startDelay(): Long {
        return startDelayFunc()
    }


    override suspend fun run() {
        runFunc()
    }

    override suspend fun catch(e: Exception) {
        catchFunc(e)
    }

    override suspend fun delay(): Long {
        return timerData.delayNext()
    }

    override suspend fun loop(): Boolean {
        return loopFunc()
    }
}