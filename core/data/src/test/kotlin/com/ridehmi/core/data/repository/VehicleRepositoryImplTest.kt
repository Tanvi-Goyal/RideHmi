package com.ridehmi.core.data.repository

import com.ridehmi.core.data.source.VehicleDataSource
import com.ridehmi.core.model.KillSwitchState
import com.ridehmi.core.model.MotorState
import com.ridehmi.core.model.VehicleReading
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals

class VehicleRepositoryImplTest {

    @Test
    fun `passes readings through unchanged, bad values included`() = runTest {
        val readings = listOf(
            VehicleReading(25f, MotorState.ON, KillSwitchState.INACTIVE, timestampMillis = 0),
            VehicleReading(-5f, null, KillSwitchState.ACTIVE, timestampMillis = 1_000),
            VehicleReading(null, MotorState.OFF, null, timestampMillis = 2_000),
        )
        val source = object : VehicleDataSource {
            override fun readings() = flowOf(*readings.toTypedArray())
        }
        val repository = VehicleRepositoryImpl(
            source,
            StandardTestDispatcher(testScheduler)
        )

        assertEquals(readings, repository.observeReadings().toList())
    }
}
