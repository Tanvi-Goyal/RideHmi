package com.ridehmi.core.domain.usecase

import com.ridehmi.core.domain.repository.VehicleRepository
import com.ridehmi.core.domain.statemachine.RideStateMachine
import com.ridehmi.core.domain.validation.VehicleSignalValidator
import com.ridehmi.core.model.VehicleDashboard
import com.ridehmi.core.model.valueOrNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.transformLatest
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

class ObserveVehicleDashboard @Inject constructor(
    private val repository: VehicleRepository,
    private val validator: VehicleSignalValidator,
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<VehicleDashboard> = liveReadings()
        .retryWhen { _, _ ->
            emit(VehicleDashboard.SignalLost)
            delay(RETRY_DELAY)
            true
        }
        .onStart { emit(VehicleDashboard.Connecting) }
        .transformLatest { dashboard ->
            emit(dashboard)
            if (dashboard != VehicleDashboard.SignalLost) {
                delay(STALE_AFTER)
                emit(VehicleDashboard.SignalLost)
            }
        }
        .distinctUntilChanged()

    private fun liveReadings(): Flow<VehicleDashboard> = flow {
        var lastTimestamp = Long.MIN_VALUE
        var lastSpeedKmh: Float? = null

        repository.observeReadings().collect { reading ->
            if (reading.timestampMillis <= lastTimestamp) return@collect
            lastTimestamp = reading.timestampMillis

            val status = validator.validate(reading)
            val rideState = RideStateMachine.next(lastSpeedKmh, status)
            lastSpeedKmh = status.speed.valueOrNull()?.kmh

            emit(VehicleDashboard.Live(status, rideState))
        }
    }

    companion object {
        val STALE_AFTER = 3.seconds
        val RETRY_DELAY = 1.seconds
    }
}
