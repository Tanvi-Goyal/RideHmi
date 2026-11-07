package com.ridehmi.core.data.repository

import com.ridehmi.core.data.source.VehicleDataSource
import com.ridehmi.core.domain.dispatcher.Dispatcher
import com.ridehmi.core.domain.dispatcher.HmiDispatchers
import com.ridehmi.core.domain.repository.VehicleRepository
import com.ridehmi.core.model.VehicleReading
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

internal class VehicleRepositoryImpl @Inject constructor(
    private val dataSource: VehicleDataSource,
    @param:Dispatcher(HmiDispatchers.Default) private val dispatcher: CoroutineDispatcher,
) : VehicleRepository {

    override fun observeReadings(): Flow<VehicleReading> =
        dataSource.readings().flowOn(dispatcher)
}
