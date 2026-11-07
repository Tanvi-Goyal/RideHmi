package com.ridehmi.core.data.source

import com.ridehmi.core.model.VehicleReading
import kotlinx.coroutines.flow.Flow

/** Where readings come from. The simulator today; a CAN bus adapter could replace it. */
internal interface VehicleDataSource {
    fun readings(): Flow<VehicleReading>
}
