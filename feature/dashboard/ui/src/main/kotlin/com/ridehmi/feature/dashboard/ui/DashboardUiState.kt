package com.ridehmi.feature.dashboard.ui

/** Display-ready state. Formatting decisions live in the mapper, not in composables. */
sealed interface DashboardUiState {
    data object Connecting : DashboardUiState
    // Phase 3: Content (speed text, motor pill, kill-switch indicator, ride state) and SignalLost.
}
