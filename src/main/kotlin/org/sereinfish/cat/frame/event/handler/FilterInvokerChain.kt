package org.sereinfish.cat.frame.event.handler

import okhttp3.OkHttpClient
import okhttp3.Request
import org.sereinfish.cat.frame.event.invoker.Invoker
import org.sereinfish.cat.frame.event.invoker.InvokerContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.InetSocketAddress
import java.net.Proxy
import java.util.Vector
import java.util.concurrent.PriorityBlockingQueue

class FilterInvokerChain<I: Invoker<I, C>, C: InvokerContext>: Vector<I>() {
    /**
     * 执行执行链
     */
    suspend fun invoke(context: C){
        for (filter in this){
            filter.invoke(context)

            if ((context.result == true).not())
                break
        }
    }

    override fun add(element: I): Boolean {
        return super.add(element).also {
            sortBy { it.level }
        }
    }

    override fun addAll(elements: Collection<I>): Boolean {
        return super.addAll(elements).also {
            sortBy { it.level }
        }
    }
}