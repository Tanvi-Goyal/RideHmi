package com.ridehmi.feature.dashboard.ui

import com.ridehmi.core.model.KillSwitchState
import com.ridehmi.core.model.MotorState
import com.ridehmi.core.model.RideState

sealed interface DashboardUiState {
    data object Connecting : DashboardUiState

    data class Ride(
        val speedKmh: Int?,
        val rideState: RideState,
        val motor: MotorState?,
        val killSwitch: KillSwitchState?,
        val signalLost: Boolean,
    ) : DashboardUiState
}
