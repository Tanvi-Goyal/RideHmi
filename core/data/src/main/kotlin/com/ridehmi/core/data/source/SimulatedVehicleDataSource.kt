package com.ridehmi.core.data.source

import com.ridehmi.core.model.MonotonicClock
import com.ridehmi.core.model.VehicleReading
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject

internal class SimulatedVehicleDataSource @Inject constructor(
    private val clock: MonotonicClock,
) : VehicleDataSource {

    // Phase 1: play the ride scenario script, one reading about every second.
    override fun readings(): Flow<VehicleReading> = emptyFlow()
}
