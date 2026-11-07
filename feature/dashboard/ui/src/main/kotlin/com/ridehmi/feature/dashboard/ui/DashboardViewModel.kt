package com.ridehmi.feature.dashboard.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ridehmi.core.domain.usecase.ObserveVehicleFeed
import com.ridehmi.feature.dashboard.ui.mapper.toUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    observeVehicleFeed: ObserveVehicleFeed,
) : ViewModel() {

    val state: StateFlow<DashboardUiState> = observeVehicleFeed()
        .map { it.toUiState() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DashboardUiState.Connecting,
        )
}
