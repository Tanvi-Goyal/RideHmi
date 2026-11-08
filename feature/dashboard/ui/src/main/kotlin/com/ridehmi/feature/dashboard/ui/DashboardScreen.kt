package com.ridehmi.feature.dashboard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ridehmi.core.designsystem.R
import com.ridehmi.core.designsystem.theme.HmiTheme
import com.ridehmi.core.model.KillSwitchState
import com.ridehmi.core.model.MotorState
import com.ridehmi.core.model.RideState
import com.ridehmi.feature.dashboard.ui.components.KillSwitchIndicator
import com.ridehmi.feature.dashboard.ui.components.MotorIndicator
import com.ridehmi.feature.dashboard.ui.components.RideStateBadge
import com.ridehmi.feature.dashboard.ui.components.SignalLostWarning
import com.ridehmi.feature.dashboard.ui.components.SpeedReadout

@Composable
fun DashboardRoute(
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    DashboardScreen(state = state, modifier = modifier)
}

@Composable
internal fun DashboardScreen(
    state: DashboardUiState,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
            .padding(24.dp),
    ) {
        when (state) {
            DashboardUiState.Connecting -> Text(
                text = stringResource(R.string.dashboard_connecting),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.Center),
            )

            is DashboardUiState.Ride -> RideContent(state)
        }
    }
}

@Composable
private fun BoxScope.RideContent(state: DashboardUiState.Ride) {
    RideStateBadge(state.rideState, Modifier.align(Alignment.TopCenter))
    SpeedReadout(state.speedKmh, Modifier.align(Alignment.Center))
    KillSwitchIndicator(state.killSwitch, Modifier.align(Alignment.TopEnd))
    MotorIndicator(state.motor, Modifier.align(Alignment.BottomEnd))
    if (state.signalLost) {
        SignalLostWarning(Modifier.align(Alignment.BottomCenter))
    }
}

private class DashboardPreviewStates : PreviewParameterProvider<DashboardUiState> {
    override val values = sequenceOf(
        DashboardUiState.Connecting,
        ride(
            0,
            RideState.PARKED,
            MotorState.OFF,
            KillSwitchState.INACTIVE
        ),
        ride(
            0,
            RideState.READY,
            MotorState.ON,
            KillSwitchState.INACTIVE
        ),
        ride(
            25,
            RideState.RIDING,
            MotorState.ON,
            KillSwitchState.INACTIVE
        ),
        ride(
            50,
            RideState.ACCELERATING,
            MotorState.ON,
            KillSwitchState.INACTIVE
        ),
        ride(
            0,
            RideState.MOTOR_DISABLED,
            MotorState.OFF,
            KillSwitchState.ACTIVE
        ),
        DashboardUiState.Ride(
            null,
            RideState.UNKNOWN,
            null,
            null,
            signalLost = true
        ),
    )

    private fun ride(
        speed: Int, state: RideState,
        motor: MotorState,
        killSwitch: KillSwitchState
    ) = DashboardUiState.Ride(
            speed,
            state, motor,
            killSwitch, signalLost = false
        )
}

@Preview(widthDp = 960, heightDp = 540)
@Composable
private fun DashboardScreenPreview(
    @PreviewParameter(DashboardPreviewStates::class) state: DashboardUiState,
) {
    HmiTheme { DashboardScreen(state = state) }
}
