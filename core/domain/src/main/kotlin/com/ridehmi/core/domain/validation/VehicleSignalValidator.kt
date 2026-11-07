package com.ridehmi.core.domain.validation

import com.ridehmi.core.domain.validation.VehicleLimits.MAX_SPEED_KMH
import com.ridehmi.core.domain.validation.VehicleLimits.SPEED_NOISE_FLOOR_KMH
import com.ridehmi.core.model.InvalidReason
import com.ridehmi.core.model.Signal
import com.ridehmi.core.model.Speed
import com.ridehmi.core.model.VehicleReading
import com.ridehmi.core.model.VehicleStatus
import javax.inject.Inject

class VehicleSignalValidator @Inject constructor() {

    fun validate(reading: VehicleReading) = VehicleStatus(
        speed = speed(reading.speedKmh),
        motor = reading.motor?.let { Signal.Valid(it) } ?: Signal.Missing,
        killSwitch = reading.killSwitch?.let { Signal.Valid(it) } ?: Signal.Missing,
    )

    private fun speed(kmh: Float?): Signal<Speed> = when {
        kmh == null -> Signal.Missing
        !kmh.isFinite() -> Signal.Invalid(InvalidReason.NOT_A_NUMBER)
        kmh < SPEED_NOISE_FLOOR_KMH -> Signal.Invalid(InvalidReason.NEGATIVE)
        kmh > MAX_SPEED_KMH -> Signal.Invalid(InvalidReason.ABOVE_PLAUSIBLE_MAX)
        kmh <= 0f -> Signal.Valid(Speed(0f))
        else -> Signal.Valid(Speed(kmh))
    }
}
