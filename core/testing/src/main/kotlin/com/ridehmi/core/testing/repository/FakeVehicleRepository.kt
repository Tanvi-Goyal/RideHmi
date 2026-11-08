package com.ridehmi.core.testing.repository

import com.ridehmi.core.domain.repository.VehicleRepository
import com.ridehmi.core.model.VehicleReading
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class FakeVehicleRepository : VehicleRepository {

    private val readings = MutableSharedFlow<Result<VehicleReading>>(extraBufferCapacity = 64)

    override fun observeReadings(): Flow<VehicleReading> = flow {
        readings.collect { emit(it.getOrThrow()) }
    }

    suspend fun emit(reading: VehicleReading) = send(Result.success(reading))

    suspend fun fail(error: Throwable = IllegalStateException("vehicle bus failed")) =
        send(Result.failure(error))

    private suspend fun send(result: Result<VehicleReading>) {
        readings.subscriptionCount.first { it > 0 }
        readings.emit(result)
    }
}
