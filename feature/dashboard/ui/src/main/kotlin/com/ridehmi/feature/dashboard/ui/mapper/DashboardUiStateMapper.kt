package com.ridehmi.feature.dashboard.ui.mapper

import com.ridehmi.core.model.VehicleDashboard
import com.ridehmi.feature.dashboard.ui.DashboardUiState

internal fun VehicleDashboard.toUiState(): DashboardUiState = when (this) {
    VehicleDashboard.Connecting -> DashboardUiState.Connecting
    is VehicleDashboard.Live -> TODO("Phase 3")
    VehicleDashboard.SignalLost -> TODO("Phase 3")
}
