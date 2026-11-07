package com.ridehmi.core.model

sealed interface Signal<out T> {
    data class Valid<out T>(val value: T) : Signal<T>
    data object Missing : Signal<Nothing>
    data class Invalid(val reason: InvalidReason) : Signal<Nothing>
}

enum class InvalidReason { NOT_A_NUMBER, NEGATIVE, ABOVE_PLAUSIBLE_MAX }
