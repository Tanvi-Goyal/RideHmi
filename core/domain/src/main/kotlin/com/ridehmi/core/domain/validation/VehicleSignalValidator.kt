package com.ridehmi.core.domain.validation

import com.ridehmi.core.model.VehicleReading
import com.ridehmi.core.model.VehicleStatus
import javax.inject.Inject

class VehicleSignalValidator @Inject constructor() {

    fun validate(reading: VehicleReading, limits: VehicleLimits): VehicleStatus =
        TODO("Phase 2: map each raw signal to Valid, Missing or Invalid")
}
