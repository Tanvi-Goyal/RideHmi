package com.ridehmi.core.domain.validation

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

data class VehicleLimits(
    val maxPlausibleSpeedKmh: Float = 150f,
    val staleAfter: Duration = 3.seconds,
)
