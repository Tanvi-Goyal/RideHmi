package com.ridehmi.core.testing.repository

import com.ridehmi.core.domain.repository.VehicleRepository
import com.ridehmi.core.model.VehicleReading
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow

/** Tests push readings with [emit], or make the source throw with [fail]. */
class FakeVehicleRepository : VehicleRepository {

    private val readings = MutableSharedFlow<Result<VehicleReading>>(extraBufferCapacity = 64)

    override fun observeReadings(): Flow<VehicleReading> = flow {
        readings.collect { emit(it.getOrThrow()) }
    }

    suspend fun emit(reading: VehicleReading) = readings.emit(Result.success(reading))

    suspend fun fail(error: Throwable = IllegalStateException("vehicle bus failed")) =
        readings.emit(Result.failure(error))
}
