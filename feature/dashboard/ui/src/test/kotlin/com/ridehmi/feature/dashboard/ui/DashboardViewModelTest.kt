package com.ridehmi.feature.dashboard.ui

import app.cash.turbine.test
import com.ridehmi.core.domain.usecase.ObserveVehicleDashboard
import com.ridehmi.core.domain.validation.VehicleSignalValidator
import com.ridehmi.core.testing.repository.FakeVehicleRepository
import com.ridehmi.core.testing.rules.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class DashboardViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeVehicleRepository()

    @Test
    fun `starts in Connecting before any reading arrives`() = runTest {
        val viewModel = DashboardViewModel(ObserveVehicleDashboard(repository, VehicleSignalValidator()))

        viewModel.state.test {
            assertEquals(DashboardUiState.Connecting, awaitItem())
        }
    }
}
