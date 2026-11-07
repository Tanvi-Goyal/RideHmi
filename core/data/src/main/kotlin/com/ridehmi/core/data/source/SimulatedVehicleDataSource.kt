package com.ridehmi.core.data.source

import com.ridehmi.core.model.MonotonicClock
import com.ridehmi.core.model.VehicleReading
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

internal class SimulatedVehicleDataSource @Inject constructor(
    private val clock: MonotonicClock,
) : VehicleDataSource {

    override fun readings(): Flow<VehicleReading> = flow {
        while (true) {
            for (sample in RideSamples.normal) {
                // Send first, then wait, so the screen leaves "Connecting" straight away.
                emit(sample.toReading(clock.nowMillis()))
                delay(1.seconds)
            }
        }
    }
}
