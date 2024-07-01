package org.sereinfish.cat.frame.event

import kotlinx.coroutines.*
import org.sereinfish.cat.frame.CatFrameConfig
import org.sereinfish.cat.frame.context.getOrElse
import org.sereinfish.cat.frame.event.handler.EventHandler
import org.sereinfish.cat.frame.event.handler.FilterInvokerChain
import org.sereinfish.cat.frame.event.handler.SimpleEventHandler
import org.sereinfish.cat.frame.event.invoker.InvokerChain
import org.sereinfish.cat.frame.utils.creatContextScope
import org.sereinfish.cat.frame.utils.logger
import java.util.Vector
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.PriorityBlockingQueue

/**
 * 事件管理器
 *
 * 1. 进行扫包完成事件类加载
 * 2. 分发事件数据
 */
object EventManager {
    // 事件处理协程
    private val eventManagerSpace = creatContextScope()
    private val logger = logger()

    // 事件处理器优先级列表
    private val eventHandlerList = EventHandlerChain<Event, EventHandlerContext<Event>>()
    // 事件队列
    private val eventQueue = LinkedBlockingQueue<Event>()

    private val threadCount = CatFrameConfig.getOrElse("frame.event.handler.threadCount"){ 4 }

    private val threads = Vector<Deferred<Unit>>()

    /**
     * 控制事件处理器是否关闭
     */
    private var _isCloseFlag = false

    private var isInit = false

    /**
     * 初始化事件处理线程，进入事件处理
     */
    fun init() {
        if (isInit) return

        // 初始化事件处理线程
        initHandlerThread()

        isInit = true
    }

    /**
     * 初始化事件处理线程
     */
    private fun initHandlerThread(){
        repeat(threadCount){
            threads.add(eventManagerSpace.async {
                while (_isCloseFlag.not()){
                    try {
                        eventHandler(eventQueue.take().also {
                            logger.info(it.toLogString())
                        })
                    }catch (e: Exception){
                        logger.error("事件处理异常", e)
                    }
                }
            })
        }
        runBlocking {
            threads.awaitAll()
        }
    }

    /**
     * 注册事件处理器，按优先级排序
     */
    fun <E: Event, C: EventHandlerContext<E>> registerHandler(handler: EventHandler<E, C>){
        eventHandlerList.add(handler as EventHandler<Event, EventHandlerContext<Event>>)
    }

    /**
     * 广播事件
     */
    fun broadcast(event: Event){
        eventQueue.add(event)
    }

    /**
     * 事件处理
     */
    private suspend fun eventHandler(event: Event){
        for (handler in eventHandlerList){
            val context = handler.getContext(event)
            // 过滤处理器
            handler.filter(context)
            if ((context.result == true).not())
                continue

            // 执行事件处理器
            handler.invoke(context)

            // 终止事件处理
            if (context.stopHandler) {
                break
            }
        }
    }

    /**
     * 关闭事件处理器运行
     */
    fun close(){
        _isCloseFlag = false
    }
}