package com.ridehmi.core.data.source

import com.ridehmi.core.model.KillSwitchState.ACTIVE
import com.ridehmi.core.model.KillSwitchState.INACTIVE
import com.ridehmi.core.model.VehicleReading
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals
import com.ridehmi.core.model.MotorState.OFF as MOTOR_OFF
import com.ridehmi.core.model.MotorState.ON as MOTOR_ON

class SimulatedVehicleDataSourceTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun TestScope.source() = SimulatedVehicleDataSource { testScheduler.currentTime }

    private suspend fun SimulatedVehicleDataSource.first(count: Int): List<VehicleReading> =
        readings().take(count).toList()

    @Test
    fun `plays the ride in order`() = runTest {
        val values = source().first(17).map {
            Triple(it.speedKmh, it.motor, it.killSwitch)
        }

        val expected =
            List(3) { Triple(0f, MOTOR_OFF, INACTIVE) } + // Parked
                    List(3) { Triple(0f, MOTOR_ON, INACTIVE) } + // Ready
                    List(3) { Triple(25f, MOTOR_ON, INACTIVE) } + // Riding
                    listOf(30f, 35f, 40f, 45f, 50f).map {
                        Triple(
                            it,
                            MOTOR_ON,
                            INACTIVE
                        )
                    } + // Accelerating
                    List(3) { Triple(0f, MOTOR_OFF, ACTIVE) } // Motor disabled
        assertEquals(expected, values)
    }

    @Test
    fun `sends the first reading at once, then one every second`() = runTest {
        val timestamps = source().first(4).map { it.timestampMillis }

        assertEquals(listOf(0L, 1_000L, 2_000L, 3_000L), timestamps)
    }

    @Test
    fun `starts the ride again after the last sample`() = runTest {
        val readings = source().first(18)

        assertEquals(readings[0].copy(timestampMillis = 17_000L), readings[17])
    }

    @Test
    fun `timestamps come from the clock`() = runTest {
        var now = 5_000L
        val source = SimulatedVehicleDataSource { now++ }

        val timestamps = source.readings().take(3).toList().map { it.timestampMillis }

        assertEquals(listOf(5_000L, 5_001L, 5_002L), timestamps)
    }
}
