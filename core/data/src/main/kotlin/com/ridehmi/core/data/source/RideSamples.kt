package com.ridehmi.core.data.source

import com.ridehmi.core.model.KillSwitchState
import com.ridehmi.core.model.MotorState
import com.ridehmi.core.model.VehicleReading

internal data class Sample(
    val speedKmh: Float,
    val motor: MotorState,
    val killSwitch: KillSwitchState,
) {
    fun toReading(timestampMillis: Long) = VehicleReading(
        speedKmh,
        motor,
        killSwitch,
        timestampMillis
    )
}

internal object RideSamples {

    // 17 seconds: Parked, Ready, Riding, Accelerating, Motor disabled.
    // Accelerating ramps up to 50 because a steady speed reads as Riding.
    val normal: List<Sample> = List(3) {
        Sample(0f, MotorState.OFF, KillSwitchState.INACTIVE)
    } + List(3) {
        Sample(0f, MotorState.ON, KillSwitchState.INACTIVE)
    } + List(3) {
        Sample(25f, MotorState.ON, KillSwitchState.INACTIVE)
    } + listOf(30f, 35f, 40f, 45f, 50f).map {
        Sample(
            it,
            MotorState.ON,
            KillSwitchState.INACTIVE
        )
    } + List(3) {
        Sample(0f, MotorState.OFF, KillSwitchState.ACTIVE)
    }
}
