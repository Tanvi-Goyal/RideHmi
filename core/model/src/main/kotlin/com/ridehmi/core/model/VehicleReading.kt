package com.ridehmi.core.model

/**
 * One raw sample from the vehicle bus, before any validation. A null field means that signal
 * was missing from the sample; a bus can drop one signal while the others still arrive.
 *
 * [timestampMillis] is monotonic time, so it never jumps when the wall clock changes.
 */
data class VehicleReading(
    val speedKmh: Float?,
    val motorOn: Boolean?,
    val killSwitchActive: Boolean?,
    val timestampMillis: Long,
)
