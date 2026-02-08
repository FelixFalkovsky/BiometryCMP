package com.enumSet.biometry.sample.compose

/**
 * Неизменяемое состояние UI экрана биометрии (удобно для тестов и отладки).
 */
data class BiometrySampleUiState(
    val authSuccess: Boolean = false,
    val authFailure: Boolean = false,
    val sheetVisible: Boolean = false,
    val biometryTypeText: String = "…",
    val isAvailable: Boolean = false
)
