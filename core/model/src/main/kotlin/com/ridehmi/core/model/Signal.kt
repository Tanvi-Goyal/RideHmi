package com.ridehmi.core.model

/** A validated signal. The UI must handle every case, so a bad value can never reach the screen. */
sealed interface Signal<out T> {
    data class Valid<out T>(val value: T) : Signal<T>
    data object Missing : Signal<Nothing>
    data class Invalid(val reason: InvalidReason) : Signal<Nothing>
}

enum class InvalidReason { NOT_A_NUMBER, NEGATIVE, ABOVE_PLAUSIBLE_MAX }
