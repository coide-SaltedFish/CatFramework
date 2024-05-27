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
import java.util.concurrent.PriorityBlockingQueue

class FilterInvokerChain<I: Invoker<I, C>, C: InvokerContext>:
    PriorityBlockingQueue<I>(
        11,
        Comparator<I> { e1, e2 ->
            e1.level - e2.level  // 数越大越后面
        }
    )
{

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
        return super.add(element)
    }

    override fun addAll(elements: Collection<I>): Boolean {
        return super.addAll(elements)
    }
}

fun main() {
    val url =           "https://api.tracker.gg/api/v2/bf1/standard/matches/origin/SaltedFishSpri?"
    val matchUrl =      "https://api.tracker.gg/api/v2/bf1/standard/matches/5-1779134241818630976"
//    val UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36 Edg/124.0.0.0"
    val UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36 Edg/124.0.0.0"
    val cookie = "session_id=2c68ac6b-97c9-49bf-9cd7-fc7a7a6eef6f; X-Mapping-Server=s20; __cf_bm=F7pLhQJcXNmEyiC9L8pXQd3IpPJTA2qSnec1gK01UtQ-1715055202-1.0.1.1-iFwhznnYXl2SzK1BYVvTFp7aFrD7MAvXibgE9xK2q6ssuS4OVeOy9DgDuJ7ABHSpT1Rcn_eg8MUqKv0IXttIdICYT7D9aGr8QjThIpgNc5c; __cflb=02DiuFQAkRrzD1P1mdm8JatZXtAyjoPD2XqofMUFCSj9v"

    val client = OkHttpClient.Builder()
        .protocols(listOf(okhttp3.Protocol.HTTP_1_1))
        .proxy(Proxy(Proxy.Type.HTTP, InetSocketAddress("127.0.0.1", 10809)))
        .build()
    val request = Request.Builder()
        // 设置cookie
//        .addHeader("Cookie", cookie)
        .addHeader("Accept", "application/json, text/plain, */*")
//        .addHeader("Accept_Encoding", "gzip, deflate, br, zstd")
        .addHeader("User-Agent", UA)
        .url(matchUrl)
        .build()
    val response = client.newCall(request).execute()
    println(response.body?.string())
}