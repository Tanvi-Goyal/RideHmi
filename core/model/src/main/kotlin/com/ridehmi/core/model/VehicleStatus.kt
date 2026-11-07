package com.ridehmi.core.model

@JvmInline
value class Speed(val kmh: Float)

enum class MotorState { ON, OFF }

enum class KillSwitchState { ACTIVE, INACTIVE }

data class VehicleStatus(
    val speed: Signal<Speed>,
    val motor: Signal<MotorState>,
    val killSwitch: Signal<KillSwitchState>,
)
