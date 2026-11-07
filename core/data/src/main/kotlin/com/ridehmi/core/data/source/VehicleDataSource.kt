package com.ridehmi.core.data.source

import com.ridehmi.core.model.VehicleReading
import kotlinx.coroutines.flow.Flow

internal interface VehicleDataSource {
    fun readings(): Flow<VehicleReading>
}
