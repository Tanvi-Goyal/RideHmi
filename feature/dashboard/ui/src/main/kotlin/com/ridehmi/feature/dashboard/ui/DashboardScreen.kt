package com.ridehmi.feature.dashboard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ridehmi.core.designsystem.R
import com.ridehmi.core.designsystem.theme.HmiTextStyles
import com.ridehmi.core.designsystem.theme.HmiTheme
import com.ridehmi.core.model.KillSwitchState
import com.ridehmi.core.model.MotorState
import com.ridehmi.core.model.RideState
import com.ridehmi.feature.dashboard.ui.components.KillSwitchAlert
import com.ridehmi.feature.dashboard.ui.components.MotorPill
import com.ridehmi.feature.dashboard.ui.components.RidePod
import com.ridehmi.feature.dashboard.ui.components.SignalLostAlert
import com.ridehmi.feature.dashboard.ui.components.SpeedDeck

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
            .background(MaterialTheme.colorScheme.background),
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
private fun RideContent(state: DashboardUiState.Ride) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val height = maxHeight

        SpeedDeck(
            speedKmh = state.speedKmh,
            rideState = state.rideState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 84.dp)
                .fillMaxWidth()
                .fillMaxHeight(0.6f),
        )

        RidePod(
            state.rideState,
            Modifier
                .align(Alignment.TopCenter)
                .padding(top = 12.dp)
        )

        SpeedReadout(
            speedKmh = state.speedKmh,
            height = height,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = height * 0.15f),
        )

        Box(
            Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 32.dp, vertical = 24.dp),
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 24.dp),
                verticalArrangement = Arrangement.spacedBy(28.dp),
            ) {
                KillSwitchAlert(state.killSwitch)
                if (state.signalLost) SignalLostAlert()
            }
            MotorPill(state.motor, Modifier.align(Alignment.BottomEnd))
        }
    }
}

@Composable
private fun SpeedReadout(speedKmh: Int?, height: Dp, modifier: Modifier = Modifier) {
    val fontSize = with(LocalDensity.current) { (height * 0.36f).toSp() }
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = speedKmh?.toString() ?: stringResource(R.string.value_unknown),
            style = HmiTextStyles.SpeedValue.copy(
                fontSize = fontSize,
                lineHeight = fontSize,
                letterSpacing = if (speedKmh == null) 12.sp
                else HmiTextStyles.SpeedValue.letterSpacing,
            ),
            color = MaterialTheme.colorScheme.onBackground,
        )

        Text(
            text = stringResource(R.string.unit_kmh),
            style = HmiTextStyles.SpeedUnit,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.offset(y = -(height * 0.04f)),
        )
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
        speed: Int,
        state: RideState,
        motor: MotorState,
        killSwitch: KillSwitchState
    ) = DashboardUiState.Ride(
        speed,
        state,
        motor,
        killSwitch,
        signalLost = false
    )
}

@Preview(widthDp = 960, heightDp = 432)
@Composable
private fun DashboardScreenPreview(
    @PreviewParameter(DashboardPreviewStates::class) state: DashboardUiState,
) {
    HmiTheme { DashboardScreen(state = state) }
}
