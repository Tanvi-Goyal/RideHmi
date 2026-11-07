package com.ridehmi.core.data.di

import com.ridehmi.core.data.repository.VehicleRepositoryImpl
import com.ridehmi.core.data.source.SimulatedVehicleDataSource
import com.ridehmi.core.data.source.VehicleDataSource
import com.ridehmi.core.domain.repository.VehicleRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
internal abstract class DataModule {

    @Binds
    abstract fun bindsVehicleDataSource(source: SimulatedVehicleDataSource): VehicleDataSource

    @Binds
    abstract fun bindsVehicleRepository(repository: VehicleRepositoryImpl): VehicleRepository
}
