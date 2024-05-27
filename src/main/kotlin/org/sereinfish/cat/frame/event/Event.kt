package org.sereinfish.cat.frame.event

interface Event {

    fun toLogString(): String = "Event: ${this::class.java.simpleName}"
}