package org.sereinfish.cat.frame.context

/**
 * 类型解析器
 */
interface TypeParser<T> {
    /**
     * 类型是否匹配
     */
    fun match(any: Any, output: Class<*>): Boolean

    /**
     * 类型解析
     */
    fun cast(any: Any): T
}