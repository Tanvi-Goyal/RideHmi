package com.ridehmi.core.domain.statemachine

import com.ridehmi.core.model.RideState
import com.ridehmi.core.model.VehicleStatus

/** Pure transition function: the next ride state from the current one and a validated status. */
object RideStateMachine {

    fun reduce(current: RideState, status: VehicleStatus): RideState =
        TODO("Phase 2: transition rules, see docs/plan.md")
}
