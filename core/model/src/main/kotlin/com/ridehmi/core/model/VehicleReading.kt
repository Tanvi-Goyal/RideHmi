package com.ridehmi.core.model

data class VehicleReading(
    val speedKmh: Float?,
    val motor: MotorState?,
    val killSwitch: KillSwitchState?,
    val timestampMillis: Long,
)
