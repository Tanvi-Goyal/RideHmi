package com.ridehmi.core.domain.statemachine

import com.ridehmi.core.domain.validation.VehicleLimits.ACCELERATION_STEP_KMH
import com.ridehmi.core.model.KillSwitchState
import com.ridehmi.core.model.MotorState
import com.ridehmi.core.model.RideState
import com.ridehmi.core.model.VehicleStatus
import com.ridehmi.core.model.valueOrNull

object RideStateMachine {

    fun next(previousSpeedKmh: Float?, status: VehicleStatus): RideState {
        val killSwitch = status.killSwitch.valueOrNull()
        val motor = status.motor.valueOrNull()
        val speed = status.speed.valueOrNull()?.kmh

        if (killSwitch == KillSwitchState.ACTIVE) return RideState.MOTOR_DISABLED
        // Don't claim Parked or Ready unless every signal is trusted.
        if (killSwitch == null || motor == null || speed == null) return RideState.UNKNOWN

        return when {
            speed == 0f && motor == MotorState.OFF -> RideState.PARKED
            speed == 0f -> RideState.READY
            previousSpeedKmh != null &&
                    speed - previousSpeedKmh >= ACCELERATION_STEP_KMH -> RideState.ACCELERATING
            else -> RideState.RIDING
        }
    }
}
