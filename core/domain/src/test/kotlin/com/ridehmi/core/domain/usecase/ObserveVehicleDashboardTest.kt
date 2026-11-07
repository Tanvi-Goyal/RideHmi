package com.ridehmi.core.domain.usecase

import app.cash.turbine.test
import com.ridehmi.core.domain.validation.VehicleSignalValidator
import com.ridehmi.core.model.KillSwitchState
import com.ridehmi.core.model.MotorState
import com.ridehmi.core.model.RideState
import com.ridehmi.core.model.VehicleDashboard
import com.ridehmi.core.model.valueOrNull
import com.ridehmi.core.testing.data.reading
import com.ridehmi.core.testing.repository.FakeVehicleRepository
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ObserveVehicleDashboardTest {

    private val repository = FakeVehicleRepository()
    private val observeDashboard = ObserveVehicleDashboard(repository, VehicleSignalValidator())

    private fun VehicleDashboard.rideState() = assertIs<VehicleDashboard.Live>(this).rideState

    @Test
    fun `starts with Connecting`() = runTest {
        observeDashboard().test {
            assertEquals(VehicleDashboard.Connecting, awaitItem())
        }
    }

    @Test
    fun `plays the five scenarios as their ride states`() = runTest {
        observeDashboard().test {
            awaitItem() // Connecting

            repository.emit(reading(0f, MotorState.OFF, timestampMillis = 1_000))
            assertEquals(RideState.PARKED, awaitItem().rideState())

            repository.emit(reading(0f, MotorState.ON, timestampMillis = 2_000))
            assertEquals(RideState.READY, awaitItem().rideState())

            repository.emit(reading(25f, MotorState.ON, timestampMillis = 3_000))
            assertEquals(RideState.ACCELERATING, awaitItem().rideState())

            repository.emit(reading(25f, MotorState.ON, timestampMillis = 4_000))
            assertEquals(RideState.RIDING, awaitItem().rideState())

            repository.emit(reading(50f, MotorState.ON, timestampMillis = 5_000))
            assertEquals(RideState.ACCELERATING, awaitItem().rideState())

            repository.emit(
                reading(
                    0f,
                    MotorState.OFF,
                    KillSwitchState.ACTIVE,
                    timestampMillis = 6_000
                )
            )
            assertEquals(RideState.MOTOR_DISABLED, awaitItem().rideState())
        }
    }

    @Test
    fun `shows SignalLost when no reading arrives for 3 s, then recovers`() = runTest {
        observeDashboard().test {
            awaitItem() // Connecting
            repository.emit(reading(timestampMillis = 1_000))
            awaitItem() // Live
            val lastReadingAt = testScheduler.currentTime

            assertEquals(VehicleDashboard.SignalLost, awaitItem())
            assertEquals(3_000L, testScheduler.currentTime - lastReadingAt)

            repository.emit(reading(timestampMillis = 5_000))
            assertEquals(RideState.PARKED, awaitItem().rideState())
        }
    }

    @Test
    fun `a reading just before the timeout keeps it live`() = runTest {
        observeDashboard().test {
            awaitItem() // Connecting
            repository.emit(reading(0f, timestampMillis = 1_000))
            awaitItem()

            testScheduler.advanceTimeBy(2_999)
            repository.emit(reading(0f, MotorState.ON, timestampMillis = 4_000))

            assertEquals(RideState.READY, awaitItem().rideState())
        }
    }

    @Test
    fun `shows SignalLost when no reading ever arrives`() = runTest {
        observeDashboard().test {
            assertEquals(VehicleDashboard.Connecting, awaitItem())
            assertEquals(VehicleDashboard.SignalLost, awaitItem())
            assertEquals(3_000L, testScheduler.currentTime)
        }
    }

    @Test
    fun `ignores late and repeated readings`() = runTest {
        observeDashboard().test {
            awaitItem() // Connecting
            repository.emit(reading(25f, MotorState.ON, timestampMillis = 2_000))
            awaitItem()

            repository.emit(reading(40f, MotorState.ON, timestampMillis = 1_000)) // late
            repository.emit(reading(40f, MotorState.ON, timestampMillis = 2_000)) // repeated
            repository.emit(reading(30f, MotorState.ON, timestampMillis = 3_000))

            val next = assertIs<VehicleDashboard.Live>(awaitItem())
            assertEquals(30f, next.status.speed.valueOrNull()?.kmh)
        }
    }

    @Test
    fun `a failing source shows SignalLost, then reconnects`() = runTest {
        observeDashboard().test {
            awaitItem() // Connecting
            repository.emit(reading(timestampMillis = 1_000))
            awaitItem() // Live

            repository.fail()
            assertEquals(VehicleDashboard.SignalLost, awaitItem())

            repository.emit(reading(0f, MotorState.ON, timestampMillis = 2_000))
            assertEquals(RideState.READY, awaitItem().rideState())
        }
    }

    @Test
    fun `identical readings don't emit twice`() = runTest {
        observeDashboard().test {
            awaitItem() // Connecting
            repository.emit(reading(timestampMillis = 1_000))
            awaitItem()

            testScheduler.advanceTimeBy(2_000)
            repository.emit(reading(timestampMillis = 2_000))

            // Next event is the timeout from the second reading, not a duplicate Live.
            assertEquals(VehicleDashboard.SignalLost, awaitItem())
            assertEquals(5_000L, testScheduler.currentTime)
        }
    }
}
