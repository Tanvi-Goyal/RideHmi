package com.ridehmi.core.domain.usecase

import com.ridehmi.core.domain.repository.VehicleRepository
import com.ridehmi.core.domain.validation.VehicleSignalValidator
import com.ridehmi.core.model.VehicleDashboard
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject

class ObserveVehicleDashboard @Inject constructor(
    private val repository: VehicleRepository,
    private val validator: VehicleSignalValidator,
) {
    // Phase 2: drop out-of-order readings, validate, reduce with RideStateMachine,
    // emit SignalLost when readings stop, and recover when the source fails.
    operator fun invoke(): Flow<VehicleDashboard> = emptyFlow()
}
