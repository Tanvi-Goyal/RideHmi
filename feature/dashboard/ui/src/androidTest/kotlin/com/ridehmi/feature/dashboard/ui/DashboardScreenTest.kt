package com.ridehmi.feature.dashboard.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.ridehmi.core.designsystem.theme.HmiTheme
import com.ridehmi.core.model.KillSwitchState
import com.ridehmi.core.model.MotorState
import com.ridehmi.core.model.RideState
import org.junit.Rule
import org.junit.Test

class DashboardScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun show(state: DashboardUiState) = composeRule.setContent {
        HmiTheme { DashboardScreen(state) }
    }

    @Test
    fun motorDisabledShowsKillSwitchActiveAndMotorOff() {
        show(
            DashboardUiState.Ride(
                0, RideState.MOTOR_DISABLED,
                MotorState.OFF,
                KillSwitchState.ACTIVE,
                signalLost = false
            )
        )

        composeRule.onNodeWithContentDescription("Kill switch Active").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Motor OFF").assertIsDisplayed()
        composeRule.onNodeWithText("MOTOR DISABLED").assertIsDisplayed()
        composeRule.onNodeWithText("0").assertIsDisplayed()
    }

    @Test
    fun signalLostShowsWarningAndNoValues() {
        show(DashboardUiState.Ride(null, RideState.UNKNOWN, null, null, signalLost = true))

        composeRule.onNodeWithContentDescription("No signal from vehicle").assertIsDisplayed()
        composeRule.onNodeWithText("--").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Kill switch --").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Motor --").assertIsDisplayed()
    }
}
