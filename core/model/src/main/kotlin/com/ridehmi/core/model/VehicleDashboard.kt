package com.ridehmi.core.model

sealed interface VehicleDashboard {
    /** No reading has arrived yet. */
    data object Connecting : VehicleDashboard

    data class Live(val status: VehicleStatus, val rideState: RideState) : VehicleDashboard

    /** Readings stopped arriving, so the last values can no longer be trusted. */
    data object SignalLost : VehicleDashboard
}
