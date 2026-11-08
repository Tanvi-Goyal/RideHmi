package com.ridehmi.feature.dashboard.ui

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.platform.app.InstrumentationRegistry
import com.ridehmi.core.designsystem.theme.HmiTheme
import com.ridehmi.core.model.KillSwitchState
import com.ridehmi.core.model.MotorState
import com.ridehmi.core.model.RideState
import org.junit.Rule
import org.junit.Test
import java.io.File

class DashboardScreenshotCapture {

    @get:Rule
    val composeRule = createComposeRule()

    private val looks = listOf(
        "1-parked" to ride(0, RideState.PARKED, MotorState.OFF, KillSwitchState.INACTIVE),
        "2-ready" to ride(0, RideState.READY, MotorState.ON, KillSwitchState.INACTIVE),
        "3-riding" to ride(25, RideState.RIDING, MotorState.ON, KillSwitchState.INACTIVE),
        "4-accelerating" to ride(50, RideState.ACCELERATING, MotorState.ON, KillSwitchState.INACTIVE),
        "5-motor-disabled" to ride(0, RideState.MOTOR_DISABLED, MotorState.OFF, KillSwitchState.ACTIVE),
        "6-signal-lost" to DashboardUiState.Ride(null, RideState.UNKNOWN, null, null, signalLost = true),
        "7-connecting" to DashboardUiState.Connecting,
    )

    @Test
    fun captureEveryLook() {
        var current by mutableStateOf<DashboardUiState>(DashboardUiState.Connecting)
        composeRule.setContent { HmiTheme { DashboardScreen(current) } }

        val dir = File(InstrumentationRegistry.getInstrumentation().targetContext.getExternalFilesDir(null), "screenshots")
        dir.mkdirs()

        looks.forEach { (name, state) ->
            current = state
            composeRule.waitForIdle()
            val bitmap = composeRule.onRoot().captureToImage().asAndroidBitmap()
            File(dir, "$name.png").outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        }
    }

    private fun ride(speed: Int, state: RideState, motor: MotorState, killSwitch: KillSwitchState) =
        DashboardUiState.Ride(speed, state, motor, killSwitch, signalLost = false)
}
