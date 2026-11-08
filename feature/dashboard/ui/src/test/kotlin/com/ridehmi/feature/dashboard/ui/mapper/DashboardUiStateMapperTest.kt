package com.ridehmi.feature.dashboard.ui.mapper

import com.ridehmi.core.model.InvalidReason
import com.ridehmi.core.model.KillSwitchState
import com.ridehmi.core.model.MotorState
import com.ridehmi.core.model.RideState
import com.ridehmi.core.model.Signal
import com.ridehmi.core.model.Speed
import com.ridehmi.core.model.VehicleDashboard
import com.ridehmi.core.model.VehicleStatus
import com.ridehmi.feature.dashboard.ui.DashboardUiState
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class DashboardUiStateMapperTest {

    private fun live(
        speed: Signal<Speed> = Signal.Valid(Speed(25f)),
        motor: Signal<MotorState> = Signal.Valid(MotorState.ON),
        killSwitch: Signal<KillSwitchState> = Signal.Valid(KillSwitchState.INACTIVE),
        rideState: RideState = RideState.RIDING,
    ) = assertIs<DashboardUiState.Ride>(
        VehicleDashboard.Live(VehicleStatus(speed, motor, killSwitch), rideState).toUiState(),
    )

    private fun speedShownFor(kmh: Float) = live(speed = Signal.Valid(Speed(kmh))).speedKmh

    @Test
    fun `live data shows every value`() {
        assertEquals(
            DashboardUiState.Ride(
                speedKmh = 25,
                rideState = RideState.RIDING,
                motor = MotorState.ON,
                killSwitch = KillSwitchState.INACTIVE,
                signalLost = false,
            ),
            live(),
        )
    }

    @Test
    fun `speed is rounded to the nearest km per h`() {
        assertEquals(25, speedShownFor(24.6f))
        assertEquals(24, speedShownFor(24.4f))
        assertEquals(0, speedShownFor(0.4f))
        assertEquals(150, speedShownFor(150f))
    }

    @Test
    fun `speed we can't trust is not shown`() {
        assertEquals(null, live(speed = Signal.Missing).speedKmh)
        assertEquals(null, live(speed = Signal.Invalid(InvalidReason.NEGATIVE)).speedKmh)
    }

    @Test
    fun `a missing motor or kill switch is not shown, the rest still is`() {
        val state =
            live(motor = Signal.Missing, killSwitch = Signal.Missing, rideState = RideState.UNKNOWN)

        assertEquals(null, state.motor)
        assertEquals(null, state.killSwitch)
        assertEquals(25, state.speedKmh)
    }

    @Test
    fun `signal lost hides every value and shows the warning`() {
        assertEquals(
            DashboardUiState.Ride(
                speedKmh = null,
                rideState = RideState.UNKNOWN,
                motor = null,
                killSwitch = null,
                signalLost = true,
            ),
            VehicleDashboard.SignalLost.toUiState(),
        )
    }

    @Test
    fun `connecting stays connecting`() {
        assertEquals(DashboardUiState.Connecting, VehicleDashboard.Connecting.toUiState())
    }
}
