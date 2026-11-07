package com.ridehmi.core.domain.validation

import com.ridehmi.core.model.InvalidReason
import com.ridehmi.core.model.KillSwitchState
import com.ridehmi.core.model.MotorState
import com.ridehmi.core.model.Signal
import com.ridehmi.core.model.Speed
import com.ridehmi.core.testing.data.reading
import org.junit.Test
import kotlin.test.assertEquals

class VehicleSignalValidatorTest {

    private val validator = VehicleSignalValidator()

    private fun speedOf(kmh: Float?) = validator.validate(reading(speedKmh = kmh)).speed

    @Test
    fun `normal speeds are valid`() {
        assertEquals(Signal.Valid(Speed(0f)), speedOf(0f))
        assertEquals(Signal.Valid(Speed(25f)), speedOf(25f))
        assertEquals(Signal.Valid(Speed(150f)), speedOf(150f))
    }

    @Test
    fun `small negative noise counts as 0`() {
        assertEquals(Signal.Valid(Speed(0f)), speedOf(-0.4f))
        assertEquals(Signal.Valid(Speed(0f)), speedOf(-1f))
        assertEquals(Signal.Valid(Speed(0f)), speedOf(-0f))
    }

    @Test
    fun `speed below -1 is invalid`() {
        assertEquals(Signal.Invalid(InvalidReason.NEGATIVE), speedOf(-1.1f))
        assertEquals(Signal.Invalid(InvalidReason.NEGATIVE), speedOf(-5f))
    }

    @Test
    fun `speed above the plausible max is invalid`() {
        assertEquals(Signal.Invalid(InvalidReason.ABOVE_PLAUSIBLE_MAX), speedOf(150.1f))
        assertEquals(Signal.Invalid(InvalidReason.ABOVE_PLAUSIBLE_MAX), speedOf(400f))
    }

    @Test
    fun `speed that is not a number is invalid`() {
        assertEquals(Signal.Invalid(InvalidReason.NOT_A_NUMBER), speedOf(Float.NaN))
        assertEquals(Signal.Invalid(InvalidReason.NOT_A_NUMBER), speedOf(Float.POSITIVE_INFINITY))
        assertEquals(Signal.Invalid(InvalidReason.NOT_A_NUMBER), speedOf(Float.NEGATIVE_INFINITY))
    }

    @Test
    fun `missing signals are Missing and the others still come through`() {
        val status = validator.validate(reading(speedKmh = null, motor = MotorState.ON, killSwitch = null))

        assertEquals(Signal.Missing, status.speed)
        assertEquals(Signal.Valid(MotorState.ON), status.motor)
        assertEquals(Signal.Missing, status.killSwitch)
    }

    @Test
    fun `motor and kill switch pass through when present`() {
        val status = validator.validate(reading(motor = MotorState.OFF, killSwitch = KillSwitchState.ACTIVE))

        assertEquals(Signal.Valid(MotorState.OFF), status.motor)
        assertEquals(Signal.Valid(KillSwitchState.ACTIVE), status.killSwitch)
    }
}
