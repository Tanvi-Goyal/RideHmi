package com.ridehmi.core.testing.data

import com.ridehmi.core.model.KillSwitchState
import com.ridehmi.core.model.MotorState
import com.ridehmi.core.model.VehicleReading

fun reading(
    speedKmh: Float? = 0f,
    motor: MotorState? = MotorState.OFF,
    killSwitch: KillSwitchState? = KillSwitchState.INACTIVE,
    timestampMillis: Long = 0,
) = VehicleReading(speedKmh, motor, killSwitch, timestampMillis)
