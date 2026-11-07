package com.ridehmi.core.domain.repository

import com.ridehmi.core.model.VehicleReading
import kotlinx.coroutines.flow.Flow

interface VehicleRepository {
    fun observeReadings(): Flow<VehicleReading>
}
