package com.ridehmi.feature.dashboard.ui.mapper

import com.ridehmi.core.model.VehicleDashboard
import com.ridehmi.feature.dashboard.ui.DashboardUiState

internal fun VehicleDashboard.toUiState(): DashboardUiState = when (this) {
    VehicleDashboard.Connecting -> DashboardUiState.Connecting
    is VehicleDashboard.Live -> DashboardUiState.Connecting
    VehicleDashboard.SignalLost -> DashboardUiState.Connecting
}
