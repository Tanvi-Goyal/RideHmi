package com.ridehmi.feature.dashboard.ui

import app.cash.turbine.test
import com.ridehmi.core.domain.usecase.ObserveVehicleDashboard
import com.ridehmi.core.domain.validation.VehicleSignalValidator
import com.ridehmi.core.model.KillSwitchState
import com.ridehmi.core.model.MotorState
import com.ridehmi.core.model.RideState
import com.ridehmi.core.testing.data.reading
import com.ridehmi.core.testing.repository.FakeVehicleRepository
import com.ridehmi.core.testing.rules.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class DashboardViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeVehicleRepository()

    private val viewModel by lazy {
        DashboardViewModel(ObserveVehicleDashboard(repository, VehicleSignalValidator()))
    }

    @Test
    fun `starts in Connecting before any reading arrives`() = runTest {
        viewModel.state.test {
            assertEquals(DashboardUiState.Connecting, awaitItem())
        }
    }

    @Test
    fun `a reading updates the screen without a refresh`() = runTest {
        viewModel.state.test {
            awaitItem() // Connecting

            repository.emit(reading(0f, MotorState.ON, timestampMillis = 1_000))

            assertEquals(
                DashboardUiState.Ride(
                    0,
                    RideState.READY,
                    MotorState.ON,
                    KillSwitchState.INACTIVE, signalLost = false
                ),
                awaitItem(),
            )
        }
    }

    @Test
    fun `an invalid speed shows as unknown without crashing`() = runTest {
        viewModel.state.test {
            awaitItem() // Connecting

            repository.emit(reading(-5f, MotorState.ON, timestampMillis = 1_000))

            val state = assertIs<DashboardUiState.Ride>(awaitItem())
            assertEquals(null, state.speedKmh)
            assertEquals(MotorState.ON, state.motor)
        }
    }

    @Test
    fun `stale data shows signal lost, then recovers`() = runTest {
        viewModel.state.test {
            awaitItem()
            repository.emit(reading(timestampMillis = 1_000))
            awaitItem()

            assertTrue(assertIs<DashboardUiState.Ride>(awaitItem()).signalLost)

            repository.emit(reading(timestampMillis = 5_000))
            assertEquals(
                false,
                assertIs<DashboardUiState.Ride>(awaitItem()).signalLost
            )
        }
    }
}
