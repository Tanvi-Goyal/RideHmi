package com.ridehmi.feature.dashboard.ui.mapper

import com.ridehmi.core.model.VehicleFeed
import com.ridehmi.feature.dashboard.ui.DashboardUiState

internal fun VehicleFeed.toUiState(): DashboardUiState = when (this) {
    VehicleFeed.Connecting -> DashboardUiState.Connecting
    is VehicleFeed.Live -> TODO("Phase 3")
    VehicleFeed.SignalLost -> TODO("Phase 3")
}
