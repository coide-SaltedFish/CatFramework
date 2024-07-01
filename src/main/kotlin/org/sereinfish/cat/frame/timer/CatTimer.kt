package org.sereinfish.cat.frame.timer

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.sereinfish.cat.frame.utils.creatContextScope
import java.util.Vector

/**
 * 定时器
 *
 * 支持以下功能：
 * 按照指定频率执行，例如每年或每月或每星期或每天或每小时或每分钟或每秒的指定时间执行
 * 按照指定间隔定时执行，例如在指定多少时间后执行一次
 * 支持仅一次执行或循环指定次数后结束或循环执行
 * 支持在等待指定时间后开始执行
 */
object CatTimer {
    private val scope = creatContextScope()
    private val tasks = Vector<CatTimerTask>()

    fun excute(task: CatTimerTask){
        tasks.add(task)
        runTask(task)
    }

    private fun runTask(task: CatTimerTask){

        scope.launch {
            try {
                delay(task.startDelay())
            }catch (e: Exception){
                task.catch(e)
            }
            while (tasks.contains(task) && task.loop()){
                try {
                    delay(maxOf(1, task.delay().also {
                        println("等待 $it")
                    }))
                    task.run()
                }catch (e: Exception){
                    task.catch(e)
                }
            }
        }
    }
}