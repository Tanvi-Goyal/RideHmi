package com.ridehmi.core.model

/** What the dashboard knows about the vehicle right now. */
sealed interface VehicleFeed {
    /** No reading has arrived yet. */
    data object Connecting : VehicleFeed

    data class Live(val status: VehicleStatus, val rideState: RideState) : VehicleFeed

    /** Readings stopped arriving, so the last values can no longer be trusted. */
    data object SignalLost : VehicleFeed
}
