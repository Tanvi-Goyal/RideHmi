package com.ridehmi.core.domain.statemachine

import com.ridehmi.core.domain.validation.VehicleSignalValidator
import com.ridehmi.core.model.KillSwitchState.ACTIVE
import com.ridehmi.core.model.KillSwitchState.INACTIVE
import com.ridehmi.core.model.KillSwitchState
import com.ridehmi.core.model.MotorState
import com.ridehmi.core.model.MotorState.OFF
import com.ridehmi.core.model.MotorState.ON
import com.ridehmi.core.model.RideState
import com.ridehmi.core.testing.data.reading
import org.junit.Test
import kotlin.test.assertEquals

class RideStateMachineTest {

    private val validator = VehicleSignalValidator()

    private fun stateOf(
        speedKmh: Float?,
        motor: MotorState?,
        killSwitch: KillSwitchState? = INACTIVE,
        previousSpeedKmh: Float? = null,
    ): RideState = RideStateMachine.next(
        previousSpeedKmh,
        validator.validate(reading(speedKmh, motor, killSwitch)),
    )

    @Test
    fun `the five assignment scenarios`() {
        assertEquals(RideState.PARKED, stateOf(0f, OFF))
        assertEquals(RideState.READY, stateOf(0f, ON))
        assertEquals(RideState.RIDING, stateOf(25f, ON, previousSpeedKmh = 25f))
        assertEquals(RideState.ACCELERATING, stateOf(50f, ON, previousSpeedKmh = 45f))
        assertEquals(RideState.MOTOR_DISABLED, stateOf(0f, OFF, ACTIVE))
    }

    @Test
    fun `kill switch wins over everything, even while moving or with bad data`() {
        assertEquals(RideState.MOTOR_DISABLED, stateOf(30f, ON, ACTIVE))
        assertEquals(RideState.MOTOR_DISABLED, stateOf(null, null, ACTIVE))
    }

    @Test
    fun `any signal we can't trust gives UNKNOWN`() {
        assertEquals(RideState.UNKNOWN, stateOf(null, ON))
        assertEquals(RideState.UNKNOWN, stateOf(-5f, ON))
        assertEquals(RideState.UNKNOWN, stateOf(0f, null))
        assertEquals(RideState.UNKNOWN, stateOf(0f, OFF, killSwitch = null))
    }

    @Test
    fun `accelerating needs a rise of at least 1 km per h`() {
        assertEquals(RideState.ACCELERATING, stateOf(25f, ON, previousSpeedKmh = 0f))
        assertEquals(RideState.ACCELERATING, stateOf(26f, ON, previousSpeedKmh = 25f))
        assertEquals(RideState.RIDING, stateOf(25.5f, ON, previousSpeedKmh = 25f))
        assertEquals(RideState.RIDING, stateOf(20f, ON, previousSpeedKmh = 25f))
    }

    @Test
    fun `without a previous speed it is riding, not accelerating`() {
        assertEquals(RideState.RIDING, stateOf(25f, ON, previousSpeedKmh = null))
    }

    @Test
    fun `motor off while moving is still riding, never parked`() {
        assertEquals(RideState.RIDING, stateOf(20f, OFF, previousSpeedKmh = 20f))
        assertEquals(RideState.ACCELERATING, stateOf(22f, OFF, previousSpeedKmh = 20f))
    }
}
