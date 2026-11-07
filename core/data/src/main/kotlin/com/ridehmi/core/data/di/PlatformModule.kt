package com.ridehmi.core.data.di

import android.os.SystemClock
import com.ridehmi.core.domain.dispatcher.Dispatcher
import com.ridehmi.core.domain.dispatcher.HmiDispatchers
import com.ridehmi.core.model.MonotonicClock
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@InstallIn(SingletonComponent::class)
@Module
internal object PlatformModule {

    @Provides
    @Dispatcher(HmiDispatchers.Default)
    fun providesDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @Provides
    fun providesMonotonicClock(): MonotonicClock = MonotonicClock {
        SystemClock.elapsedRealtime()
    }
}
