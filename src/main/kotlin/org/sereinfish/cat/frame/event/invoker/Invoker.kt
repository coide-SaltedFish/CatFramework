package org.sereinfish.cat.frame.event.invoker

interface Invoker<I: Invoker<I, C>, C: InvokerContext> {
    val level: Int // 执行器优先级，决定执行顺序, 数越大越后面

    val preProcess: InvokerChain<I, C>
    val postProcess: InvokerChain<I, C>
    val exceptionHandle: InvokerChain<I, C>

    /**
     * 执行
     */
    suspend fun invoke(context: C){
        try {
            preProcess.invoke(context)
            process(context)
            postProcess.invoke(context)
        }catch (e: Throwable) {
            if (exceptionHandle.isNotEmpty()) {
                context.exception = e
                exceptionHandle.invoke(context)
            } else throw e
        }
    }

    /**
     * 需要执行的代码
     */
    suspend fun process(context: C)
}