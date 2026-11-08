package com.ridehmi.feature.dashboard.ui.mapper

import com.ridehmi.core.model.RideState
import com.ridehmi.core.model.VehicleDashboard
import com.ridehmi.core.model.valueOrNull
import com.ridehmi.feature.dashboard.ui.DashboardUiState
import kotlin.math.roundToInt

internal fun VehicleDashboard.toUiState(): DashboardUiState = when (this) {
    VehicleDashboard.Connecting -> DashboardUiState.Connecting

    is VehicleDashboard.Live -> DashboardUiState.Ride(
        speedKmh = status.speed.valueOrNull()?.kmh?.roundToInt(),
        rideState = rideState,
        motor = status.motor.valueOrNull(),
        killSwitch = status.killSwitch.valueOrNull(),
        signalLost = false,
    )

    VehicleDashboard.SignalLost -> DashboardUiState.Ride(
        speedKmh = null,
        rideState = RideState.UNKNOWN,
        motor = null,
        killSwitch = null,
        signalLost = true,
    )
}
