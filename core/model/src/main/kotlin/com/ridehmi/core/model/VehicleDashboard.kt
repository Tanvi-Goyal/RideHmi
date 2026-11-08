package com.ridehmi.core.model

sealed interface VehicleDashboard {
    data object Connecting : VehicleDashboard

    data class Live(val status: VehicleStatus, val rideState: RideState) : VehicleDashboard

    data object SignalLost : VehicleDashboard
}
