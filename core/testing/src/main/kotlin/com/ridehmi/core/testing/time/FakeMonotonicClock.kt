package com.ridehmi.core.testing.time

import com.ridehmi.core.model.MonotonicClock

class FakeMonotonicClock(var now: Long = 0L) : MonotonicClock {
    override fun nowMillis(): Long = now

    fun advanceBy(millis: Long) {
        now += millis
    }
}
