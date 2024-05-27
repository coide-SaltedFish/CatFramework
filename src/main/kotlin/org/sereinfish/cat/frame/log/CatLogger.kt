package org.sereinfish.cat.frame.log

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.PrintStream

object CatLogger {
    /**
     * 初始化日志
     */
    fun init() {
        LoggerFactory.getILoggerFactory()
        System.setOut(createInfoLoggerPrint(System.out))
        System.setErr(createErrorLoggerPrint(System.err))
    }


    private fun createErrorLoggerPrint(realPrintStream: PrintStream) = object : CatPrintStream(realPrintStream) {
        override val logger: Logger = LoggerFactory.getLogger("STDERR")

        override fun logger(any: Any?) {
            logger.error(any.toString())
        }
    }

    private fun createInfoLoggerPrint(realPrintStream: PrintStream) = object : CatPrintStream(realPrintStream) {
        override val logger: Logger = LoggerFactory.getLogger("STDOUT")

        override fun logger(any: Any?) {
            logger.info(any.toString())
        }
    }

    private abstract class CatPrintStream(realPrintStream: PrintStream): PrintStream(realPrintStream) {
        abstract val logger: Logger

        abstract fun logger(any: Any?)

        override fun print(b: Boolean) {
            logger(b)
        }

        override fun print(c: Char) {
            logger(c)
        }

        override fun print(i: Int) {
            logger(i)
        }

        override fun print(l: Long) {
            logger(l)
        }

        override fun print(f: Float) {
            logger(f)
        }

        override fun print(d: Double) {
            logger(d)
        }

        override fun print(s: CharArray) {
            logger(s)
        }

        override fun print(s: String?) {
            logger(s)
        }

        override fun print(obj: Any?) {
            logger(obj)
        }

        override fun println() {
            logger("")
        }

        override fun println(x: Boolean) {
            logger(x)
        }

        override fun println(x: Char) {
            logger(x)
        }

        override fun println(x: Int) {
            logger(x)
        }

        override fun println(x: Long) {
            logger(x)
        }

        override fun println(x: Float) {
            logger(x)
        }

        override fun println(x: Double) {
            logger(x)
        }

        override fun println(x: CharArray) {
            logger(x)
        }

        override fun println(x: String?) {
            logger(x)
        }

        override fun println(x: Any?) {
            logger(x)
        }
    }
}