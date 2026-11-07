package com.ridehmi.core.model

fun interface MonotonicClock {
    fun nowMillis(): Long
}
